package pl.marczynski.medicus.repository

import pl.marczynski.medicus.domain.VisitedDoctor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [VisitedDoctor] entity.
 */
@Suppress("unused")
@Repository
interface VisitedDoctorRepository : JpaRepository<VisitedDoctor, Long> {

    @Query("select visitedDoctor from VisitedDoctor visitedDoctor where visitedDoctor.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<VisitedDoctor>
}
