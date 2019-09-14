package pl.marczynski.medicus.repository

import pl.marczynski.medicus.domain.ExaminationType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [ExaminationType] entity.
 */
@Suppress("unused")
@Repository
interface ExaminationTypeRepository : JpaRepository<ExaminationType, Long>
