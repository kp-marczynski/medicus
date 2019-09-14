package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.MedicusApp
import pl.marczynski.medicus.domain.Examination
import pl.marczynski.medicus.repository.ExaminationRepository
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
 * Integration tests for the [ExaminationResource] REST controller.
 *
 * @see ExaminationResource
 */
@SpringBootTest(classes = [MedicusApp::class])
class ExaminationResourceIT {

    @Autowired
    private lateinit var examinationRepository: ExaminationRepository

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

    private lateinit var restExaminationMockMvc: MockMvc

    private lateinit var examination: Examination

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val examinationResource = ExaminationResource(examinationRepository)
        this.restExaminationMockMvc = MockMvcBuilders.standaloneSetup(examinationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        examination = createEntity(em)
    }

    @Test
    @Transactional
    fun createExamination() {
        val databaseSizeBeforeCreate = examinationRepository.findAll().size

        // Create the Examination
        restExaminationMockMvc.perform(
            post("/api/examinations")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(examination))
        ).andExpect(status().isCreated)

        // Validate the Examination in the database
        val examinationList = examinationRepository.findAll()
        assertThat(examinationList).hasSize(databaseSizeBeforeCreate + 1)
        val testExamination = examinationList[examinationList.size - 1]
        assertThat(testExamination.value).isEqualTo(DEFAULT_VALUE)
        assertThat(testExamination.valueModificator).isEqualTo(DEFAULT_VALUE_MODIFICATOR)
    }

    @Test
    @Transactional
    fun createExaminationWithExistingId() {
        val databaseSizeBeforeCreate = examinationRepository.findAll().size

        // Create the Examination with an existing ID
        examination.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restExaminationMockMvc.perform(
            post("/api/examinations")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(examination))
        ).andExpect(status().isBadRequest)

        // Validate the Examination in the database
        val examinationList = examinationRepository.findAll()
        assertThat(examinationList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkValueIsRequired() {
        val databaseSizeBeforeTest = examinationRepository.findAll().size
        // set the field null
        examination.value = null

        // Create the Examination, which fails.

        restExaminationMockMvc.perform(
            post("/api/examinations")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(examination))
        ).andExpect(status().isBadRequest)

        val examinationList = examinationRepository.findAll()
        assertThat(examinationList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllExaminations() {
        // Initialize the database
        examinationRepository.saveAndFlush(examination)

        // Get all the examinationList
        restExaminationMockMvc.perform(get("/api/examinations?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(examination.id?.toInt())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].valueModificator").value(hasItem(DEFAULT_VALUE_MODIFICATOR)))
    }

    @Test
    @Transactional
    fun getExamination() {
        // Initialize the database
        examinationRepository.saveAndFlush(examination)

        val id = examination.id
        assertNotNull(id)

        // Get the examination
        restExaminationMockMvc.perform(get("/api/examinations/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.valueModificator").value(DEFAULT_VALUE_MODIFICATOR))
    }

    @Test
    @Transactional
    fun getNonExistingExamination() {
        // Get the examination
        restExaminationMockMvc.perform(get("/api/examinations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateExamination() {
        // Initialize the database
        examinationRepository.saveAndFlush(examination)

        val databaseSizeBeforeUpdate = examinationRepository.findAll().size

        // Update the examination
        val id = examination.id
        assertNotNull(id)
        val updatedExamination = examinationRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedExamination are not directly saved in db
        em.detach(updatedExamination)
        updatedExamination.value = UPDATED_VALUE
        updatedExamination.valueModificator = UPDATED_VALUE_MODIFICATOR

        restExaminationMockMvc.perform(
            put("/api/examinations")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(updatedExamination))
        ).andExpect(status().isOk)

        // Validate the Examination in the database
        val examinationList = examinationRepository.findAll()
        assertThat(examinationList).hasSize(databaseSizeBeforeUpdate)
        val testExamination = examinationList[examinationList.size - 1]
        assertThat(testExamination.value).isEqualTo(UPDATED_VALUE)
        assertThat(testExamination.valueModificator).isEqualTo(UPDATED_VALUE_MODIFICATOR)
    }

    @Test
    @Transactional
    fun updateNonExistingExamination() {
        val databaseSizeBeforeUpdate = examinationRepository.findAll().size

        // Create the Examination

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExaminationMockMvc.perform(
            put("/api/examinations")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(examination))
        ).andExpect(status().isBadRequest)

        // Validate the Examination in the database
        val examinationList = examinationRepository.findAll()
        assertThat(examinationList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteExamination() {
        // Initialize the database
        examinationRepository.saveAndFlush(examination)

        val databaseSizeBeforeDelete = examinationRepository.findAll().size

        val id = examination.id
        assertNotNull(id)

        // Delete the examination
        restExaminationMockMvc.perform(
            delete("/api/examinations/{id}", id)
                .accept(APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val examinationList = examinationRepository.findAll()
        assertThat(examinationList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        equalsVerifier(Examination::class)
        val examination1 = Examination()
        examination1.id = 1L
        val examination2 = Examination()
        examination2.id = examination1.id
        assertThat(examination1).isEqualTo(examination2)
        examination2.id = 2L
        assertThat(examination1).isNotEqualTo(examination2)
        examination1.id = null
        assertThat(examination1).isNotEqualTo(examination2)
    }

    companion object {

        private const val DEFAULT_VALUE: Double = 1.0
        private const val UPDATED_VALUE: Double = 2.0
        private const val SMALLER_VALUE: Double = 1.0 - 1.0

        private const val DEFAULT_VALUE_MODIFICATOR: String = "AAAAAAAAAA"
        private const val UPDATED_VALUE_MODIFICATOR = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Examination {
            val examination = Examination(
                value = DEFAULT_VALUE,
                valueModificator = DEFAULT_VALUE_MODIFICATOR
            )

            return examination
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Examination {
            val examination = Examination(
                value = UPDATED_VALUE,
                valueModificator = UPDATED_VALUE_MODIFICATOR
            )

            return examination
        }
    }
}
