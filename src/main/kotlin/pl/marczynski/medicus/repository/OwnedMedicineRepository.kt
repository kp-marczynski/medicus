package pl.marczynski.medicus.repository

import pl.marczynski.medicus.domain.OwnedMedicine
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

import java.util.Optional

/**
 * Spring Data  repository for the [OwnedMedicine] entity.
 */
@Repository
interface OwnedMedicineRepository : JpaRepository<OwnedMedicine, Long> {

    @Query("select ownedMedicine from OwnedMedicine ownedMedicine where ownedMedicine.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<OwnedMedicine>

    @Query(
        value = "select distinct ownedMedicine from OwnedMedicine ownedMedicine left join fetch ownedMedicine.medicines",
        countQuery = "select count(distinct ownedMedicine) from OwnedMedicine ownedMedicine"
    )
    fun findAllWithEagerRelationships(pageable: Pageable): Page<OwnedMedicine>

    @Query(value = "select distinct ownedMedicine from OwnedMedicine ownedMedicine left join fetch ownedMedicine.medicines")
    fun findAllWithEagerRelationships(): MutableList<OwnedMedicine>

    @Query("select ownedMedicine from OwnedMedicine ownedMedicine left join fetch ownedMedicine.medicines where ownedMedicine.id =:id")
    fun findOneWithEagerRelationships(@Param("id") id: Long): Optional<OwnedMedicine>
}
