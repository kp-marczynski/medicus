package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.MedicusApp
import pl.marczynski.medicus.domain.OwnedMedicine
import pl.marczynski.medicus.repository.OwnedMedicineRepository
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
 * Integration tests for the [OwnedMedicineResource] REST controller.
 *
 * @see OwnedMedicineResource
 */
@SpringBootTest(classes = [MedicusApp::class])
class OwnedMedicineResourceIT {

    @Autowired
    private lateinit var ownedMedicineRepository: OwnedMedicineRepository

    @Mock
    private lateinit var ownedMedicineRepositoryMock: OwnedMedicineRepository

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

    private lateinit var restOwnedMedicineMockMvc: MockMvc

    private lateinit var ownedMedicine: OwnedMedicine

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val ownedMedicineResource = OwnedMedicineResource(ownedMedicineRepository)
        this.restOwnedMedicineMockMvc = MockMvcBuilders.standaloneSetup(ownedMedicineResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        ownedMedicine = createEntity(em)
    }

    @Test
    @Transactional
    fun createOwnedMedicine() {
        val databaseSizeBeforeCreate = ownedMedicineRepository.findAll().size

        // Create the OwnedMedicine
        restOwnedMedicineMockMvc.perform(
            post("/api/owned-medicines")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(ownedMedicine))
        ).andExpect(status().isCreated)

        // Validate the OwnedMedicine in the database
        val ownedMedicineList = ownedMedicineRepository.findAll()
        assertThat(ownedMedicineList).hasSize(databaseSizeBeforeCreate + 1)
        val testOwnedMedicine = ownedMedicineList[ownedMedicineList.size - 1]
        assertThat(testOwnedMedicine.doses).isEqualTo(DEFAULT_DOSES)
        assertThat(testOwnedMedicine.expirationDate).isEqualTo(DEFAULT_EXPIRATION_DATE)
    }

    @Test
    @Transactional
    fun createOwnedMedicineWithExistingId() {
        val databaseSizeBeforeCreate = ownedMedicineRepository.findAll().size

        // Create the OwnedMedicine with an existing ID
        ownedMedicine.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restOwnedMedicineMockMvc.perform(
            post("/api/owned-medicines")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(ownedMedicine))
        ).andExpect(status().isBadRequest)

        // Validate the OwnedMedicine in the database
        val ownedMedicineList = ownedMedicineRepository.findAll()
        assertThat(ownedMedicineList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkDosesIsRequired() {
        val databaseSizeBeforeTest = ownedMedicineRepository.findAll().size
        // set the field null
        ownedMedicine.doses = null

        // Create the OwnedMedicine, which fails.

        restOwnedMedicineMockMvc.perform(
            post("/api/owned-medicines")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(ownedMedicine))
        ).andExpect(status().isBadRequest)

        val ownedMedicineList = ownedMedicineRepository.findAll()
        assertThat(ownedMedicineList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkExpirationDateIsRequired() {
        val databaseSizeBeforeTest = ownedMedicineRepository.findAll().size
        // set the field null
        ownedMedicine.expirationDate = null

        // Create the OwnedMedicine, which fails.

        restOwnedMedicineMockMvc.perform(
            post("/api/owned-medicines")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(ownedMedicine))
        ).andExpect(status().isBadRequest)

        val ownedMedicineList = ownedMedicineRepository.findAll()
        assertThat(ownedMedicineList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllOwnedMedicines() {
        // Initialize the database
        ownedMedicineRepository.saveAndFlush(ownedMedicine)

        // Get all the ownedMedicineList
        restOwnedMedicineMockMvc.perform(get("/api/owned-medicines?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ownedMedicine.id?.toInt())))
            .andExpect(jsonPath("$.[*].doses").value(hasItem(DEFAULT_DOSES)))
            .andExpect(jsonPath("$.[*].expirationDate").value(hasItem(DEFAULT_EXPIRATION_DATE.toString())))
    }

    @Suppress("unchecked")
    fun getAllOwnedMedicinesWithEagerRelationshipsIsEnabled() {
        val ownedMedicineResource = OwnedMedicineResource(ownedMedicineRepositoryMock)
        `when`(ownedMedicineRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))

        val restOwnedMedicineMockMvc = MockMvcBuilders.standaloneSetup(ownedMedicineResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restOwnedMedicineMockMvc.perform(get("/api/owned-medicines?eagerload=true"))
            .andExpect(status().isOk)

        verify(ownedMedicineRepositoryMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Suppress("unchecked")
    fun getAllOwnedMedicinesWithEagerRelationshipsIsNotEnabled() {
        val ownedMedicineResource = OwnedMedicineResource(ownedMedicineRepositoryMock)
        `when`(ownedMedicineRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))
        val restOwnedMedicineMockMvc = MockMvcBuilders.standaloneSetup(ownedMedicineResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restOwnedMedicineMockMvc.perform(get("/api/owned-medicines?eagerload=true"))
            .andExpect(status().isOk)

        verify(ownedMedicineRepositoryMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Test
    @Transactional
    fun getOwnedMedicine() {
        // Initialize the database
        ownedMedicineRepository.saveAndFlush(ownedMedicine)

        val id = ownedMedicine.id
        assertNotNull(id)

        // Get the ownedMedicine
        restOwnedMedicineMockMvc.perform(get("/api/owned-medicines/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.doses").value(DEFAULT_DOSES))
            .andExpect(jsonPath("$.expirationDate").value(DEFAULT_EXPIRATION_DATE.toString()))
    }

    @Test
    @Transactional
    fun getNonExistingOwnedMedicine() {
        // Get the ownedMedicine
        restOwnedMedicineMockMvc.perform(get("/api/owned-medicines/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateOwnedMedicine() {
        // Initialize the database
        ownedMedicineRepository.saveAndFlush(ownedMedicine)

        val databaseSizeBeforeUpdate = ownedMedicineRepository.findAll().size

        // Update the ownedMedicine
        val id = ownedMedicine.id
        assertNotNull(id)
        val updatedOwnedMedicine = ownedMedicineRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedOwnedMedicine are not directly saved in db
        em.detach(updatedOwnedMedicine)
        updatedOwnedMedicine.doses = UPDATED_DOSES
        updatedOwnedMedicine.expirationDate = UPDATED_EXPIRATION_DATE

        restOwnedMedicineMockMvc.perform(
            put("/api/owned-medicines")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(updatedOwnedMedicine))
        ).andExpect(status().isOk)

        // Validate the OwnedMedicine in the database
        val ownedMedicineList = ownedMedicineRepository.findAll()
        assertThat(ownedMedicineList).hasSize(databaseSizeBeforeUpdate)
        val testOwnedMedicine = ownedMedicineList[ownedMedicineList.size - 1]
        assertThat(testOwnedMedicine.doses).isEqualTo(UPDATED_DOSES)
        assertThat(testOwnedMedicine.expirationDate).isEqualTo(UPDATED_EXPIRATION_DATE)
    }

    @Test
    @Transactional
    fun updateNonExistingOwnedMedicine() {
        val databaseSizeBeforeUpdate = ownedMedicineRepository.findAll().size

        // Create the OwnedMedicine

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOwnedMedicineMockMvc.perform(
            put("/api/owned-medicines")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(ownedMedicine))
        ).andExpect(status().isBadRequest)

        // Validate the OwnedMedicine in the database
        val ownedMedicineList = ownedMedicineRepository.findAll()
        assertThat(ownedMedicineList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteOwnedMedicine() {
        // Initialize the database
        ownedMedicineRepository.saveAndFlush(ownedMedicine)

        val databaseSizeBeforeDelete = ownedMedicineRepository.findAll().size

        val id = ownedMedicine.id
        assertNotNull(id)

        // Delete the ownedMedicine
        restOwnedMedicineMockMvc.perform(
            delete("/api/owned-medicines/{id}", id)
                .accept(APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val ownedMedicineList = ownedMedicineRepository.findAll()
        assertThat(ownedMedicineList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        equalsVerifier(OwnedMedicine::class)
        val ownedMedicine1 = OwnedMedicine()
        ownedMedicine1.id = 1L
        val ownedMedicine2 = OwnedMedicine()
        ownedMedicine2.id = ownedMedicine1.id
        assertThat(ownedMedicine1).isEqualTo(ownedMedicine2)
        ownedMedicine2.id = 2L
        assertThat(ownedMedicine1).isNotEqualTo(ownedMedicine2)
        ownedMedicine1.id = null
        assertThat(ownedMedicine1).isNotEqualTo(ownedMedicine2)
    }

    companion object {

        private const val DEFAULT_DOSES: Int = 1
        private const val UPDATED_DOSES: Int = 2
        private const val SMALLER_DOSES: Int = 1 - 1

        private val DEFAULT_EXPIRATION_DATE: LocalDate = LocalDate.ofEpochDay(0L)
        private val UPDATED_EXPIRATION_DATE: LocalDate = LocalDate.now(ZoneId.systemDefault())
        private val SMALLER_EXPIRATION_DATE: LocalDate = LocalDate.ofEpochDay(-1L)

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): OwnedMedicine {
            val ownedMedicine = OwnedMedicine(
                doses = DEFAULT_DOSES,
                expirationDate = DEFAULT_EXPIRATION_DATE
            )

            return ownedMedicine
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): OwnedMedicine {
            val ownedMedicine = OwnedMedicine(
                doses = UPDATED_DOSES,
                expirationDate = UPDATED_EXPIRATION_DATE
            )

            return ownedMedicine
        }
    }
}
