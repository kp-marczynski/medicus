package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.MedicusApp
import pl.marczynski.medicus.domain.Symptom
import pl.marczynski.medicus.repository.SymptomRepository
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
import java.time.LocalDate
import java.time.ZoneId

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
 * Integration tests for the [SymptomResource] REST controller.
 *
 * @see SymptomResource
 */
@SpringBootTest(classes = [MedicusApp::class])
class SymptomResourceIT {

    @Autowired
    private lateinit var symptomRepository: SymptomRepository

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

    private lateinit var restSymptomMockMvc: MockMvc

    private lateinit var symptom: Symptom

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val symptomResource = SymptomResource(symptomRepository)
        this.restSymptomMockMvc = MockMvcBuilders.standaloneSetup(symptomResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        symptom = createEntity(em)
    }

    @Test
    @Transactional
    fun createSymptom() {
        val databaseSizeBeforeCreate = symptomRepository.findAll().size

        // Create the Symptom
        restSymptomMockMvc.perform(
            post("/api/symptoms")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(symptom))
        ).andExpect(status().isCreated)

        // Validate the Symptom in the database
        val symptomList = symptomRepository.findAll()
        assertThat(symptomList).hasSize(databaseSizeBeforeCreate + 1)
        val testSymptom = symptomList[symptomList.size - 1]
        assertThat(testSymptom.startDate).isEqualTo(DEFAULT_START_DATE)
        assertThat(testSymptom.endDate).isEqualTo(DEFAULT_END_DATE)
        assertThat(testSymptom.description).isEqualTo(DEFAULT_DESCRIPTION)
    }

    @Test
    @Transactional
    fun createSymptomWithExistingId() {
        val databaseSizeBeforeCreate = symptomRepository.findAll().size

        // Create the Symptom with an existing ID
        symptom.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restSymptomMockMvc.perform(
            post("/api/symptoms")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(symptom))
        ).andExpect(status().isBadRequest)

        // Validate the Symptom in the database
        val symptomList = symptomRepository.findAll()
        assertThat(symptomList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkStartDateIsRequired() {
        val databaseSizeBeforeTest = symptomRepository.findAll().size
        // set the field null
        symptom.startDate = null

        // Create the Symptom, which fails.

        restSymptomMockMvc.perform(
            post("/api/symptoms")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(symptom))
        ).andExpect(status().isBadRequest)

        val symptomList = symptomRepository.findAll()
        assertThat(symptomList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkDescriptionIsRequired() {
        val databaseSizeBeforeTest = symptomRepository.findAll().size
        // set the field null
        symptom.description = null

        // Create the Symptom, which fails.

        restSymptomMockMvc.perform(
            post("/api/symptoms")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(symptom))
        ).andExpect(status().isBadRequest)

        val symptomList = symptomRepository.findAll()
        assertThat(symptomList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllSymptoms() {
        // Initialize the database
        symptomRepository.saveAndFlush(symptom)

        // Get all the symptomList
        restSymptomMockMvc.perform(get("/api/symptoms?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(symptom.id?.toInt())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
    }

    @Test
    @Transactional
    fun getSymptom() {
        // Initialize the database
        symptomRepository.saveAndFlush(symptom)

        val id = symptom.id
        assertNotNull(id)

        // Get the symptom
        restSymptomMockMvc.perform(get("/api/symptoms/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
    }

    @Test
    @Transactional
    fun getNonExistingSymptom() {
        // Get the symptom
        restSymptomMockMvc.perform(get("/api/symptoms/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateSymptom() {
        // Initialize the database
        symptomRepository.saveAndFlush(symptom)

        val databaseSizeBeforeUpdate = symptomRepository.findAll().size

        // Update the symptom
        val id = symptom.id
        assertNotNull(id)
        val updatedSymptom = symptomRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedSymptom are not directly saved in db
        em.detach(updatedSymptom)
        updatedSymptom.startDate = UPDATED_START_DATE
        updatedSymptom.endDate = UPDATED_END_DATE
        updatedSymptom.description = UPDATED_DESCRIPTION

        restSymptomMockMvc.perform(
            put("/api/symptoms")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(updatedSymptom))
        ).andExpect(status().isOk)

        // Validate the Symptom in the database
        val symptomList = symptomRepository.findAll()
        assertThat(symptomList).hasSize(databaseSizeBeforeUpdate)
        val testSymptom = symptomList[symptomList.size - 1]
        assertThat(testSymptom.startDate).isEqualTo(UPDATED_START_DATE)
        assertThat(testSymptom.endDate).isEqualTo(UPDATED_END_DATE)
        assertThat(testSymptom.description).isEqualTo(UPDATED_DESCRIPTION)
    }

    @Test
    @Transactional
    fun updateNonExistingSymptom() {
        val databaseSizeBeforeUpdate = symptomRepository.findAll().size

        // Create the Symptom

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSymptomMockMvc.perform(
            put("/api/symptoms")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(symptom))
        ).andExpect(status().isBadRequest)

        // Validate the Symptom in the database
        val symptomList = symptomRepository.findAll()
        assertThat(symptomList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteSymptom() {
        // Initialize the database
        symptomRepository.saveAndFlush(symptom)

        val databaseSizeBeforeDelete = symptomRepository.findAll().size

        val id = symptom.id
        assertNotNull(id)

        // Delete the symptom
        restSymptomMockMvc.perform(
            delete("/api/symptoms/{id}", id)
                .accept(APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val symptomList = symptomRepository.findAll()
        assertThat(symptomList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        equalsVerifier(Symptom::class)
        val symptom1 = Symptom()
        symptom1.id = 1L
        val symptom2 = Symptom()
        symptom2.id = symptom1.id
        assertThat(symptom1).isEqualTo(symptom2)
        symptom2.id = 2L
        assertThat(symptom1).isNotEqualTo(symptom2)
        symptom1.id = null
        assertThat(symptom1).isNotEqualTo(symptom2)
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

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Symptom {
            val symptom = Symptom(
                startDate = DEFAULT_START_DATE,
                endDate = DEFAULT_END_DATE,
                description = DEFAULT_DESCRIPTION
            )

            return symptom
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Symptom {
            val symptom = Symptom(
                startDate = UPDATED_START_DATE,
                endDate = UPDATED_END_DATE,
                description = UPDATED_DESCRIPTION
            )

            return symptom
        }
    }
}
