package pl.marczynski.medicus.repository

import pl.marczynski.medicus.domain.Doctor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Doctor] entity.
 */
@Suppress("unused")
@Repository
interface DoctorRepository : JpaRepository<Doctor, Long>
