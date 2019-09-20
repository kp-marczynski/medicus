package pl.marczynski.medicus.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import pl.marczynski.medicus.domain.ExaminationPackage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Spring Data  repository for the [ExaminationPackage] entity.
 */
@Suppress("unused")
@Repository
interface ExaminationPackageRepository : JpaRepository<ExaminationPackage, Long> {

    @Query("select examinationPackage from ExaminationPackage examinationPackage where examinationPackage.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<ExaminationPackage>

    @Query("select case when count(res)> 0 then true else false end from ExaminationPackage res where res.id = :id and res.user.login = ?#{principal.username}")
    fun checkUserRightsById(@Param("id") id: Long): Boolean

    @Query(
        value = "select distinct res from ExaminationPackage res where res.user.login = ?#{principal.username}",
        countQuery = "select count(distinct res) from ExaminationPackage res where res.user.login = ?#{principal.username}"
    )
    override fun findAll(pageable: Pageable): Page<ExaminationPackage>

    @Query("select distinct res from ExaminationPackage res where res.user.login = ?#{principal.username} order by res.date")
    override fun findAll(): MutableList<ExaminationPackage>

    @Query(
        value = "select distinct examinationPackage from ExaminationPackage examinationPackage left join fetch examinationPackage.examinations left join fetch examinationPackage.visitedDoctors where examinationPackage.user.login = ?#{principal.username}",
        countQuery = "select count(distinct examinationPackage) from ExaminationPackage examinationPackage where examinationPackage.user.login = ?#{principal.username}"
    )
    fun findAllWithEagerRelationships(pageable: Pageable): Page<ExaminationPackage>

    @Query(value = "select distinct examinationPackage from ExaminationPackage examinationPackage left join fetch examinationPackage.examinations left join fetch examinationPackage.visitedDoctors where examinationPackage.user.login = ?#{principal.username}  order by examinationPackage.date")
    fun findAllWithEagerRelationships(): MutableList<ExaminationPackage>

    @Query(value = "select distinct examinationPackage from ExaminationPackage examinationPackage left join fetch examinationPackage.examinations left join fetch examinationPackage.visitedDoctors where examinationPackage.user.login = ?#{principal.username} and examinationPackage.appointment is null order by examinationPackage.date")
    fun findAllWithoutAppointment(): MutableList<ExaminationPackage>

    @Query("select examinationPackage from ExaminationPackage examinationPackage left join fetch examinationPackage.examinations left join fetch examinationPackage.visitedDoctors left join fetch examinationPackage.descriptionScan where examinationPackage.id =:id")
    fun findOneWithEagerRelationships(@Param("id") id: Long): Optional<ExaminationPackage>
}
