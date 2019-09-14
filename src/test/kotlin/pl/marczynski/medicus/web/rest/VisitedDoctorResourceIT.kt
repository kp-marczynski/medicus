package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.MedicusApp
import pl.marczynski.medicus.domain.VisitedDoctor
import pl.marczynski.medicus.repository.VisitedDoctorRepository
import pl.marczynski.medicus.web.rest.errors.ExceptionTranslator

import kotlin.test.assertNotNull

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import javax.persistence.EntityManager

import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Integration tests for the [VisitedDoctorResource] REST controller.
 *
 * @see VisitedDoctorResource
 */
@SpringBootTest(classes = [MedicusApp::class])
class VisitedDoctorResourceIT {

    @Autowired
    private lateinit var visitedDoctorRepository: VisitedDoctorRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var validator: Validator

    private lateinit var restVisitedDoctorMockMvc: MockMvc

    private lateinit var visitedDoctor: VisitedDoctor

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val visitedDoctorResource = VisitedDoctorResource(visitedDoctorRepository)
        this.restVisitedDoctorMockMvc = MockMvcBuilders.standaloneSetup(visitedDoctorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        visitedDoctor = createEntity(em)
    }

    @Test
    @Transactional
    fun createVisitedDoctor() {
        val databaseSizeBeforeCreate = visitedDoctorRepository.findAll().size

        // Create the VisitedDoctor
        restVisitedDoctorMockMvc.perform(
            post("/api/visited-doctors")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(visitedDoctor))
        ).andExpect(status().isCreated)

        // Validate the VisitedDoctor in the database
        val visitedDoctorList = visitedDoctorRepository.findAll()
        assertThat(visitedDoctorList).hasSize(databaseSizeBeforeCreate + 1)
        val testVisitedDoctor = visitedDoctorList[visitedDoctorList.size - 1]
        assertThat(testVisitedDoctor.opinion).isEqualTo(DEFAULT_OPINION)
    }

    @Test
    @Transactional
    fun createVisitedDoctorWithExistingId() {
        val databaseSizeBeforeCreate = visitedDoctorRepository.findAll().size

        // Create the VisitedDoctor with an existing ID
        visitedDoctor.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restVisitedDoctorMockMvc.perform(
            post("/api/visited-doctors")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(visitedDoctor))
        ).andExpect(status().isBadRequest)

        // Validate the VisitedDoctor in the database
        val visitedDoctorList = visitedDoctorRepository.findAll()
        assertThat(visitedDoctorList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun getAllVisitedDoctors() {
        // Initialize the database
        visitedDoctorRepository.saveAndFlush(visitedDoctor)

        // Get all the visitedDoctorList
        restVisitedDoctorMockMvc.perform(get("/api/visited-doctors?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(visitedDoctor.id?.toInt())))
            .andExpect(jsonPath("$.[*].opinion").value(hasItem(DEFAULT_OPINION)))
    }

    @Test
    @Transactional
    fun getVisitedDoctor() {
        // Initialize the database
        visitedDoctorRepository.saveAndFlush(visitedDoctor)

        val id = visitedDoctor.id
        assertNotNull(id)

        // Get the visitedDoctor
        restVisitedDoctorMockMvc.perform(get("/api/visited-doctors/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.opinion").value(DEFAULT_OPINION))
    }

    @Test
    @Transactional
    fun getNonExistingVisitedDoctor() {
        // Get the visitedDoctor
        restVisitedDoctorMockMvc.perform(get("/api/visited-doctors/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateVisitedDoctor() {
        // Initialize the database
        visitedDoctorRepository.saveAndFlush(visitedDoctor)

        val databaseSizeBeforeUpdate = visitedDoctorRepository.findAll().size

        // Update the visitedDoctor
        val id = visitedDoctor.id
        assertNotNull(id)
        val updatedVisitedDoctor = visitedDoctorRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedVisitedDoctor are not directly saved in db
        em.detach(updatedVisitedDoctor)
        updatedVisitedDoctor.opinion = UPDATED_OPINION

        restVisitedDoctorMockMvc.perform(
            put("/api/visited-doctors")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(updatedVisitedDoctor))
        ).andExpect(status().isOk)

        // Validate the VisitedDoctor in the database
        val visitedDoctorList = visitedDoctorRepository.findAll()
        assertThat(visitedDoctorList).hasSize(databaseSizeBeforeUpdate)
        val testVisitedDoctor = visitedDoctorList[visitedDoctorList.size - 1]
        assertThat(testVisitedDoctor.opinion).isEqualTo(UPDATED_OPINION)
    }

    @Test
    @Transactional
    fun updateNonExistingVisitedDoctor() {
        val databaseSizeBeforeUpdate = visitedDoctorRepository.findAll().size

        // Create the VisitedDoctor

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVisitedDoctorMockMvc.perform(
            put("/api/visited-doctors")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(visitedDoctor))
        ).andExpect(status().isBadRequest)

        // Validate the VisitedDoctor in the database
        val visitedDoctorList = visitedDoctorRepository.findAll()
        assertThat(visitedDoctorList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteVisitedDoctor() {
        // Initialize the database
        visitedDoctorRepository.saveAndFlush(visitedDoctor)

        val databaseSizeBeforeDelete = visitedDoctorRepository.findAll().size

        val id = visitedDoctor.id
        assertNotNull(id)

        // Delete the visitedDoctor
        restVisitedDoctorMockMvc.perform(
            delete("/api/visited-doctors/{id}", id)
                .accept(APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val visitedDoctorList = visitedDoctorRepository.findAll()
        assertThat(visitedDoctorList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        equalsVerifier(VisitedDoctor::class)
        val visitedDoctor1 = VisitedDoctor()
        visitedDoctor1.id = 1L
        val visitedDoctor2 = VisitedDoctor()
        visitedDoctor2.id = visitedDoctor1.id
        assertThat(visitedDoctor1).isEqualTo(visitedDoctor2)
        visitedDoctor2.id = 2L
        assertThat(visitedDoctor1).isNotEqualTo(visitedDoctor2)
        visitedDoctor1.id = null
        assertThat(visitedDoctor1).isNotEqualTo(visitedDoctor2)
    }

    companion object {

        private const val DEFAULT_OPINION: String = "AAAAAAAAAA"
        private const val UPDATED_OPINION = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): VisitedDoctor {
            val visitedDoctor = VisitedDoctor(
                opinion = DEFAULT_OPINION
            )

            return visitedDoctor
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): VisitedDoctor {
            val visitedDoctor = VisitedDoctor(
                opinion = UPDATED_OPINION
            )

            return visitedDoctor
        }
    }
}
