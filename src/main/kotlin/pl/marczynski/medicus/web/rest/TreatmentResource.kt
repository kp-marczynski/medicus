package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.domain.Treatment
import pl.marczynski.medicus.repository.TreatmentRepository
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

private const val ENTITY_NAME = "treatment"

/**
 * REST controller for managing [pl.marczynski.medicus.domain.Treatment].
 */
@RestController
@RequestMapping("/api")
class TreatmentResource(
    private val treatmentRepository: TreatmentRepository,
    private val userRepository: UserRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /treatments` : Create a new treatment.
     *
     * @param treatment the treatment to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new treatment, or with status `400 (Bad Request)` if the treatment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/treatments")
    fun createTreatment(@Valid @RequestBody treatment: Treatment): ResponseEntity<Treatment> {
        log.debug("REST request to save Treatment : {}", treatment)
        if (treatment.id != null) {
            throw BadRequestAlertException(
                "A new treatment cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        treatment.user = userRepository.findByUserIsCurrentUser().orElse(null)
        val result = treatmentRepository.save(treatment)
        return ResponseEntity.created(URI("/api/treatments/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /treatments` : Updates an existing treatment.
     *
     * @param treatment the treatment to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated treatment,
     * or with status `400 (Bad Request)` if the treatment is not valid,
     * or with status `500 (Internal Server Error)` if the treatment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/treatments")
    fun updateTreatment(@Valid @RequestBody treatment: Treatment): ResponseEntity<Treatment> {
        log.debug("REST request to update Treatment : {}", treatment)
        if (treatment.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!treatmentRepository.checkUserRightsById(treatment.id!!)) {
            throw BadRequestAlertException(
                "User must be owner od the entity",
                ENTITY_NAME, "notowner"
            )
        }
        val result = treatmentRepository.save(treatment)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     treatment.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /treatments` : get all the treatments.
     *

     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the [ResponseEntity] with status `200 (OK)` and the list of treatments in body.
     */
    @GetMapping("/treatments")
    fun getAllTreatments(
        pageable: Pageable,
        @RequestParam(required = false, defaultValue = "false") eagerload: Boolean
    ): ResponseEntity<MutableList<Treatment>> {
        log.debug("REST request to get a page of Treatments")
        val page: Page<Treatment> = if (eagerload) {
            treatmentRepository.findAllWithEagerRelationships(pageable)
        } else {
            treatmentRepository.findAll(pageable)
        }
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /treatments/:id` : get the "id" treatment.
     *
     * @param id the id of the treatment to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the treatment, or with status `404 (Not Found)`.
     */
    @GetMapping("/treatments/{id}")
    fun getTreatment(@PathVariable id: Long): ResponseEntity<Treatment> {
        log.debug("REST request to get Treatment : {}", id)
        if (!treatmentRepository.checkUserRightsById(id)) {
            throw BadRequestAlertException(
                "User must be owner od the entity",
                ENTITY_NAME, "notowner"
            )
        }
        val treatment = treatmentRepository.findOneWithEagerRelationships(id)
        return ResponseUtil.wrapOrNotFound(treatment)
    }

    /**
     *  `DELETE  /treatments/:id` : delete the "id" treatment.
     *
     * @param id the id of the treatment to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/treatments/{id}")
    fun deleteTreatment(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Treatment : {}", id)
        if (!treatmentRepository.checkUserRightsById(id)) {
            throw BadRequestAlertException(
                "User must be owner od the entity",
                ENTITY_NAME, "notowner"
            )
        }
        treatmentRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
