package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.MedicusApp
import pl.marczynski.medicus.domain.Procedure
import pl.marczynski.medicus.repository.ProcedureRepository
import pl.marczynski.medicus.web.rest.errors.ExceptionTranslator

import kotlin.test.assertNotNull

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import javax.persistence.EntityManager
import java.time.LocalDate
import java.time.ZoneId

import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Integration tests for the [ProcedureResource] REST controller.
 *
 * @see ProcedureResource
 */
@SpringBootTest(classes = [MedicusApp::class])
class ProcedureResourceIT {

    @Autowired
    private lateinit var procedureRepository: ProcedureRepository

    @Mock
    private lateinit var procedureRepositoryMock: ProcedureRepository

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

    private lateinit var restProcedureMockMvc: MockMvc

    private lateinit var procedure: Procedure

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val procedureResource = ProcedureResource(procedureRepository)
        this.restProcedureMockMvc = MockMvcBuilders.standaloneSetup(procedureResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        procedure = createEntity(em)
    }

    @Test
    @Transactional
    fun createProcedure() {
        val databaseSizeBeforeCreate = procedureRepository.findAll().size

        // Create the Procedure
        restProcedureMockMvc.perform(
            post("/api/procedures")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(procedure))
        ).andExpect(status().isCreated)

        // Validate the Procedure in the database
        val procedureList = procedureRepository.findAll()
        assertThat(procedureList).hasSize(databaseSizeBeforeCreate + 1)
        val testProcedure = procedureList[procedureList.size - 1]
        assertThat(testProcedure.date).isEqualTo(DEFAULT_DATE)
        assertThat(testProcedure.title).isEqualTo(DEFAULT_TITLE)
        assertThat(testProcedure.description).isEqualTo(DEFAULT_DESCRIPTION)
    }

    @Test
    @Transactional
    fun createProcedureWithExistingId() {
        val databaseSizeBeforeCreate = procedureRepository.findAll().size

        // Create the Procedure with an existing ID
        procedure.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restProcedureMockMvc.perform(
            post("/api/procedures")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(procedure))
        ).andExpect(status().isBadRequest)

        // Validate the Procedure in the database
        val procedureList = procedureRepository.findAll()
        assertThat(procedureList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkDateIsRequired() {
        val databaseSizeBeforeTest = procedureRepository.findAll().size
        // set the field null
        procedure.date = null

        // Create the Procedure, which fails.

        restProcedureMockMvc.perform(
            post("/api/procedures")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(procedure))
        ).andExpect(status().isBadRequest)

        val procedureList = procedureRepository.findAll()
        assertThat(procedureList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkTitleIsRequired() {
        val databaseSizeBeforeTest = procedureRepository.findAll().size
        // set the field null
        procedure.title = null

        // Create the Procedure, which fails.

        restProcedureMockMvc.perform(
            post("/api/procedures")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(procedure))
        ).andExpect(status().isBadRequest)

        val procedureList = procedureRepository.findAll()
        assertThat(procedureList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllProcedures() {
        // Initialize the database
        procedureRepository.saveAndFlush(procedure)

        // Get all the procedureList
        restProcedureMockMvc.perform(get("/api/procedures?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(procedure.id?.toInt())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
    }

    @Suppress("unchecked")
    fun getAllProceduresWithEagerRelationshipsIsEnabled() {
        val procedureResource = ProcedureResource(procedureRepositoryMock)
        `when`(procedureRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))

        val restProcedureMockMvc = MockMvcBuilders.standaloneSetup(procedureResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restProcedureMockMvc.perform(get("/api/procedures?eagerload=true"))
            .andExpect(status().isOk)

        verify(procedureRepositoryMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Suppress("unchecked")
    fun getAllProceduresWithEagerRelationshipsIsNotEnabled() {
        val procedureResource = ProcedureResource(procedureRepositoryMock)
        `when`(procedureRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))
        val restProcedureMockMvc = MockMvcBuilders.standaloneSetup(procedureResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restProcedureMockMvc.perform(get("/api/procedures?eagerload=true"))
            .andExpect(status().isOk)

        verify(procedureRepositoryMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Test
    @Transactional
    fun getProcedure() {
        // Initialize the database
        procedureRepository.saveAndFlush(procedure)

        val id = procedure.id
        assertNotNull(id)

        // Get the procedure
        restProcedureMockMvc.perform(get("/api/procedures/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
    }

    @Test
    @Transactional
    fun getNonExistingProcedure() {
        // Get the procedure
        restProcedureMockMvc.perform(get("/api/procedures/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateProcedure() {
        // Initialize the database
        procedureRepository.saveAndFlush(procedure)

        val databaseSizeBeforeUpdate = procedureRepository.findAll().size

        // Update the procedure
        val id = procedure.id
        assertNotNull(id)
        val updatedProcedure = procedureRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedProcedure are not directly saved in db
        em.detach(updatedProcedure)
        updatedProcedure.date = UPDATED_DATE
        updatedProcedure.title = UPDATED_TITLE
        updatedProcedure.description = UPDATED_DESCRIPTION

        restProcedureMockMvc.perform(
            put("/api/procedures")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(updatedProcedure))
        ).andExpect(status().isOk)

        // Validate the Procedure in the database
        val procedureList = procedureRepository.findAll()
        assertThat(procedureList).hasSize(databaseSizeBeforeUpdate)
        val testProcedure = procedureList[procedureList.size - 1]
        assertThat(testProcedure.date).isEqualTo(UPDATED_DATE)
        assertThat(testProcedure.title).isEqualTo(UPDATED_TITLE)
        assertThat(testProcedure.description).isEqualTo(UPDATED_DESCRIPTION)
    }

    @Test
    @Transactional
    fun updateNonExistingProcedure() {
        val databaseSizeBeforeUpdate = procedureRepository.findAll().size

        // Create the Procedure

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProcedureMockMvc.perform(
            put("/api/procedures")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(procedure))
        ).andExpect(status().isBadRequest)

        // Validate the Procedure in the database
        val procedureList = procedureRepository.findAll()
        assertThat(procedureList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteProcedure() {
        // Initialize the database
        procedureRepository.saveAndFlush(procedure)

        val databaseSizeBeforeDelete = procedureRepository.findAll().size

        val id = procedure.id
        assertNotNull(id)

        // Delete the procedure
        restProcedureMockMvc.perform(
            delete("/api/procedures/{id}", id)
                .accept(APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val procedureList = procedureRepository.findAll()
        assertThat(procedureList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        equalsVerifier(Procedure::class)
        val procedure1 = Procedure()
        procedure1.id = 1L
        val procedure2 = Procedure()
        procedure2.id = procedure1.id
        assertThat(procedure1).isEqualTo(procedure2)
        procedure2.id = 2L
        assertThat(procedure1).isNotEqualTo(procedure2)
        procedure1.id = null
        assertThat(procedure1).isNotEqualTo(procedure2)
    }

    companion object {

        private val DEFAULT_DATE: LocalDate = LocalDate.ofEpochDay(0L)
        private val UPDATED_DATE: LocalDate = LocalDate.now(ZoneId.systemDefault())
        private val SMALLER_DATE: LocalDate = LocalDate.ofEpochDay(-1L)

        private const val DEFAULT_TITLE: String = "AAAAAAAAAA"
        private const val UPDATED_TITLE = "BBBBBBBBBB"

        private const val DEFAULT_DESCRIPTION: String = "AAAAAAAAAA"
        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Procedure {
            val procedure = Procedure(
                date = DEFAULT_DATE,
                title = DEFAULT_TITLE,
                description = DEFAULT_DESCRIPTION
            )

            return procedure
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Procedure {
            val procedure = Procedure(
                date = UPDATED_DATE,
                title = UPDATED_TITLE,
                description = UPDATED_DESCRIPTION
            )

            return procedure
        }
    }
}
