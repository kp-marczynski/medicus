package pl.marczynski.medicus.repository

import pl.marczynski.medicus.domain.Symptom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Symptom] entity.
 */
@Suppress("unused")
@Repository
interface SymptomRepository : JpaRepository<Symptom, Long> {

    @Query("select symptom from Symptom symptom where symptom.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<Symptom>
}
