package pl.marczynski.medicus.repository

import pl.marczynski.medicus.domain.ExaminationPackage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [ExaminationPackage] entity.
 */
@Suppress("unused")
@Repository
interface ExaminationPackageRepository : JpaRepository<ExaminationPackage, Long> {

    @Query("select examinationPackage from ExaminationPackage examinationPackage where examinationPackage.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<ExaminationPackage>
}
