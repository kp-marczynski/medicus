package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.domain.Appointment
import pl.marczynski.medicus.repository.AppointmentRepository
import pl.marczynski.medicus.web.rest.errors.BadRequestAlertException

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.PaginationUtil
import io.github.jhipster.web.util.ResponseUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.marczynski.medicus.repository.UserRepository

import javax.validation.Valid
import java.net.URI
import java.net.URISyntaxException

private const val ENTITY_NAME = "appointment"

/**
 * REST controller for managing [pl.marczynski.medicus.domain.Appointment].
 */
@RestController
@RequestMapping("/api")
class AppointmentResource(
    private val appointmentRepository: AppointmentRepository,
    private val userRepository: UserRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /appointments` : Create a new appointment.
     *
     * @param appointment the appointment to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new appointment, or with status `400 (Bad Request)` if the appointment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/appointments")
    fun createAppointment(@Valid @RequestBody appointment: Appointment): ResponseEntity<Appointment> {
        log.debug("REST request to save Appointment : {}", appointment)
        if (appointment.id != null) {
            throw BadRequestAlertException(
                "A new appointment cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        appointment.user = userRepository.findByUserIsCurrentUser().orElse(null)
        val result = appointmentRepository.save(appointment)
        return ResponseEntity.created(URI("/api/appointments/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /appointments` : Updates an existing appointment.
     *
     * @param appointment the appointment to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated appointment,
     * or with status `400 (Bad Request)` if the appointment is not valid,
     * or with status `500 (Internal Server Error)` if the appointment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/appointments")
    fun updateAppointment(@Valid @RequestBody appointment: Appointment): ResponseEntity<Appointment> {
        log.debug("REST request to update Appointment : {}", appointment)
        if (appointment.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!appointmentRepository.checkUserRightsById(appointment.id!!)) {
            throw BadRequestAlertException(
                "User must be owner od the entity",
                ENTITY_NAME, "notowner"
            )
        }
        val result = appointmentRepository.save(appointment)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     appointment.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /appointments` : get all the appointments.
     *

     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the [ResponseEntity] with status `200 (OK)` and the list of appointments in body.
     */
    @GetMapping("/appointments")
    fun getAllAppointments(
        pageable: Pageable,
        @RequestParam(required = false, defaultValue = "false") eagerload: Boolean
    ): ResponseEntity<MutableList<Appointment>> {
        log.debug("REST request to get a page of Appointments")
        val page: Page<Appointment> = if (eagerload) {
            appointmentRepository.findAllWithEagerRelationships(pageable)
        } else {
            appointmentRepository.findAll(pageable)
        }
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /appointments/:id` : get the "id" appointment.
     *
     * @param id the id of the appointment to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the appointment, or with status `404 (Not Found)`.
     */
    @GetMapping("/appointments/{id}")
    fun getAppointment(@PathVariable id: Long): ResponseEntity<Appointment> {
        log.debug("REST request to get Appointment : {}", id)
        if (!appointmentRepository.checkUserRightsById(id)) {
            throw BadRequestAlertException(
                "User must be owner od the entity",
                ENTITY_NAME, "notowner"
            )
        }
        val appointment = appointmentRepository.findOneWithEagerRelationships(id)
        return ResponseUtil.wrapOrNotFound(appointment)
    }

    /**
     *  `DELETE  /appointments/:id` : delete the "id" appointment.
     *
     * @param id the id of the appointment to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/appointments/{id}")
    fun deleteAppointment(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Appointment : {}", id)
        if (!appointmentRepository.checkUserRightsById(id)) {
            throw BadRequestAlertException(
                "User must be owner od the entity",
                ENTITY_NAME, "notowner"
            )
        }
        appointmentRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
