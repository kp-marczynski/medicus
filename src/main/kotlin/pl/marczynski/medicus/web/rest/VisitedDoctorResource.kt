package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.domain.VisitedDoctor
import pl.marczynski.medicus.repository.VisitedDoctorRepository
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

import java.net.URI
import java.net.URISyntaxException

private const val ENTITY_NAME = "visitedDoctor"

/**
 * REST controller for managing [pl.marczynski.medicus.domain.VisitedDoctor].
 */
@RestController
@RequestMapping("/api")
class VisitedDoctorResource(
    private val visitedDoctorRepository: VisitedDoctorRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /visited-doctors` : Create a new visitedDoctor.
     *
     * @param visitedDoctor the visitedDoctor to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new visitedDoctor, or with status `400 (Bad Request)` if the visitedDoctor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/visited-doctors")
    fun createVisitedDoctor(@RequestBody visitedDoctor: VisitedDoctor): ResponseEntity<VisitedDoctor> {
        log.debug("REST request to save VisitedDoctor : {}", visitedDoctor)
        if (visitedDoctor.id != null) {
            throw BadRequestAlertException(
                "A new visitedDoctor cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = visitedDoctorRepository.save(visitedDoctor)
        return ResponseEntity.created(URI("/api/visited-doctors/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /visited-doctors` : Updates an existing visitedDoctor.
     *
     * @param visitedDoctor the visitedDoctor to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated visitedDoctor,
     * or with status `400 (Bad Request)` if the visitedDoctor is not valid,
     * or with status `500 (Internal Server Error)` if the visitedDoctor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/visited-doctors")
    fun updateVisitedDoctor(@RequestBody visitedDoctor: VisitedDoctor): ResponseEntity<VisitedDoctor> {
        log.debug("REST request to update VisitedDoctor : {}", visitedDoctor)
        if (visitedDoctor.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = visitedDoctorRepository.save(visitedDoctor)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     visitedDoctor.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /visited-doctors` : get all the visitedDoctors.
     *

     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of visitedDoctors in body.
     */
    @GetMapping("/visited-doctors")
    fun getAllVisitedDoctors(
        pageable: Pageable
    ): ResponseEntity<MutableList<VisitedDoctor>> {
        log.debug("REST request to get a page of VisitedDoctors")
        val page = visitedDoctorRepository.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /visited-doctors/:id` : get the "id" visitedDoctor.
     *
     * @param id the id of the visitedDoctor to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the visitedDoctor, or with status `404 (Not Found)`.
     */
    @GetMapping("/visited-doctors/{id}")
    fun getVisitedDoctor(@PathVariable id: Long): ResponseEntity<VisitedDoctor> {
        log.debug("REST request to get VisitedDoctor : {}", id)
        val visitedDoctor = visitedDoctorRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(visitedDoctor)
    }

    /**
     *  `DELETE  /visited-doctors/:id` : delete the "id" visitedDoctor.
     *
     * @param id the id of the visitedDoctor to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/visited-doctors/{id}")
    fun deleteVisitedDoctor(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete VisitedDoctor : {}", id)

        visitedDoctorRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
