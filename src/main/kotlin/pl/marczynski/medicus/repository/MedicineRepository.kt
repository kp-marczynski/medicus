package pl.marczynski.medicus.repository

import pl.marczynski.medicus.domain.Medicine
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Medicine] entity.
 */
@Suppress("unused")
@Repository
interface MedicineRepository : JpaRepository<Medicine, Long> {

    @Query("select medicine from Medicine medicine where medicine.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<Medicine>

    @Query("select distinct res from Medicine res where res.user.login = ?#{principal.username}")
    override fun findAll(): MutableList<Medicine>

    @Query("select case when count(res)> 0 then true else false end from Medicine res where res.id = :id and res.user.login = ?#{principal.username}")
    fun checkUserRightsById(@Param("id") id: Long): Boolean
}
