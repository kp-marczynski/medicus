package pl.marczynski.medicus.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import pl.marczynski.medicus.domain.Symptom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Symptom] entity.
 */
@Suppress("unused")
@Repository
interface SymptomRepository : JpaRepository<Symptom, Long> {

    @Query("select symptom from Symptom symptom where symptom.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<Symptom>

    @Query("select case when count(res)> 0 then true else false end from Symptom res where res.id = :id and res.user.login = ?#{principal.username}")
    fun checkUserRightsById(@Param("id") id: Long): Boolean

    @Query(
        value = "select distinct res from Symptom res where res.user.login = ?#{principal.username}",
        countQuery = "select count(distinct res) from Symptom res where res.user.login = ?#{principal.username}"
    )
    override fun findAll(pageable: Pageable): Page<Symptom>

    @Query("select distinct res from Symptom res where res.user.login = ?#{principal.username} order by res.startDate")
    override fun findAll(): MutableList<Symptom>
}
