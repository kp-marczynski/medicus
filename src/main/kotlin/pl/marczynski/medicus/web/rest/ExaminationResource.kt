package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.domain.Examination
import pl.marczynski.medicus.repository.ExaminationRepository
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

private const val ENTITY_NAME = "examination"

/**
 * REST controller for managing [pl.marczynski.medicus.domain.Examination].
 */
@RestController
@RequestMapping("/api")
class ExaminationResource(
    private val examinationRepository: ExaminationRepository,
    private val userRepository: UserRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /examinations` : Create a new examination.
     *
     * @param examination the examination to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new examination, or with status `400 (Bad Request)` if the examination has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/examinations")
    fun createExamination(@Valid @RequestBody examination: Examination): ResponseEntity<Examination> {
        log.debug("REST request to save Examination : {}", examination)
        if (examination.id != null) {
            throw BadRequestAlertException(
                "A new examination cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        examination.user = userRepository.findByUserIsCurrentUser().orElse(null)
        val result = examinationRepository.save(examination)
        return ResponseEntity.created(URI("/api/examinations/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /examinations` : Updates an existing examination.
     *
     * @param examination the examination to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated examination,
     * or with status `400 (Bad Request)` if the examination is not valid,
     * or with status `500 (Internal Server Error)` if the examination couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/examinations")
    fun updateExamination(@Valid @RequestBody examination: Examination): ResponseEntity<Examination> {
        log.debug("REST request to update Examination : {}", examination)
        if (examination.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!examinationRepository.checkUserRightsById(examination.id!!)) {
            throw BadRequestAlertException(
                "User must be owner od the entity",
                ENTITY_NAME, "notowner"
            )
        }
        val result = examinationRepository.save(examination)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    examination.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /examinations` : get all the examinations.
     *

     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of examinations in body.
     */
    @GetMapping("/examinations")
    fun getAllExaminations(
        pageable: Pageable
    ): ResponseEntity<MutableList<Examination>> {
        log.debug("REST request to get a page of Examinations")
        val page = examinationRepository.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /examinations/:id` : get the "id" examination.
     *
     * @param id the id of the examination to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the examination, or with status `404 (Not Found)`.
     */
    @GetMapping("/examinations/{id}")
    fun getExamination(@PathVariable id: Long): ResponseEntity<Examination> {
        log.debug("REST request to get Examination : {}", id)
        if (!examinationRepository.checkUserRightsById(id)) {
            throw BadRequestAlertException(
                "User must be owner od the entity",
                ENTITY_NAME, "notowner"
            )
        }
        val examination = examinationRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(examination)
    }

    /**
     *  `DELETE  /examinations/:id` : delete the "id" examination.
     *
     * @param id the id of the examination to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/examinations/{id}")
    fun deleteExamination(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Examination : {}", id)
        if (!examinationRepository.checkUserRightsById(id)) {
            throw BadRequestAlertException(
                "User must be owner od the entity",
                ENTITY_NAME, "notowner"
            )
        }
        examinationRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
