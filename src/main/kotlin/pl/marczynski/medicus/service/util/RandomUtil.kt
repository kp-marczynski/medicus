@file:JvmName("RandomUtil")

package pl.marczynski.medicus.service.util

import org.apache.commons.lang3.RandomStringUtils

private const val DEF_COUNT = 20

/**
 * Generate a password.
 *
 * @return the generated password.
 */
fun generatePassword(): String = RandomStringUtils.randomAlphanumeric(DEF_COUNT)

/**
 * Generate an activation key.
 *
 * @return the generated activation key.
 */
fun generateActivationKey(): String = RandomStringUtils.randomNumeric(DEF_COUNT)

/**
 * Generate a reset key.
 *
 * @return the generated reset key.
 */
fun generateResetKey(): String = RandomStringUtils.randomNumeric(DEF_COUNT)
