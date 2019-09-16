package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.MedicusApp
import pl.marczynski.medicus.domain.ExaminationType
import pl.marczynski.medicus.repository.ExaminationTypeRepository
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
 * Integration tests for the [ExaminationTypeResource] REST controller.
 *
 * @see ExaminationTypeResource
 */
@SpringBootTest(classes = [MedicusApp::class])
class ExaminationTypeResourceIT {

    @Autowired
    private lateinit var examinationTypeRepository: ExaminationTypeRepository

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

    private lateinit var restExaminationTypeMockMvc: MockMvc

    private lateinit var examinationType: ExaminationType

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val examinationTypeResource = ExaminationTypeResource(examinationTypeRepository)
        this.restExaminationTypeMockMvc = MockMvcBuilders.standaloneSetup(examinationTypeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        examinationType = createEntity(em)
    }

    @Test
    @Transactional
    fun createExaminationType() {
        val databaseSizeBeforeCreate = examinationTypeRepository.findAll().size

        // Create the ExaminationType
        restExaminationTypeMockMvc.perform(
            post("/api/examination-types")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(examinationType))
        ).andExpect(status().isCreated)

        // Validate the ExaminationType in the database
        val examinationTypeList = examinationTypeRepository.findAll()
        assertThat(examinationTypeList).hasSize(databaseSizeBeforeCreate + 1)
        val testExaminationType = examinationTypeList[examinationTypeList.size - 1]
        assertThat(testExaminationType.name).isEqualTo(DEFAULT_NAME)
        assertThat(testExaminationType.unit).isEqualTo(DEFAULT_UNIT)
        assertThat(testExaminationType.minValue).isEqualTo(DEFAULT_MIN_VALUE)
        assertThat(testExaminationType.maxValue).isEqualTo(DEFAULT_MAX_VALUE)
        assertThat(testExaminationType.innerRange).isEqualTo(DEFAULT_INNER_RANGE)
    }

    @Test
    @Transactional
    fun createExaminationTypeWithExistingId() {
        val databaseSizeBeforeCreate = examinationTypeRepository.findAll().size

        // Create the ExaminationType with an existing ID
        examinationType.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restExaminationTypeMockMvc.perform(
            post("/api/examination-types")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(examinationType))
        ).andExpect(status().isBadRequest)

        // Validate the ExaminationType in the database
        val examinationTypeList = examinationTypeRepository.findAll()
        assertThat(examinationTypeList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = examinationTypeRepository.findAll().size
        // set the field null
        examinationType.name = null

        // Create the ExaminationType, which fails.

        restExaminationTypeMockMvc.perform(
            post("/api/examination-types")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(examinationType))
        ).andExpect(status().isBadRequest)

        val examinationTypeList = examinationTypeRepository.findAll()
        assertThat(examinationTypeList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllExaminationTypes() {
        // Initialize the database
        examinationTypeRepository.saveAndFlush(examinationType)

        // Get all the examinationTypeList
        restExaminationTypeMockMvc.perform(get("/api/examination-types?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(examinationType.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].minValue").value(hasItem(DEFAULT_MIN_VALUE)))
            .andExpect(jsonPath("$.[*].maxValue").value(hasItem(DEFAULT_MAX_VALUE)))
            .andExpect(jsonPath("$.[*].innerRange").value(hasItem(DEFAULT_INNER_RANGE)))
    }

    @Test
    @Transactional
    fun getExaminationType() {
        // Initialize the database
        examinationTypeRepository.saveAndFlush(examinationType)

        val id = examinationType.id
        assertNotNull(id)

        // Get the examinationType
        restExaminationTypeMockMvc.perform(get("/api/examination-types/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT))
            .andExpect(jsonPath("$.minValue").value(DEFAULT_MIN_VALUE))
            .andExpect(jsonPath("$.maxValue").value(DEFAULT_MAX_VALUE))
            .andExpect(jsonPath("$.innerRange").value(DEFAULT_INNER_RANGE))
    }

    @Test
    @Transactional
    fun getNonExistingExaminationType() {
        // Get the examinationType
        restExaminationTypeMockMvc.perform(get("/api/examination-types/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateExaminationType() {
        // Initialize the database
        examinationTypeRepository.saveAndFlush(examinationType)

        val databaseSizeBeforeUpdate = examinationTypeRepository.findAll().size

        // Update the examinationType
        val id = examinationType.id
        assertNotNull(id)
        val updatedExaminationType = examinationTypeRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedExaminationType are not directly saved in db
        em.detach(updatedExaminationType)
        updatedExaminationType.name = UPDATED_NAME
        updatedExaminationType.unit = UPDATED_UNIT
        updatedExaminationType.minValue = UPDATED_MIN_VALUE
        updatedExaminationType.maxValue = UPDATED_MAX_VALUE
        updatedExaminationType.innerRange = UPDATED_INNER_RANGE

        restExaminationTypeMockMvc.perform(
            put("/api/examination-types")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(updatedExaminationType))
        ).andExpect(status().isOk)

        // Validate the ExaminationType in the database
        val examinationTypeList = examinationTypeRepository.findAll()
        assertThat(examinationTypeList).hasSize(databaseSizeBeforeUpdate)
        val testExaminationType = examinationTypeList[examinationTypeList.size - 1]
        assertThat(testExaminationType.name).isEqualTo(UPDATED_NAME)
        assertThat(testExaminationType.unit).isEqualTo(UPDATED_UNIT)
        assertThat(testExaminationType.minValue).isEqualTo(UPDATED_MIN_VALUE)
        assertThat(testExaminationType.maxValue).isEqualTo(UPDATED_MAX_VALUE)
        assertThat(testExaminationType.innerRange).isEqualTo(UPDATED_INNER_RANGE)
    }

    @Test
    @Transactional
    fun updateNonExistingExaminationType() {
        val databaseSizeBeforeUpdate = examinationTypeRepository.findAll().size

        // Create the ExaminationType

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExaminationTypeMockMvc.perform(
            put("/api/examination-types")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(examinationType))
        ).andExpect(status().isBadRequest)

        // Validate the ExaminationType in the database
        val examinationTypeList = examinationTypeRepository.findAll()
        assertThat(examinationTypeList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteExaminationType() {
        // Initialize the database
        examinationTypeRepository.saveAndFlush(examinationType)

        val databaseSizeBeforeDelete = examinationTypeRepository.findAll().size

        val id = examinationType.id
        assertNotNull(id)

        // Delete the examinationType
        restExaminationTypeMockMvc.perform(
            delete("/api/examination-types/{id}", id)
                .accept(APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val examinationTypeList = examinationTypeRepository.findAll()
        assertThat(examinationTypeList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        equalsVerifier(ExaminationType::class)
        val examinationType1 = ExaminationType()
        examinationType1.id = 1L
        val examinationType2 = ExaminationType()
        examinationType2.id = examinationType1.id
        assertThat(examinationType1).isEqualTo(examinationType2)
        examinationType2.id = 2L
        assertThat(examinationType1).isNotEqualTo(examinationType2)
        examinationType1.id = null
        assertThat(examinationType1).isNotEqualTo(examinationType2)
    }

    companion object {

        private const val DEFAULT_NAME: String = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_UNIT: String = "AAAAAAAAAA"
        private const val UPDATED_UNIT = "BBBBBBBBBB"

        private const val DEFAULT_MIN_VALUE: Double = 1.0
        private const val UPDATED_MIN_VALUE: Double = 2.0
        private const val SMALLER_MIN_VALUE: Double = 1.0 - 1.0

        private const val DEFAULT_MAX_VALUE: Double = 1.0
        private const val UPDATED_MAX_VALUE: Double = 2.0
        private const val SMALLER_MAX_VALUE: Double = 1.0 - 1.0

        private const val DEFAULT_INNER_RANGE: Boolean = false
        private const val UPDATED_INNER_RANGE: Boolean = true

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): ExaminationType {
            val examinationType = ExaminationType(
                name = DEFAULT_NAME,
                unit = DEFAULT_UNIT,
                minValue = DEFAULT_MIN_VALUE,
                maxValue = DEFAULT_MAX_VALUE,
                innerRange = DEFAULT_INNER_RANGE
            )

            return examinationType
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): ExaminationType {
            val examinationType = ExaminationType(
                name = UPDATED_NAME,
                unit = UPDATED_UNIT,
                minValue = UPDATED_MIN_VALUE,
                maxValue = UPDATED_MAX_VALUE,
                innerRange = UPDATED_INNER_RANGE
            )

            return examinationType
        }
    }
}
