package pl.marczynski.medicus.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import pl.marczynski.medicus.domain.VisitedDoctor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [VisitedDoctor] entity.
 */
@Suppress("unused")
@Repository
interface VisitedDoctorRepository : JpaRepository<VisitedDoctor, Long> {

    @Query("select visitedDoctor from VisitedDoctor visitedDoctor where visitedDoctor.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<VisitedDoctor>

    @Query(
        value = "select distinct res from VisitedDoctor res where res.user.login = ?#{principal.username}",
        countQuery = "select count(distinct res) from VisitedDoctor res where res.user.login = ?#{principal.username}"
    )
    override fun findAll(pageable: Pageable): Page<VisitedDoctor>

    @Query("select distinct res from VisitedDoctor res where res.user.login = ?#{principal.username}")
    override fun findAll(): MutableList<VisitedDoctor>

    @Query("select case when count(res)> 0 then true else false end from VisitedDoctor res where res.id = :id and res.user.login = ?#{principal.username}")
    fun checkUserRightsById(@Param("id") id: Long): Boolean
}
