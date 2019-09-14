package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.MedicusApp
import pl.marczynski.medicus.domain.Treatment
import pl.marczynski.medicus.repository.TreatmentRepository
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
import org.springframework.util.Base64Utils
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
 * Integration tests for the [TreatmentResource] REST controller.
 *
 * @see TreatmentResource
 */
@SpringBootTest(classes = [MedicusApp::class])
class TreatmentResourceIT {

    @Autowired
    private lateinit var treatmentRepository: TreatmentRepository

    @Mock
    private lateinit var treatmentRepositoryMock: TreatmentRepository

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

    private lateinit var restTreatmentMockMvc: MockMvc

    private lateinit var treatment: Treatment

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val treatmentResource = TreatmentResource(treatmentRepository)
        this.restTreatmentMockMvc = MockMvcBuilders.standaloneSetup(treatmentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        treatment = createEntity(em)
    }

    @Test
    @Transactional
    fun createTreatment() {
        val databaseSizeBeforeCreate = treatmentRepository.findAll().size

        // Create the Treatment
        restTreatmentMockMvc.perform(
            post("/api/treatments")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(treatment))
        ).andExpect(status().isCreated)

        // Validate the Treatment in the database
        val treatmentList = treatmentRepository.findAll()
        assertThat(treatmentList).hasSize(databaseSizeBeforeCreate + 1)
        val testTreatment = treatmentList[treatmentList.size - 1]
        assertThat(testTreatment.startDate).isEqualTo(DEFAULT_START_DATE)
        assertThat(testTreatment.endDate).isEqualTo(DEFAULT_END_DATE)
        assertThat(testTreatment.description).isEqualTo(DEFAULT_DESCRIPTION)
        assertThat(testTreatment.descriptionScan).isEqualTo(DEFAULT_DESCRIPTION_SCAN)
        assertThat(testTreatment.descriptionScanContentType).isEqualTo(DEFAULT_DESCRIPTION_SCAN_CONTENT_TYPE)
    }

    @Test
    @Transactional
    fun createTreatmentWithExistingId() {
        val databaseSizeBeforeCreate = treatmentRepository.findAll().size

        // Create the Treatment with an existing ID
        treatment.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restTreatmentMockMvc.perform(
            post("/api/treatments")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(treatment))
        ).andExpect(status().isBadRequest)

        // Validate the Treatment in the database
        val treatmentList = treatmentRepository.findAll()
        assertThat(treatmentList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkStartDateIsRequired() {
        val databaseSizeBeforeTest = treatmentRepository.findAll().size
        // set the field null
        treatment.startDate = null

        // Create the Treatment, which fails.

        restTreatmentMockMvc.perform(
            post("/api/treatments")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(treatment))
        ).andExpect(status().isBadRequest)

        val treatmentList = treatmentRepository.findAll()
        assertThat(treatmentList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllTreatments() {
        // Initialize the database
        treatmentRepository.saveAndFlush(treatment)

        // Get all the treatmentList
        restTreatmentMockMvc.perform(get("/api/treatments?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(treatment.id?.toInt())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].descriptionScanContentType").value(hasItem(DEFAULT_DESCRIPTION_SCAN_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].descriptionScan").value(hasItem(Base64Utils.encodeToString(DEFAULT_DESCRIPTION_SCAN))))
    }

    @Suppress("unchecked")
    fun getAllTreatmentsWithEagerRelationshipsIsEnabled() {
        val treatmentResource = TreatmentResource(treatmentRepositoryMock)
        `when`(treatmentRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))

        val restTreatmentMockMvc = MockMvcBuilders.standaloneSetup(treatmentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restTreatmentMockMvc.perform(get("/api/treatments?eagerload=true"))
            .andExpect(status().isOk)

        verify(treatmentRepositoryMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Suppress("unchecked")
    fun getAllTreatmentsWithEagerRelationshipsIsNotEnabled() {
        val treatmentResource = TreatmentResource(treatmentRepositoryMock)
        `when`(treatmentRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))
        val restTreatmentMockMvc = MockMvcBuilders.standaloneSetup(treatmentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restTreatmentMockMvc.perform(get("/api/treatments?eagerload=true"))
            .andExpect(status().isOk)

        verify(treatmentRepositoryMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Test
    @Transactional
    fun getTreatment() {
        // Initialize the database
        treatmentRepository.saveAndFlush(treatment)

        val id = treatment.id
        assertNotNull(id)

        // Get the treatment
        restTreatmentMockMvc.perform(get("/api/treatments/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.descriptionScanContentType").value(DEFAULT_DESCRIPTION_SCAN_CONTENT_TYPE))
            .andExpect(jsonPath("$.descriptionScan").value(Base64Utils.encodeToString(DEFAULT_DESCRIPTION_SCAN)))
    }

    @Test
    @Transactional
    fun getNonExistingTreatment() {
        // Get the treatment
        restTreatmentMockMvc.perform(get("/api/treatments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateTreatment() {
        // Initialize the database
        treatmentRepository.saveAndFlush(treatment)

        val databaseSizeBeforeUpdate = treatmentRepository.findAll().size

        // Update the treatment
        val id = treatment.id
        assertNotNull(id)
        val updatedTreatment = treatmentRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedTreatment are not directly saved in db
        em.detach(updatedTreatment)
        updatedTreatment.startDate = UPDATED_START_DATE
        updatedTreatment.endDate = UPDATED_END_DATE
        updatedTreatment.description = UPDATED_DESCRIPTION
        updatedTreatment.descriptionScan = UPDATED_DESCRIPTION_SCAN
        updatedTreatment.descriptionScanContentType = UPDATED_DESCRIPTION_SCAN_CONTENT_TYPE

        restTreatmentMockMvc.perform(
            put("/api/treatments")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(updatedTreatment))
        ).andExpect(status().isOk)

        // Validate the Treatment in the database
        val treatmentList = treatmentRepository.findAll()
        assertThat(treatmentList).hasSize(databaseSizeBeforeUpdate)
        val testTreatment = treatmentList[treatmentList.size - 1]
        assertThat(testTreatment.startDate).isEqualTo(UPDATED_START_DATE)
        assertThat(testTreatment.endDate).isEqualTo(UPDATED_END_DATE)
        assertThat(testTreatment.description).isEqualTo(UPDATED_DESCRIPTION)
        assertThat(testTreatment.descriptionScan).isEqualTo(UPDATED_DESCRIPTION_SCAN)
        assertThat(testTreatment.descriptionScanContentType).isEqualTo(UPDATED_DESCRIPTION_SCAN_CONTENT_TYPE)
    }

    @Test
    @Transactional
    fun updateNonExistingTreatment() {
        val databaseSizeBeforeUpdate = treatmentRepository.findAll().size

        // Create the Treatment

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTreatmentMockMvc.perform(
            put("/api/treatments")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(treatment))
        ).andExpect(status().isBadRequest)

        // Validate the Treatment in the database
        val treatmentList = treatmentRepository.findAll()
        assertThat(treatmentList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteTreatment() {
        // Initialize the database
        treatmentRepository.saveAndFlush(treatment)

        val databaseSizeBeforeDelete = treatmentRepository.findAll().size

        val id = treatment.id
        assertNotNull(id)

        // Delete the treatment
        restTreatmentMockMvc.perform(
            delete("/api/treatments/{id}", id)
                .accept(APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val treatmentList = treatmentRepository.findAll()
        assertThat(treatmentList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        equalsVerifier(Treatment::class)
        val treatment1 = Treatment()
        treatment1.id = 1L
        val treatment2 = Treatment()
        treatment2.id = treatment1.id
        assertThat(treatment1).isEqualTo(treatment2)
        treatment2.id = 2L
        assertThat(treatment1).isNotEqualTo(treatment2)
        treatment1.id = null
        assertThat(treatment1).isNotEqualTo(treatment2)
    }

    companion object {

        private val DEFAULT_START_DATE: LocalDate = LocalDate.ofEpochDay(0L)
        private val UPDATED_START_DATE: LocalDate = LocalDate.now(ZoneId.systemDefault())
        private val SMALLER_START_DATE: LocalDate = LocalDate.ofEpochDay(-1L)

        private val DEFAULT_END_DATE: LocalDate = LocalDate.ofEpochDay(0L)
        private val UPDATED_END_DATE: LocalDate = LocalDate.now(ZoneId.systemDefault())
        private val SMALLER_END_DATE: LocalDate = LocalDate.ofEpochDay(-1L)

        private const val DEFAULT_DESCRIPTION: String = "AAAAAAAAAA"
        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"

        private val DEFAULT_DESCRIPTION_SCAN: ByteArray = createByteArray(1, "0")
        private val UPDATED_DESCRIPTION_SCAN: ByteArray = createByteArray(1, "1")
        private const val DEFAULT_DESCRIPTION_SCAN_CONTENT_TYPE: String = "image/jpg"
        private const val UPDATED_DESCRIPTION_SCAN_CONTENT_TYPE: String = "image/png"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Treatment {
            val treatment = Treatment(
                startDate = DEFAULT_START_DATE,
                endDate = DEFAULT_END_DATE,
                description = DEFAULT_DESCRIPTION,
                descriptionScan = DEFAULT_DESCRIPTION_SCAN,
                descriptionScanContentType = DEFAULT_DESCRIPTION_SCAN_CONTENT_TYPE
            )

            return treatment
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Treatment {
            val treatment = Treatment(
                startDate = UPDATED_START_DATE,
                endDate = UPDATED_END_DATE,
                description = UPDATED_DESCRIPTION,
                descriptionScan = UPDATED_DESCRIPTION_SCAN,
                descriptionScanContentType = UPDATED_DESCRIPTION_SCAN_CONTENT_TYPE
            )

            return treatment
        }
    }
}
