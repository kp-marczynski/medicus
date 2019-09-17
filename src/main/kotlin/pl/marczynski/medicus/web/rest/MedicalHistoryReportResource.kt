package pl.marczynski.medicus.web.rest

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.marczynski.medicus.domain.MedicalHistoryReport
import pl.marczynski.medicus.repository.UserRepository
import pl.marczynski.medicus.service.MedicalHistoryReportService

/**
 * REST controller for managing Medical History Report.
 */
@RestController
@RequestMapping("/api")
class MedicalHistoryReportResource(
    private val userRepository: UserRepository,
    private val medicalHistoryReportService: MedicalHistoryReportService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * `GET  /report
     *
     * @return the [ResponseEntity] with status `200 (OK)` and with body the report, or with status `404 (Not Found)`.
     */
    @GetMapping("/report")
    fun getReport(): ResponseEntity<MedicalHistoryReport> {
        log.debug("REST request to get Medical History Report for user: {}", userRepository.findByUserIsCurrentUser().get().login)
        val report = medicalHistoryReportService.getReport()
        return ResponseEntity.ok(report)
    }
}
