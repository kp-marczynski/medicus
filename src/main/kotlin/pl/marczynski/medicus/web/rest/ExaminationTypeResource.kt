package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.domain.ExaminationType
import pl.marczynski.medicus.repository.ExaminationTypeRepository
import pl.marczynski.medicus.web.rest.errors.BadRequestAlertException

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
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

private const val ENTITY_NAME = "examinationType"

/**
 * REST controller for managing [pl.marczynski.medicus.domain.ExaminationType].
 */
@RestController
@RequestMapping("/api")
class ExaminationTypeResource(
    private val examinationTypeRepository: ExaminationTypeRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /examination-types` : Create a new examinationType.
     *
     * @param examinationType the examinationType to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new examinationType, or with status `400 (Bad Request)` if the examinationType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/examination-types")
    fun createExaminationType(@Valid @RequestBody examinationType: ExaminationType): ResponseEntity<ExaminationType> {
        log.debug("REST request to save ExaminationType : {}", examinationType)
        if (examinationType.id != null) {
            throw BadRequestAlertException(
                "A new examinationType cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = examinationTypeRepository.save(examinationType)
        return ResponseEntity.created(URI("/api/examination-types/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /examination-types` : Updates an existing examinationType.
     *
     * @param examinationType the examinationType to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated examinationType,
     * or with status `400 (Bad Request)` if the examinationType is not valid,
     * or with status `500 (Internal Server Error)` if the examinationType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/examination-types")
    fun updateExaminationType(@Valid @RequestBody examinationType: ExaminationType): ResponseEntity<ExaminationType> {
        log.debug("REST request to update ExaminationType : {}", examinationType)
        if (examinationType.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = examinationTypeRepository.save(examinationType)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     examinationType.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /examination-types` : get all the examinationTypes.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of examinationTypes in body.
     */
    @GetMapping("/examination-types")
    fun getAllExaminationTypes(): MutableList<ExaminationType> {
        log.debug("REST request to get all ExaminationTypes")
        return examinationTypeRepository.findAll()
    }

    /**
     * `GET  /examination-types/:id` : get the "id" examinationType.
     *
     * @param id the id of the examinationType to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the examinationType, or with status `404 (Not Found)`.
     */
    @GetMapping("/examination-types/{id}")
    fun getExaminationType(@PathVariable id: Long): ResponseEntity<ExaminationType> {
        log.debug("REST request to get ExaminationType : {}", id)
        val examinationType = examinationTypeRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(examinationType)
    }

    /**
     *  `DELETE  /examination-types/:id` : delete the "id" examinationType.
     *
     * @param id the id of the examinationType to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/examination-types/{id}")
    fun deleteExaminationType(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete ExaminationType : {}", id)

        examinationTypeRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
