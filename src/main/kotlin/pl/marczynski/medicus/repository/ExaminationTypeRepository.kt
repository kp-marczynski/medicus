package pl.marczynski.medicus.repository

import pl.marczynski.medicus.domain.ExaminationType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [ExaminationType] entity.
 */
@Suppress("unused")
@Repository
interface ExaminationTypeRepository : JpaRepository<ExaminationType, Long> {

    @Query("select examinationType from ExaminationType examinationType where examinationType.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<ExaminationType>

    @Query("select distinct res from ExaminationType res where res.user.login = ?#{principal.username}")
    override fun findAll(): MutableList<ExaminationType>

    @Query("select case when count(res)> 0 then true else false end from ExaminationType res where res.id = :id and res.user.login = ?#{principal.username}")
    fun checkUserRightsById(@Param("id") id: Long): Boolean
}
