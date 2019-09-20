package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.MedicusApp
import pl.marczynski.medicus.domain.Appointment
import pl.marczynski.medicus.repository.AppointmentRepository
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
 * Integration tests for the [AppointmentResource] REST controller.
 *
 * @see AppointmentResource
 */
@SpringBootTest(classes = [MedicusApp::class])
class AppointmentResourceIT {

    @Autowired
    private lateinit var appointmentRepository: AppointmentRepository

    @Mock
    private lateinit var appointmentRepositoryMock: AppointmentRepository

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

    private lateinit var restAppointmentMockMvc: MockMvc

    private lateinit var appointment: Appointment

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val appointmentResource = AppointmentResource(appointmentRepository)
        this.restAppointmentMockMvc = MockMvcBuilders.standaloneSetup(appointmentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        appointment = createEntity(em)
    }

    @Test
    @Transactional
    fun createAppointment() {
        val databaseSizeBeforeCreate = appointmentRepository.findAll().size

        // Create the Appointment
        restAppointmentMockMvc.perform(
            post("/api/appointments")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(appointment))
        ).andExpect(status().isCreated)

        // Validate the Appointment in the database
        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeCreate + 1)
        val testAppointment = appointmentList[appointmentList.size - 1]
        assertThat(testAppointment.date).isEqualTo(DEFAULT_DATE)
        assertThat(testAppointment.title).isEqualTo(DEFAULT_TITLE)
        assertThat(testAppointment.description).isEqualTo(DEFAULT_DESCRIPTION)
    }

    @Test
    @Transactional
    fun createAppointmentWithExistingId() {
        val databaseSizeBeforeCreate = appointmentRepository.findAll().size

        // Create the Appointment with an existing ID
        appointment.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppointmentMockMvc.perform(
            post("/api/appointments")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(appointment))
        ).andExpect(status().isBadRequest)

        // Validate the Appointment in the database
        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkDateIsRequired() {
        val databaseSizeBeforeTest = appointmentRepository.findAll().size
        // set the field null
        appointment.date = null

        // Create the Appointment, which fails.

        restAppointmentMockMvc.perform(
            post("/api/appointments")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(appointment))
        ).andExpect(status().isBadRequest)

        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkTitleIsRequired() {
        val databaseSizeBeforeTest = appointmentRepository.findAll().size
        // set the field null
        appointment.title = null

        // Create the Appointment, which fails.

        restAppointmentMockMvc.perform(
            post("/api/appointments")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(appointment))
        ).andExpect(status().isBadRequest)

        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllAppointments() {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment)

        // Get all the appointmentList
        restAppointmentMockMvc.perform(get("/api/appointments?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appointment.id?.toInt())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
    }

    @Suppress("unchecked")
    fun getAllAppointmentsWithEagerRelationshipsIsEnabled() {
        val appointmentResource = AppointmentResource(appointmentRepositoryMock)
        `when`(appointmentRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))

        val restAppointmentMockMvc = MockMvcBuilders.standaloneSetup(appointmentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restAppointmentMockMvc.perform(get("/api/appointments?eagerload=true"))
            .andExpect(status().isOk)

        verify(appointmentRepositoryMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Suppress("unchecked")
    fun getAllAppointmentsWithEagerRelationshipsIsNotEnabled() {
        val appointmentResource = AppointmentResource(appointmentRepositoryMock)
        `when`(appointmentRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))
        val restAppointmentMockMvc = MockMvcBuilders.standaloneSetup(appointmentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restAppointmentMockMvc.perform(get("/api/appointments?eagerload=true"))
            .andExpect(status().isOk)

        verify(appointmentRepositoryMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Test
    @Transactional
    fun getAppointment() {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment)

        val id = appointment.id
        assertNotNull(id)

        // Get the appointment
        restAppointmentMockMvc.perform(get("/api/appointments/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
    }

    @Test
    @Transactional
    fun getNonExistingAppointment() {
        // Get the appointment
        restAppointmentMockMvc.perform(get("/api/appointments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateAppointment() {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment)

        val databaseSizeBeforeUpdate = appointmentRepository.findAll().size

        // Update the appointment
        val id = appointment.id
        assertNotNull(id)
        val updatedAppointment = appointmentRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedAppointment are not directly saved in db
        em.detach(updatedAppointment)
        updatedAppointment.date = UPDATED_DATE
        updatedAppointment.title = UPDATED_TITLE
        updatedAppointment.description = UPDATED_DESCRIPTION

        restAppointmentMockMvc.perform(
            put("/api/appointments")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(updatedAppointment))
        ).andExpect(status().isOk)

        // Validate the Appointment in the database
        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate)
        val testAppointment = appointmentList[appointmentList.size - 1]
        assertThat(testAppointment.date).isEqualTo(UPDATED_DATE)
        assertThat(testAppointment.title).isEqualTo(UPDATED_TITLE)
        assertThat(testAppointment.description).isEqualTo(UPDATED_DESCRIPTION)
    }

    @Test
    @Transactional
    fun updateNonExistingAppointment() {
        val databaseSizeBeforeUpdate = appointmentRepository.findAll().size

        // Create the Appointment

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppointmentMockMvc.perform(
            put("/api/appointments")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(appointment))
        ).andExpect(status().isBadRequest)

        // Validate the Appointment in the database
        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteAppointment() {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment)

        val databaseSizeBeforeDelete = appointmentRepository.findAll().size

        val id = appointment.id
        assertNotNull(id)

        // Delete the appointment
        restAppointmentMockMvc.perform(
            delete("/api/appointments/{id}", id)
                .accept(APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        equalsVerifier(Appointment::class)
        val appointment1 = Appointment()
        appointment1.id = 1L
        val appointment2 = Appointment()
        appointment2.id = appointment1.id
        assertThat(appointment1).isEqualTo(appointment2)
        appointment2.id = 2L
        assertThat(appointment1).isNotEqualTo(appointment2)
        appointment1.id = null
        assertThat(appointment1).isNotEqualTo(appointment2)
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
        fun createEntity(em: EntityManager): Appointment {
            val appointment = Appointment(
                date = DEFAULT_DATE,
                title = DEFAULT_TITLE,
                description = DEFAULT_DESCRIPTION
            )

            return appointment
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Appointment {
            val appointment = Appointment(
                date = UPDATED_DATE,
                title = UPDATED_TITLE,
                description = UPDATED_DESCRIPTION
            )

            return appointment
        }
    }
}
