package pl.marczynski.medicus.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import pl.marczynski.medicus.domain.Procedure
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Procedure] entity.
 */
@Suppress("unused")
@Repository
interface ProcedureRepository : JpaRepository<Procedure, Long> {

    @Query("select procedure from Procedure procedure where procedure.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<Procedure>

    @Query("select case when count(res)> 0 then true else false end from Procedure res where res.id = :id and res.user.login = ?#{principal.username}")
    fun checkUserRightsById(@Param("id") id: Long): Boolean

    @Query(
        value = "select distinct res from Procedure res where res.user.login = ?#{principal.username}",
        countQuery = "select count(distinct res) from Procedure res where res.user.login = ?#{principal.username}"
    )
    override fun findAll(pageable: Pageable): Page<Procedure>
}
