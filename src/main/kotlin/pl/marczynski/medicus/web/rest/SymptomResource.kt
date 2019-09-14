package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.domain.Symptom
import pl.marczynski.medicus.repository.SymptomRepository
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

import javax.validation.Valid
import java.net.URI
import java.net.URISyntaxException

private const val ENTITY_NAME = "symptom"

/**
 * REST controller for managing [pl.marczynski.medicus.domain.Symptom].
 */
@RestController
@RequestMapping("/api")
class SymptomResource(
    private val symptomRepository: SymptomRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /symptoms` : Create a new symptom.
     *
     * @param symptom the symptom to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new symptom, or with status `400 (Bad Request)` if the symptom has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/symptoms")
    fun createSymptom(@Valid @RequestBody symptom: Symptom): ResponseEntity<Symptom> {
        log.debug("REST request to save Symptom : {}", symptom)
        if (symptom.id != null) {
            throw BadRequestAlertException(
                "A new symptom cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = symptomRepository.save(symptom)
        return ResponseEntity.created(URI("/api/symptoms/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /symptoms` : Updates an existing symptom.
     *
     * @param symptom the symptom to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated symptom,
     * or with status `400 (Bad Request)` if the symptom is not valid,
     * or with status `500 (Internal Server Error)` if the symptom couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/symptoms")
    fun updateSymptom(@Valid @RequestBody symptom: Symptom): ResponseEntity<Symptom> {
        log.debug("REST request to update Symptom : {}", symptom)
        if (symptom.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = symptomRepository.save(symptom)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     symptom.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /symptoms` : get all the symptoms.
     *

     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of symptoms in body.
     */
    @GetMapping("/symptoms")
    fun getAllSymptoms(
        pageable: Pageable
    ): ResponseEntity<MutableList<Symptom>> {
        log.debug("REST request to get a page of Symptoms")
        val page = symptomRepository.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /symptoms/:id` : get the "id" symptom.
     *
     * @param id the id of the symptom to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the symptom, or with status `404 (Not Found)`.
     */
    @GetMapping("/symptoms/{id}")
    fun getSymptom(@PathVariable id: Long): ResponseEntity<Symptom> {
        log.debug("REST request to get Symptom : {}", id)
        val symptom = symptomRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(symptom)
    }

    /**
     *  `DELETE  /symptoms/:id` : delete the "id" symptom.
     *
     * @param id the id of the symptom to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/symptoms/{id}")
    fun deleteSymptom(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Symptom : {}", id)

        symptomRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
