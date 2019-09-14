package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.domain.ExaminationPackage
import pl.marczynski.medicus.repository.ExaminationPackageRepository
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

private const val ENTITY_NAME = "examinationPackage"

/**
 * REST controller for managing [pl.marczynski.medicus.domain.ExaminationPackage].
 */
@RestController
@RequestMapping("/api")
class ExaminationPackageResource(
    private val examinationPackageRepository: ExaminationPackageRepository,
    private val userRepository: UserRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /examination-packages` : Create a new examinationPackage.
     *
     * @param examinationPackage the examinationPackage to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new examinationPackage, or with status `400 (Bad Request)` if the examinationPackage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/examination-packages")
    fun createExaminationPackage(@Valid @RequestBody examinationPackage: ExaminationPackage): ResponseEntity<ExaminationPackage> {
        log.debug("REST request to save ExaminationPackage : {}", examinationPackage)
        if (examinationPackage.id != null) {
            throw BadRequestAlertException(
                "A new examinationPackage cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        examinationPackage.user = userRepository.findByUserIsCurrentUser().orElse(null)
        val result = examinationPackageRepository.save(examinationPackage)
        return ResponseEntity.created(URI("/api/examination-packages/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /examination-packages` : Updates an existing examinationPackage.
     *
     * @param examinationPackage the examinationPackage to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated examinationPackage,
     * or with status `400 (Bad Request)` if the examinationPackage is not valid,
     * or with status `500 (Internal Server Error)` if the examinationPackage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/examination-packages")
    fun updateExaminationPackage(@Valid @RequestBody examinationPackage: ExaminationPackage): ResponseEntity<ExaminationPackage> {
        log.debug("REST request to update ExaminationPackage : {}", examinationPackage)
        if (examinationPackage.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!examinationPackageRepository.checkUserRightsById(examinationPackage.id!!)) {
            throw BadRequestAlertException(
                "User must be owner od the entity",
                ENTITY_NAME, "notowner"
            )
        }
        val result = examinationPackageRepository.save(examinationPackage)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     examinationPackage.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /examination-packages` : get all the examinationPackages.
     *

     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of examinationPackages in body.
     */
    @GetMapping("/examination-packages")
    fun getAllExaminationPackages(
        pageable: Pageable
    ): ResponseEntity<MutableList<ExaminationPackage>> {
        log.debug("REST request to get a page of ExaminationPackages")
        val page = examinationPackageRepository.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /examination-packages/:id` : get the "id" examinationPackage.
     *
     * @param id the id of the examinationPackage to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the examinationPackage, or with status `404 (Not Found)`.
     */
    @GetMapping("/examination-packages/{id}")
    fun getExaminationPackage(@PathVariable id: Long): ResponseEntity<ExaminationPackage> {
        log.debug("REST request to get ExaminationPackage : {}", id)
        if (!examinationPackageRepository.checkUserRightsById(id)) {
            throw BadRequestAlertException(
                "User must be owner od the entity",
                ENTITY_NAME, "notowner"
            )
        }
        val examinationPackage = examinationPackageRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(examinationPackage)
    }

    /**
     *  `DELETE  /examination-packages/:id` : delete the "id" examinationPackage.
     *
     * @param id the id of the examinationPackage to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/examination-packages/{id}")
    fun deleteExaminationPackage(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete ExaminationPackage : {}", id)
        if (!examinationPackageRepository.checkUserRightsById(id)) {
            throw BadRequestAlertException(
                "User must be owner od the entity",
                ENTITY_NAME, "notowner"
            )
        }
        examinationPackageRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
