package pl.marczynski.medicus.repository

import pl.marczynski.medicus.domain.Examination
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Examination] entity.
 */
@Suppress("unused")
@Repository
interface ExaminationRepository : JpaRepository<Examination, Long> {

    @Query("select examination from Examination examination where examination.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<Examination>
}
