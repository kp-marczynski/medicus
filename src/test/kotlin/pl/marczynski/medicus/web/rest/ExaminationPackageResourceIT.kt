package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.MedicusApp
import pl.marczynski.medicus.domain.ExaminationPackage
import pl.marczynski.medicus.repository.ExaminationPackageRepository
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
 * Integration tests for the [ExaminationPackageResource] REST controller.
 *
 * @see ExaminationPackageResource
 */
@SpringBootTest(classes = [MedicusApp::class])
class ExaminationPackageResourceIT {

    @Autowired
    private lateinit var examinationPackageRepository: ExaminationPackageRepository

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

    private lateinit var restExaminationPackageMockMvc: MockMvc

    private lateinit var examinationPackage: ExaminationPackage

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val examinationPackageResource = ExaminationPackageResource(examinationPackageRepository)
        this.restExaminationPackageMockMvc = MockMvcBuilders.standaloneSetup(examinationPackageResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        examinationPackage = createEntity(em)
    }

    @Test
    @Transactional
    fun createExaminationPackage() {
        val databaseSizeBeforeCreate = examinationPackageRepository.findAll().size

        // Create the ExaminationPackage
        restExaminationPackageMockMvc.perform(
            post("/api/examination-packages")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(examinationPackage))
        ).andExpect(status().isCreated)

        // Validate the ExaminationPackage in the database
        val examinationPackageList = examinationPackageRepository.findAll()
        assertThat(examinationPackageList).hasSize(databaseSizeBeforeCreate + 1)
        val testExaminationPackage = examinationPackageList[examinationPackageList.size - 1]
        assertThat(testExaminationPackage.date).isEqualTo(DEFAULT_DATE)
        assertThat(testExaminationPackage.title).isEqualTo(DEFAULT_TITLE)
        assertThat(testExaminationPackage.examinationPackageScan).isEqualTo(DEFAULT_EXAMINATION_PACKAGE_SCAN)
        assertThat(testExaminationPackage.examinationPackageScanContentType).isEqualTo(DEFAULT_EXAMINATION_PACKAGE_SCAN_CONTENT_TYPE)
    }

    @Test
    @Transactional
    fun createExaminationPackageWithExistingId() {
        val databaseSizeBeforeCreate = examinationPackageRepository.findAll().size

        // Create the ExaminationPackage with an existing ID
        examinationPackage.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restExaminationPackageMockMvc.perform(
            post("/api/examination-packages")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(examinationPackage))
        ).andExpect(status().isBadRequest)

        // Validate the ExaminationPackage in the database
        val examinationPackageList = examinationPackageRepository.findAll()
        assertThat(examinationPackageList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkDateIsRequired() {
        val databaseSizeBeforeTest = examinationPackageRepository.findAll().size
        // set the field null
        examinationPackage.date = null

        // Create the ExaminationPackage, which fails.

        restExaminationPackageMockMvc.perform(
            post("/api/examination-packages")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(examinationPackage))
        ).andExpect(status().isBadRequest)

        val examinationPackageList = examinationPackageRepository.findAll()
        assertThat(examinationPackageList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkTitleIsRequired() {
        val databaseSizeBeforeTest = examinationPackageRepository.findAll().size
        // set the field null
        examinationPackage.title = null

        // Create the ExaminationPackage, which fails.

        restExaminationPackageMockMvc.perform(
            post("/api/examination-packages")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(examinationPackage))
        ).andExpect(status().isBadRequest)

        val examinationPackageList = examinationPackageRepository.findAll()
        assertThat(examinationPackageList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllExaminationPackages() {
        // Initialize the database
        examinationPackageRepository.saveAndFlush(examinationPackage)

        // Get all the examinationPackageList
        restExaminationPackageMockMvc.perform(get("/api/examination-packages?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(examinationPackage.id?.toInt())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].examinationPackageScanContentType").value(hasItem(DEFAULT_EXAMINATION_PACKAGE_SCAN_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].examinationPackageScan").value(hasItem(Base64Utils.encodeToString(DEFAULT_EXAMINATION_PACKAGE_SCAN))))
    }

    @Test
    @Transactional
    fun getExaminationPackage() {
        // Initialize the database
        examinationPackageRepository.saveAndFlush(examinationPackage)

        val id = examinationPackage.id
        assertNotNull(id)

        // Get the examinationPackage
        restExaminationPackageMockMvc.perform(get("/api/examination-packages/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.examinationPackageScanContentType").value(DEFAULT_EXAMINATION_PACKAGE_SCAN_CONTENT_TYPE))
            .andExpect(jsonPath("$.examinationPackageScan").value(Base64Utils.encodeToString(DEFAULT_EXAMINATION_PACKAGE_SCAN)))
    }

    @Test
    @Transactional
    fun getNonExistingExaminationPackage() {
        // Get the examinationPackage
        restExaminationPackageMockMvc.perform(get("/api/examination-packages/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateExaminationPackage() {
        // Initialize the database
        examinationPackageRepository.saveAndFlush(examinationPackage)

        val databaseSizeBeforeUpdate = examinationPackageRepository.findAll().size

        // Update the examinationPackage
        val id = examinationPackage.id
        assertNotNull(id)
        val updatedExaminationPackage = examinationPackageRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedExaminationPackage are not directly saved in db
        em.detach(updatedExaminationPackage)
        updatedExaminationPackage.date = UPDATED_DATE
        updatedExaminationPackage.title = UPDATED_TITLE
        updatedExaminationPackage.examinationPackageScan = UPDATED_EXAMINATION_PACKAGE_SCAN
        updatedExaminationPackage.examinationPackageScanContentType = UPDATED_EXAMINATION_PACKAGE_SCAN_CONTENT_TYPE

        restExaminationPackageMockMvc.perform(
            put("/api/examination-packages")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(updatedExaminationPackage))
        ).andExpect(status().isOk)

        // Validate the ExaminationPackage in the database
        val examinationPackageList = examinationPackageRepository.findAll()
        assertThat(examinationPackageList).hasSize(databaseSizeBeforeUpdate)
        val testExaminationPackage = examinationPackageList[examinationPackageList.size - 1]
        assertThat(testExaminationPackage.date).isEqualTo(UPDATED_DATE)
        assertThat(testExaminationPackage.title).isEqualTo(UPDATED_TITLE)
        assertThat(testExaminationPackage.examinationPackageScan).isEqualTo(UPDATED_EXAMINATION_PACKAGE_SCAN)
        assertThat(testExaminationPackage.examinationPackageScanContentType).isEqualTo(UPDATED_EXAMINATION_PACKAGE_SCAN_CONTENT_TYPE)
    }

    @Test
    @Transactional
    fun updateNonExistingExaminationPackage() {
        val databaseSizeBeforeUpdate = examinationPackageRepository.findAll().size

        // Create the ExaminationPackage

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExaminationPackageMockMvc.perform(
            put("/api/examination-packages")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(examinationPackage))
        ).andExpect(status().isBadRequest)

        // Validate the ExaminationPackage in the database
        val examinationPackageList = examinationPackageRepository.findAll()
        assertThat(examinationPackageList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteExaminationPackage() {
        // Initialize the database
        examinationPackageRepository.saveAndFlush(examinationPackage)

        val databaseSizeBeforeDelete = examinationPackageRepository.findAll().size

        val id = examinationPackage.id
        assertNotNull(id)

        // Delete the examinationPackage
        restExaminationPackageMockMvc.perform(
            delete("/api/examination-packages/{id}", id)
                .accept(APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val examinationPackageList = examinationPackageRepository.findAll()
        assertThat(examinationPackageList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        equalsVerifier(ExaminationPackage::class)
        val examinationPackage1 = ExaminationPackage()
        examinationPackage1.id = 1L
        val examinationPackage2 = ExaminationPackage()
        examinationPackage2.id = examinationPackage1.id
        assertThat(examinationPackage1).isEqualTo(examinationPackage2)
        examinationPackage2.id = 2L
        assertThat(examinationPackage1).isNotEqualTo(examinationPackage2)
        examinationPackage1.id = null
        assertThat(examinationPackage1).isNotEqualTo(examinationPackage2)
    }

    companion object {

        private val DEFAULT_DATE: LocalDate = LocalDate.ofEpochDay(0L)
        private val UPDATED_DATE: LocalDate = LocalDate.now(ZoneId.systemDefault())
        private val SMALLER_DATE: LocalDate = LocalDate.ofEpochDay(-1L)

        private const val DEFAULT_TITLE: String = "AAAAAAAAAA"
        private const val UPDATED_TITLE = "BBBBBBBBBB"

        private val DEFAULT_EXAMINATION_PACKAGE_SCAN: ByteArray = createByteArray(1, "0")
        private val UPDATED_EXAMINATION_PACKAGE_SCAN: ByteArray = createByteArray(1, "1")
        private const val DEFAULT_EXAMINATION_PACKAGE_SCAN_CONTENT_TYPE: String = "image/jpg"
        private const val UPDATED_EXAMINATION_PACKAGE_SCAN_CONTENT_TYPE: String = "image/png"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): ExaminationPackage {
            val examinationPackage = ExaminationPackage(
                date = DEFAULT_DATE,
                title = DEFAULT_TITLE,
                examinationPackageScan = DEFAULT_EXAMINATION_PACKAGE_SCAN,
                examinationPackageScanContentType = DEFAULT_EXAMINATION_PACKAGE_SCAN_CONTENT_TYPE
            )

            return examinationPackage
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): ExaminationPackage {
            val examinationPackage = ExaminationPackage(
                date = UPDATED_DATE,
                title = UPDATED_TITLE,
                examinationPackageScan = UPDATED_EXAMINATION_PACKAGE_SCAN,
                examinationPackageScanContentType = UPDATED_EXAMINATION_PACKAGE_SCAN_CONTENT_TYPE
            )

            return examinationPackage
        }
    }
}
