package pl.marczynski.medicus.repository

import pl.marczynski.medicus.domain.Appointment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

import java.util.Optional

/**
 * Spring Data  repository for the [Appointment] entity.
 */
@Repository
interface AppointmentRepository : JpaRepository<Appointment, Long> {

    @Query("select appointment from Appointment appointment where appointment.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<Appointment>

    @Query("select case when count(res)> 0 then true else false end from Appointment res where res.id = :id and res.user.login = ?#{principal.username}")
    fun checkUserRightsById(@Param("id") id: Long): Boolean

    @Query(
        value = "select distinct appointment from Appointment appointment left join fetch appointment.treatments left join fetch appointment.symptoms where appointment.user.login = ?#{principal.username}",
        countQuery = "select count(distinct appointment) from Appointment appointment where appointment.user.login = ?#{principal.username}"
    )
    fun findAllWithEagerRelationships(pageable: Pageable): Page<Appointment>

    @Query(value = "select distinct appointment from Appointment appointment left join fetch appointment.treatments left join fetch appointment.symptoms where appointment.user.login = ?#{principal.username}")
    fun findAllWithEagerRelationships(): MutableList<Appointment>

    @Query("select appointment from Appointment appointment left join fetch appointment.treatments left join fetch appointment.symptoms where appointment.id =:id")
    fun findOneWithEagerRelationships(@Param("id") id: Long): Optional<Appointment>

    @Query(
        value = "select distinct res from Appointment res where res.user.login = ?#{principal.username}",
        countQuery = "select count(distinct res) from Appointment res where res.user.login = ?#{principal.username}"
    )
    override fun findAll(pageable: Pageable): Page<Appointment>
}
