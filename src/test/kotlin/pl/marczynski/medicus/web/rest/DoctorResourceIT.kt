package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.MedicusApp
import pl.marczynski.medicus.domain.Doctor
import pl.marczynski.medicus.repository.DoctorRepository
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
 * Integration tests for the [DoctorResource] REST controller.
 *
 * @see DoctorResource
 */
@SpringBootTest(classes = [MedicusApp::class])
class DoctorResourceIT {

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

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

    private lateinit var restDoctorMockMvc: MockMvc

    private lateinit var doctor: Doctor

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val doctorResource = DoctorResource(doctorRepository)
        this.restDoctorMockMvc = MockMvcBuilders.standaloneSetup(doctorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        doctor = createEntity(em)
    }

    @Test
    @Transactional
    fun createDoctor() {
        val databaseSizeBeforeCreate = doctorRepository.findAll().size

        // Create the Doctor
        restDoctorMockMvc.perform(
            post("/api/doctors")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(doctor))
        ).andExpect(status().isCreated)

        // Validate the Doctor in the database
        val doctorList = doctorRepository.findAll()
        assertThat(doctorList).hasSize(databaseSizeBeforeCreate + 1)
        val testDoctor = doctorList[doctorList.size - 1]
        assertThat(testDoctor.name).isEqualTo(DEFAULT_NAME)
        assertThat(testDoctor.specialization).isEqualTo(DEFAULT_SPECIALIZATION)
        assertThat(testDoctor.language).isEqualTo(DEFAULT_LANGUAGE)
    }

    @Test
    @Transactional
    fun createDoctorWithExistingId() {
        val databaseSizeBeforeCreate = doctorRepository.findAll().size

        // Create the Doctor with an existing ID
        doctor.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restDoctorMockMvc.perform(
            post("/api/doctors")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(doctor))
        ).andExpect(status().isBadRequest)

        // Validate the Doctor in the database
        val doctorList = doctorRepository.findAll()
        assertThat(doctorList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = doctorRepository.findAll().size
        // set the field null
        doctor.name = null

        // Create the Doctor, which fails.

        restDoctorMockMvc.perform(
            post("/api/doctors")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(doctor))
        ).andExpect(status().isBadRequest)

        val doctorList = doctorRepository.findAll()
        assertThat(doctorList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkSpecializationIsRequired() {
        val databaseSizeBeforeTest = doctorRepository.findAll().size
        // set the field null
        doctor.specialization = null

        // Create the Doctor, which fails.

        restDoctorMockMvc.perform(
            post("/api/doctors")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(doctor))
        ).andExpect(status().isBadRequest)

        val doctorList = doctorRepository.findAll()
        assertThat(doctorList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllDoctors() {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor)

        // Get all the doctorList
        restDoctorMockMvc.perform(get("/api/doctors?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(doctor.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].specialization").value(hasItem(DEFAULT_SPECIALIZATION)))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE)))
    }

    @Test
    @Transactional
    fun getDoctor() {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor)

        val id = doctor.id
        assertNotNull(id)

        // Get the doctor
        restDoctorMockMvc.perform(get("/api/doctors/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.specialization").value(DEFAULT_SPECIALIZATION))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE))
    }

    @Test
    @Transactional
    fun getNonExistingDoctor() {
        // Get the doctor
        restDoctorMockMvc.perform(get("/api/doctors/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateDoctor() {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor)

        val databaseSizeBeforeUpdate = doctorRepository.findAll().size

        // Update the doctor
        val id = doctor.id
        assertNotNull(id)
        val updatedDoctor = doctorRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedDoctor are not directly saved in db
        em.detach(updatedDoctor)
        updatedDoctor.name = UPDATED_NAME
        updatedDoctor.specialization = UPDATED_SPECIALIZATION
        updatedDoctor.language = UPDATED_LANGUAGE

        restDoctorMockMvc.perform(
            put("/api/doctors")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(updatedDoctor))
        ).andExpect(status().isOk)

        // Validate the Doctor in the database
        val doctorList = doctorRepository.findAll()
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate)
        val testDoctor = doctorList[doctorList.size - 1]
        assertThat(testDoctor.name).isEqualTo(UPDATED_NAME)
        assertThat(testDoctor.specialization).isEqualTo(UPDATED_SPECIALIZATION)
        assertThat(testDoctor.language).isEqualTo(UPDATED_LANGUAGE)
    }

    @Test
    @Transactional
    fun updateNonExistingDoctor() {
        val databaseSizeBeforeUpdate = doctorRepository.findAll().size

        // Create the Doctor

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDoctorMockMvc.perform(
            put("/api/doctors")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(doctor))
        ).andExpect(status().isBadRequest)

        // Validate the Doctor in the database
        val doctorList = doctorRepository.findAll()
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteDoctor() {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor)

        val databaseSizeBeforeDelete = doctorRepository.findAll().size

        val id = doctor.id
        assertNotNull(id)

        // Delete the doctor
        restDoctorMockMvc.perform(
            delete("/api/doctors/{id}", id)
                .accept(APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val doctorList = doctorRepository.findAll()
        assertThat(doctorList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        equalsVerifier(Doctor::class)
        val doctor1 = Doctor()
        doctor1.id = 1L
        val doctor2 = Doctor()
        doctor2.id = doctor1.id
        assertThat(doctor1).isEqualTo(doctor2)
        doctor2.id = 2L
        assertThat(doctor1).isNotEqualTo(doctor2)
        doctor1.id = null
        assertThat(doctor1).isNotEqualTo(doctor2)
    }

    companion object {

        private const val DEFAULT_NAME: String = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_SPECIALIZATION: String = "AAAAAAAAAA"
        private const val UPDATED_SPECIALIZATION = "BBBBBBBBBB"

        private const val DEFAULT_LANGUAGE: String = "AAAAAAAAAA"
        private const val UPDATED_LANGUAGE = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Doctor {
            val doctor = Doctor(
                name = DEFAULT_NAME,
                specialization = DEFAULT_SPECIALIZATION,
                language = DEFAULT_LANGUAGE
            )

            return doctor
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Doctor {
            val doctor = Doctor(
                name = UPDATED_NAME,
                specialization = UPDATED_SPECIALIZATION,
                language = UPDATED_LANGUAGE
            )

            return doctor
        }
    }
}
