package pl.marczynski.medicus.repository

import pl.marczynski.medicus.domain.Treatment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

import java.util.Optional

/**
 * Spring Data  repository for the [Treatment] entity.
 */
@Repository
interface TreatmentRepository : JpaRepository<Treatment, Long> {

    @Query("select treatment from Treatment treatment where treatment.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<Treatment>

    @Query(
        value = "select distinct treatment from Treatment treatment left join fetch treatment.visitedDoctors left join fetch treatment.medicines where treatment.user.login = ?#{principal.username}",
        countQuery = "select count(distinct treatment) from Treatment treatment where treatment.user.login = ?#{principal.username}"
    )
    fun findAllWithEagerRelationships(pageable: Pageable): Page<Treatment>

    @Query(value = "select distinct treatment from Treatment treatment left join fetch treatment.visitedDoctors left join fetch treatment.medicines where treatment.user.login = ?#{principal.username}")
    fun findAllWithEagerRelationships(): MutableList<Treatment>

    @Query(value = "select distinct treatment from Treatment treatment left join fetch treatment.visitedDoctors left join fetch treatment.medicines where treatment.user.login = ?#{principal.username} and treatment.appointments is null order by treatment.startDate")
    fun findAllWithoutAppointment(): MutableList<Treatment>

    @Query("select treatment from Treatment treatment left join fetch treatment.visitedDoctors left join fetch treatment.medicines where treatment.id =:id")
    fun findOneWithEagerRelationships(@Param("id") id: Long): Optional<Treatment>

    @Query("select case when count(res)> 0 then true else false end from Treatment res where res.id = :id and res.user.login = ?#{principal.username}")
    fun checkUserRightsById(@Param("id") id: Long): Boolean

    @Query(
        value = "select distinct res from Treatment res where res.user.login = ?#{principal.username}",
        countQuery = "select count(distinct res) from Treatment res where res.user.login = ?#{principal.username}"
    )
    override fun findAll(pageable: Pageable): Page<Treatment>

    @Query("select distinct res from Treatment res where res.user.login = ?#{principal.username} order by res.startDate")
    override fun findAll(): MutableList<Treatment>
}
