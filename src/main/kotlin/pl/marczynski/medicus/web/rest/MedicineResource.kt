package pl.marczynski.medicus.web.rest

import pl.marczynski.medicus.domain.Medicine
import pl.marczynski.medicus.repository.MedicineRepository
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

private const val ENTITY_NAME = "medicine"

/**
 * REST controller for managing [pl.marczynski.medicus.domain.Medicine].
 */
@RestController
@RequestMapping("/api")
class MedicineResource(
    private val medicineRepository: MedicineRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /medicines` : Create a new medicine.
     *
     * @param medicine the medicine to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new medicine, or with status `400 (Bad Request)` if the medicine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/medicines")
    fun createMedicine(@Valid @RequestBody medicine: Medicine): ResponseEntity<Medicine> {
        log.debug("REST request to save Medicine : {}", medicine)
        if (medicine.id != null) {
            throw BadRequestAlertException(
                "A new medicine cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = medicineRepository.save(medicine)
        return ResponseEntity.created(URI("/api/medicines/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /medicines` : Updates an existing medicine.
     *
     * @param medicine the medicine to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated medicine,
     * or with status `400 (Bad Request)` if the medicine is not valid,
     * or with status `500 (Internal Server Error)` if the medicine couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/medicines")
    fun updateMedicine(@Valid @RequestBody medicine: Medicine): ResponseEntity<Medicine> {
        log.debug("REST request to update Medicine : {}", medicine)
        if (medicine.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = medicineRepository.save(medicine)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    medicine.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /medicines` : get all the medicines.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of medicines in body.
     */
    @GetMapping("/medicines")
    fun getAllMedicines(): MutableList<Medicine> {
        log.debug("REST request to get all Medicines")
        return medicineRepository.findAll()
    }

    /**
     * `GET  /medicines/:id` : get the "id" medicine.
     *
     * @param id the id of the medicine to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the medicine, or with status `404 (Not Found)`.
     */
    @GetMapping("/medicines/{id}")
    fun getMedicine(@PathVariable id: Long): ResponseEntity<Medicine> {
        log.debug("REST request to get Medicine : {}", id)
        val medicine = medicineRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(medicine)
    }

    /**
     *  `DELETE  /medicines/:id` : delete the "id" medicine.
     *
     * @param id the id of the medicine to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/medicines/{id}")
    fun deleteMedicine(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Medicine : {}", id)

        medicineRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
