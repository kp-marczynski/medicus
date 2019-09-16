package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.domain.Doctor
import pl.marczynski.medicus.repository.DoctorRepository
import pl.marczynski.medicus.web.rest.errors.BadRequestAlertException

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.PaginationUtil
import io.github.jhipster.web.util.ResponseUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.marczynski.medicus.repository.UserRepository

import javax.validation.Valid
import java.net.URI
import java.net.URISyntaxException

private const val ENTITY_NAME = "doctor"

/**
 * REST controller for managing [pl.marczynski.medicus.domain.Doctor].
 */
@RestController
@RequestMapping("/api")
class DoctorResource(
    private val doctorRepository: DoctorRepository,
    private val userRepository: UserRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /doctors` : Create a new doctor.
     *
     * @param doctor the doctor to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new doctor, or with status `400 (Bad Request)` if the doctor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/doctors")
    fun createDoctor(@Valid @RequestBody doctor: Doctor): ResponseEntity<Doctor> {
        log.debug("REST request to save Doctor : {}", doctor)
        if (doctor.id != null) {
            throw BadRequestAlertException(
                "A new doctor cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        doctor.language = userRepository.getCurrentUserLanguage().orElse(null)
        val result = doctorRepository.save(doctor)
        return ResponseEntity.created(URI("/api/doctors/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /doctors` : Updates an existing doctor.
     *
     * @param doctor the doctor to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated doctor,
     * or with status `400 (Bad Request)` if the doctor is not valid,
     * or with status `500 (Internal Server Error)` if the doctor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/doctors")
    fun updateDoctor(@Valid @RequestBody doctor: Doctor): ResponseEntity<Doctor> {
        log.debug("REST request to update Doctor : {}", doctor)
        if (doctor.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = doctorRepository.save(doctor)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    doctor.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /doctors` : get all the doctors.
     *

     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of doctors in body.
     */
    @GetMapping("/doctors")
    fun getAllDoctors(
        pageable: Pageable
    ): ResponseEntity<MutableList<Doctor>> {
        log.debug("REST request to get a page of Doctors")
        val page = doctorRepository.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /doctors/:id` : get the "id" doctor.
     *
     * @param id the id of the doctor to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the doctor, or with status `404 (Not Found)`.
     */
    @GetMapping("/doctors/{id}")
    fun getDoctor(@PathVariable id: Long): ResponseEntity<Doctor> {
        log.debug("REST request to get Doctor : {}", id)
        val doctor = doctorRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(doctor)
    }

    /**
     *  `DELETE  /doctors/:id` : delete the "id" doctor.
     *
     * @param id the id of the doctor to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/doctors/{id}")
    fun deleteDoctor(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Doctor : {}", id)

        doctorRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
