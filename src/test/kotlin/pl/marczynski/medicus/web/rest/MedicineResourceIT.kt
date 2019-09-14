package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.MedicusApp
import pl.marczynski.medicus.domain.Medicine
import pl.marczynski.medicus.repository.MedicineRepository
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
import org.springframework.util.Base64Utils
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
 * Integration tests for the [MedicineResource] REST controller.
 *
 * @see MedicineResource
 */
@SpringBootTest(classes = [MedicusApp::class])
class MedicineResourceIT {

    @Autowired
    private lateinit var medicineRepository: MedicineRepository

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

    private lateinit var restMedicineMockMvc: MockMvc

    private lateinit var medicine: Medicine

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val medicineResource = MedicineResource(medicineRepository)
        this.restMedicineMockMvc = MockMvcBuilders.standaloneSetup(medicineResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        medicine = createEntity(em)
    }

    @Test
    @Transactional
    fun createMedicine() {
        val databaseSizeBeforeCreate = medicineRepository.findAll().size

        // Create the Medicine
        restMedicineMockMvc.perform(
            post("/api/medicines")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(medicine))
        ).andExpect(status().isCreated)

        // Validate the Medicine in the database
        val medicineList = medicineRepository.findAll()
        assertThat(medicineList).hasSize(databaseSizeBeforeCreate + 1)
        val testMedicine = medicineList[medicineList.size - 1]
        assertThat(testMedicine.name).isEqualTo(DEFAULT_NAME)
        assertThat(testMedicine.indication).isEqualTo(DEFAULT_INDICATION)
        assertThat(testMedicine.leaflet).isEqualTo(DEFAULT_LEAFLET)
        assertThat(testMedicine.leafletContentType).isEqualTo(DEFAULT_LEAFLET_CONTENT_TYPE)
        assertThat(testMedicine.language).isEqualTo(DEFAULT_LANGUAGE)
    }

    @Test
    @Transactional
    fun createMedicineWithExistingId() {
        val databaseSizeBeforeCreate = medicineRepository.findAll().size

        // Create the Medicine with an existing ID
        medicine.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restMedicineMockMvc.perform(
            post("/api/medicines")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(medicine))
        ).andExpect(status().isBadRequest)

        // Validate the Medicine in the database
        val medicineList = medicineRepository.findAll()
        assertThat(medicineList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = medicineRepository.findAll().size
        // set the field null
        medicine.name = null

        // Create the Medicine, which fails.

        restMedicineMockMvc.perform(
            post("/api/medicines")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(medicine))
        ).andExpect(status().isBadRequest)

        val medicineList = medicineRepository.findAll()
        assertThat(medicineList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllMedicines() {
        // Initialize the database
        medicineRepository.saveAndFlush(medicine)

        // Get all the medicineList
        restMedicineMockMvc.perform(get("/api/medicines?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(medicine.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].indication").value(hasItem(DEFAULT_INDICATION)))
            .andExpect(jsonPath("$.[*].leafletContentType").value(hasItem(DEFAULT_LEAFLET_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].leaflet").value(hasItem(Base64Utils.encodeToString(DEFAULT_LEAFLET))))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE)))
    }

    @Test
    @Transactional
    fun getMedicine() {
        // Initialize the database
        medicineRepository.saveAndFlush(medicine)

        val id = medicine.id
        assertNotNull(id)

        // Get the medicine
        restMedicineMockMvc.perform(get("/api/medicines/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.indication").value(DEFAULT_INDICATION))
            .andExpect(jsonPath("$.leafletContentType").value(DEFAULT_LEAFLET_CONTENT_TYPE))
            .andExpect(jsonPath("$.leaflet").value(Base64Utils.encodeToString(DEFAULT_LEAFLET)))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE))
    }

    @Test
    @Transactional
    fun getNonExistingMedicine() {
        // Get the medicine
        restMedicineMockMvc.perform(get("/api/medicines/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateMedicine() {
        // Initialize the database
        medicineRepository.saveAndFlush(medicine)

        val databaseSizeBeforeUpdate = medicineRepository.findAll().size

        // Update the medicine
        val id = medicine.id
        assertNotNull(id)
        val updatedMedicine = medicineRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedMedicine are not directly saved in db
        em.detach(updatedMedicine)
        updatedMedicine.name = UPDATED_NAME
        updatedMedicine.indication = UPDATED_INDICATION
        updatedMedicine.leaflet = UPDATED_LEAFLET
        updatedMedicine.leafletContentType = UPDATED_LEAFLET_CONTENT_TYPE
        updatedMedicine.language = UPDATED_LANGUAGE

        restMedicineMockMvc.perform(
            put("/api/medicines")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(updatedMedicine))
        ).andExpect(status().isOk)

        // Validate the Medicine in the database
        val medicineList = medicineRepository.findAll()
        assertThat(medicineList).hasSize(databaseSizeBeforeUpdate)
        val testMedicine = medicineList[medicineList.size - 1]
        assertThat(testMedicine.name).isEqualTo(UPDATED_NAME)
        assertThat(testMedicine.indication).isEqualTo(UPDATED_INDICATION)
        assertThat(testMedicine.leaflet).isEqualTo(UPDATED_LEAFLET)
        assertThat(testMedicine.leafletContentType).isEqualTo(UPDATED_LEAFLET_CONTENT_TYPE)
        assertThat(testMedicine.language).isEqualTo(UPDATED_LANGUAGE)
    }

    @Test
    @Transactional
    fun updateNonExistingMedicine() {
        val databaseSizeBeforeUpdate = medicineRepository.findAll().size

        // Create the Medicine

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMedicineMockMvc.perform(
            put("/api/medicines")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(medicine))
        ).andExpect(status().isBadRequest)

        // Validate the Medicine in the database
        val medicineList = medicineRepository.findAll()
        assertThat(medicineList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteMedicine() {
        // Initialize the database
        medicineRepository.saveAndFlush(medicine)

        val databaseSizeBeforeDelete = medicineRepository.findAll().size

        val id = medicine.id
        assertNotNull(id)

        // Delete the medicine
        restMedicineMockMvc.perform(
            delete("/api/medicines/{id}", id)
                .accept(APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val medicineList = medicineRepository.findAll()
        assertThat(medicineList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        equalsVerifier(Medicine::class)
        val medicine1 = Medicine()
        medicine1.id = 1L
        val medicine2 = Medicine()
        medicine2.id = medicine1.id
        assertThat(medicine1).isEqualTo(medicine2)
        medicine2.id = 2L
        assertThat(medicine1).isNotEqualTo(medicine2)
        medicine1.id = null
        assertThat(medicine1).isNotEqualTo(medicine2)
    }

    companion object {

        private const val DEFAULT_NAME: String = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_INDICATION: String = "AAAAAAAAAA"
        private const val UPDATED_INDICATION = "BBBBBBBBBB"

        private val DEFAULT_LEAFLET: ByteArray = createByteArray(1, "0")
        private val UPDATED_LEAFLET: ByteArray = createByteArray(1, "1")
        private const val DEFAULT_LEAFLET_CONTENT_TYPE: String = "image/jpg"
        private const val UPDATED_LEAFLET_CONTENT_TYPE: String = "image/png"

        private const val DEFAULT_LANGUAGE: String = "AAAAAAAAAA"
        private const val UPDATED_LANGUAGE = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Medicine {
            val medicine = Medicine(
                name = DEFAULT_NAME,
                indication = DEFAULT_INDICATION,
                leaflet = DEFAULT_LEAFLET,
                leafletContentType = DEFAULT_LEAFLET_CONTENT_TYPE,
                language = DEFAULT_LANGUAGE
            )

            return medicine
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Medicine {
            val medicine = Medicine(
                name = UPDATED_NAME,
                indication = UPDATED_INDICATION,
                leaflet = UPDATED_LEAFLET,
                leafletContentType = UPDATED_LEAFLET_CONTENT_TYPE,
                language = UPDATED_LANGUAGE
            )

            return medicine
        }
    }
}
