package pl.marczynski.medicus.repository

import pl.marczynski.medicus.domain.Procedure
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Procedure] entity.
 */
@Suppress("unused")
@Repository
interface ProcedureRepository : JpaRepository<Procedure, Long> {

    @Query("select procedure from Procedure procedure where procedure.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<Procedure>
}
