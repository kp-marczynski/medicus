package pl.marczynski.medicus.repository

import pl.marczynski.medicus.domain.Medicine
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Medicine] entity.
 */
@Suppress("unused")
@Repository
interface MedicineRepository : JpaRepository<Medicine, Long>
