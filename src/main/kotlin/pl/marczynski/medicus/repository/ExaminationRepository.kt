package pl.marczynski.medicus.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import pl.marczynski.medicus.domain.Examination
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Examination] entity.
 */
@Suppress("unused")
@Repository
interface ExaminationRepository : JpaRepository<Examination, Long> {

    @Query("select examination from Examination examination where examination.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<Examination>

    @Query("select case when count(res)> 0 then true else false end from Examination res where res.id = :id and res.user.login = ?#{principal.username}")
    fun checkUserRightsById(@Param("id") id: Long): Boolean

    @Query(
        value = "select distinct res from Examination res where res.user.login = ?#{principal.username}",
        countQuery = "select count(distinct res) from Examination res where res.user.login = ?#{principal.username}"
    )
    override fun findAll(pageable: Pageable): Page<Examination>

    @Query("select distinct res from Examination res where res.user.login = ?#{principal.username}")
    override fun findAll(): MutableList<Examination>
}
