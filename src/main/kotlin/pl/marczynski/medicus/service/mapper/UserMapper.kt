package pl.marczynski.medicus.service.mapper

import pl.marczynski.medicus.domain.Authority
import pl.marczynski.medicus.domain.User
import pl.marczynski.medicus.service.dto.UserDTO

import org.springframework.stereotype.Service

/**
 * Mapper for the entity [User] and its DTO called [UserDTO].
 *
 * Normal mappers are generated using MapStruct, this one is hand-coded as MapStruct
 * support is still in beta, and requires a manual step with an IDE.
 */
@Service
class UserMapper {

    fun usersToUserDTOs(users: List<User?>): MutableList<UserDTO> =
        users.asSequence()
            .filterNotNull()
            .mapTo(mutableListOf()) { userToUserDTO(it) }

    fun userToUserDTO(user: User): UserDTO = UserDTO(user)

    fun userDTOsToUsers(userDTOs: List<UserDTO?>) =
        userDTOs.asSequence()
            .mapNotNullTo(mutableListOf()) { userDTOToUser(it) }

    fun userDTOToUser(userDTO: UserDTO?) =
        when (userDTO) {
            null -> null
            else -> {
                User(
                    id = userDTO.id,
                    login = userDTO.login,
                    firstName = userDTO.firstName,
                    lastName = userDTO.lastName,
                    email = userDTO.email,
                    imageUrl = userDTO.imageUrl,
                    activated = userDTO.activated,
                    langKey = userDTO.langKey,
                    authorities = authoritiesFromStrings(userDTO.authorities)
                )
            }
        }

    private fun authoritiesFromStrings(authoritiesAsString: Set<String>?): MutableSet<Authority> =
        authoritiesAsString?.mapTo(mutableSetOf()) { Authority(name = it) } ?: mutableSetOf()

    fun userFromId(id: Long?) = id?.let { User(id = it) }
}
