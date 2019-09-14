package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.domain.OwnedMedicine
import pl.marczynski.medicus.repository.OwnedMedicineRepository
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

private const val ENTITY_NAME = "ownedMedicine"

/**
 * REST controller for managing [pl.marczynski.medicus.domain.OwnedMedicine].
 */
@RestController
@RequestMapping("/api")
class OwnedMedicineResource(
    private val ownedMedicineRepository: OwnedMedicineRepository,
    private val userRepository: UserRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /owned-medicines` : Create a new ownedMedicine.
     *
     * @param ownedMedicine the ownedMedicine to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new ownedMedicine, or with status `400 (Bad Request)` if the ownedMedicine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/owned-medicines")
    fun createOwnedMedicine(@Valid @RequestBody ownedMedicine: OwnedMedicine): ResponseEntity<OwnedMedicine> {
        log.debug("REST request to save OwnedMedicine : {}", ownedMedicine)
        if (ownedMedicine.id != null) {
            throw BadRequestAlertException(
                "A new ownedMedicine cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        ownedMedicine.user = userRepository.findByUserIsCurrentUser().orElse(null)
        val result = ownedMedicineRepository.save(ownedMedicine)
        return ResponseEntity.created(URI("/api/owned-medicines/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /owned-medicines` : Updates an existing ownedMedicine.
     *
     * @param ownedMedicine the ownedMedicine to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated ownedMedicine,
     * or with status `400 (Bad Request)` if the ownedMedicine is not valid,
     * or with status `500 (Internal Server Error)` if the ownedMedicine couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/owned-medicines")
    fun updateOwnedMedicine(@Valid @RequestBody ownedMedicine: OwnedMedicine): ResponseEntity<OwnedMedicine> {
        log.debug("REST request to update OwnedMedicine : {}", ownedMedicine)
        if (ownedMedicine.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!ownedMedicineRepository.checkUserRightsById(ownedMedicine.id!!)) {
            throw BadRequestAlertException(
                "User must be owner od the entity",
                ENTITY_NAME, "notowner"
            )
        }
        val result = ownedMedicineRepository.save(ownedMedicine)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     ownedMedicine.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /owned-medicines` : get all the ownedMedicines.
     *

     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the [ResponseEntity] with status `200 (OK)` and the list of ownedMedicines in body.
     */
    @GetMapping("/owned-medicines")
    fun getAllOwnedMedicines(
        pageable: Pageable,
        @RequestParam(required = false, defaultValue = "false") eagerload: Boolean
    ): ResponseEntity<MutableList<OwnedMedicine>> {
        log.debug("REST request to get a page of OwnedMedicines")
        val page: Page<OwnedMedicine> = if (eagerload) {
            ownedMedicineRepository.findAllWithEagerRelationships(pageable)
        } else {
            ownedMedicineRepository.findAll(pageable)
        }
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /owned-medicines/:id` : get the "id" ownedMedicine.
     *
     * @param id the id of the ownedMedicine to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the ownedMedicine, or with status `404 (Not Found)`.
     */
    @GetMapping("/owned-medicines/{id}")
    fun getOwnedMedicine(@PathVariable id: Long): ResponseEntity<OwnedMedicine> {
        log.debug("REST request to get OwnedMedicine : {}", id)
        if (!ownedMedicineRepository.checkUserRightsById(id)) {
            throw BadRequestAlertException(
                "User must be owner od the entity",
                ENTITY_NAME, "notowner"
            )
        }
        val ownedMedicine = ownedMedicineRepository.findOneWithEagerRelationships(id)
        return ResponseUtil.wrapOrNotFound(ownedMedicine)
    }

    /**
     *  `DELETE  /owned-medicines/:id` : delete the "id" ownedMedicine.
     *
     * @param id the id of the ownedMedicine to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/owned-medicines/{id}")
    fun deleteOwnedMedicine(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete OwnedMedicine : {}", id)
        if (!ownedMedicineRepository.checkUserRightsById(id)) {
            throw BadRequestAlertException(
                "User must be owner od the entity",
                ENTITY_NAME, "notowner"
            )
        }
        ownedMedicineRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
