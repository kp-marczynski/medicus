package pl.marczynski.medicus.repository

import pl.marczynski.medicus.domain.User

import org.springframework.data.domain.Page

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

import java.util.Optional
import java.time.Instant

/**
 * Spring Data JPA repository for the [User] entity.
 */
@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findOneByActivationKey(activationKey: String): Optional<User>

    fun findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(dateTime: Instant): List<User>

    fun findOneByResetKey(resetKey: String): Optional<User>

    fun findOneByEmailIgnoreCase(email: String?): Optional<User>

    fun findOneByLogin(login: String): Optional<User>

    @Query("select user from User user where user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): Optional<User>

    @EntityGraph(attributePaths = ["authorities"])
    fun findOneWithAuthoritiesById(id: Long): Optional<User>

    @EntityGraph(attributePaths = ["authorities"])
    fun findOneWithAuthoritiesByLogin(login: String): Optional<User>

    @EntityGraph(attributePaths = ["authorities"])
    fun findOneWithAuthoritiesByEmailIgnoreCase(email: String): Optional<User>

    fun findAllByLoginNot(pageable: Pageable, login: String): Page<User>

    @Query("select user.langKey from User user where user.login = ?#{principal.username}")
    fun getCurrentUserLanguage(): Optional<String>
}
