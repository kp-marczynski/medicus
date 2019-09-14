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
        value = "select distinct treatment from Treatment treatment left join fetch treatment.medicines",
        countQuery = "select count(distinct treatment) from Treatment treatment"
    )
    fun findAllWithEagerRelationships(pageable: Pageable): Page<Treatment>

    @Query(value = "select distinct treatment from Treatment treatment left join fetch treatment.medicines")
    fun findAllWithEagerRelationships(): MutableList<Treatment>

    @Query("select treatment from Treatment treatment left join fetch treatment.medicines where treatment.id =:id")
    fun findOneWithEagerRelationships(@Param("id") id: Long): Optional<Treatment>
}
