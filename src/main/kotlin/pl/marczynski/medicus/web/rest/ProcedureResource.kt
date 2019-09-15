package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.domain.Procedure
import pl.marczynski.medicus.repository.ProcedureRepository
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
import org.springframework.web.bind.annotation.*
import pl.marczynski.medicus.repository.UserRepository

import javax.validation.Valid
import java.net.URI
import java.net.URISyntaxException

private const val ENTITY_NAME = "procedure"

/**
 * REST controller for managing [pl.marczynski.medicus.domain.Procedure].
 */
@RestController
@RequestMapping("/api")
class ProcedureResource(
    private val procedureRepository: ProcedureRepository,
    private val userRepository: UserRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /procedures` : Create a new procedure.
     *
     * @param procedure the procedure to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new procedure, or with status `400 (Bad Request)` if the procedure has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/procedures")
    fun createProcedure(@Valid @RequestBody procedure: Procedure): ResponseEntity<Procedure> {
        log.debug("REST request to save Procedure : {}", procedure)
        if (procedure.id != null) {
            throw BadRequestAlertException(
                "A new procedure cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        procedure.user = userRepository.findByUserIsCurrentUser().orElse(null)
        val result = procedureRepository.save(procedure)
        return ResponseEntity.created(URI("/api/procedures/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /procedures` : Updates an existing procedure.
     *
     * @param procedure the procedure to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated procedure,
     * or with status `400 (Bad Request)` if the procedure is not valid,
     * or with status `500 (Internal Server Error)` if the procedure couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/procedures")
    fun updateProcedure(@Valid @RequestBody procedure: Procedure): ResponseEntity<Procedure> {
        log.debug("REST request to update Procedure : {}", procedure)
        if (procedure.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!procedureRepository.checkUserRightsById(procedure.id!!)) {
            throw BadRequestAlertException(
                "User must be owner od the entity",
                ENTITY_NAME, "notowner"
            )
        }
        val result = procedureRepository.save(procedure)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    procedure.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /procedures` : get all the procedures.
     *

     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the [ResponseEntity] with status `200 (OK)` and the list of procedures in body.
     */
    @GetMapping("/procedures")
    fun getAllProcedures(
        pageable: Pageable,
        @RequestParam(required = false, defaultValue = "false") eagerload: Boolean
    ): ResponseEntity<MutableList<Procedure>> {
        log.debug("REST request to get a page of Procedures")
        val page: Page<Procedure> = if (eagerload) {
            procedureRepository.findAllWithEagerRelationships(pageable)
        } else {
            procedureRepository.findAll(pageable)
        }
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /procedures/:id` : get the "id" procedure.
     *
     * @param id the id of the procedure to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the procedure, or with status `404 (Not Found)`.
     */
    @GetMapping("/procedures/{id}")
    fun getProcedure(@PathVariable id: Long): ResponseEntity<Procedure> {
        log.debug("REST request to get Procedure : {}", id)
        if (!procedureRepository.checkUserRightsById(id)) {
            throw BadRequestAlertException(
                "User must be owner od the entity",
                ENTITY_NAME, "notowner"
            )
        }
        val procedure = procedureRepository.findOneWithEagerRelationships(id)
        return ResponseUtil.wrapOrNotFound(procedure)
    }

    /**
     *  `DELETE  /procedures/:id` : delete the "id" procedure.
     *
     * @param id the id of the procedure to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/procedures/{id}")
    fun deleteProcedure(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Procedure : {}", id)
        if (!procedureRepository.checkUserRightsById(id)) {
            throw BadRequestAlertException(
                "User must be owner od the entity",
                ENTITY_NAME, "notowner"
            )
        }
        procedureRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
