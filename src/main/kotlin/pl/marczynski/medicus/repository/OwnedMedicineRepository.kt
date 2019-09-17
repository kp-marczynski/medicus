package pl.marczynski.medicus.repository

import pl.marczynski.medicus.domain.OwnedMedicine
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [OwnedMedicine] entity.
 */
@Repository
interface OwnedMedicineRepository : JpaRepository<OwnedMedicine, Long> {

    @Query("select ownedMedicine from OwnedMedicine ownedMedicine where ownedMedicine.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<OwnedMedicine>

    @Query("select case when count(res)> 0 then true else false end from OwnedMedicine res where res.id = :id and res.user.login = ?#{principal.username}")
    fun checkUserRightsById(@Param("id") id: Long): Boolean

    @Query(
        value = "select distinct res from OwnedMedicine res where res.user.login = ?#{principal.username}",
        countQuery = "select count(distinct res) from OwnedMedicine res where res.user.login = ?#{principal.username}"
    )
    override fun findAll(pageable: Pageable): Page<OwnedMedicine>

    @Query("select distinct res from OwnedMedicine res where res.user.login = ?#{principal.username}")
    override fun findAll(): MutableList<OwnedMedicine>
}
