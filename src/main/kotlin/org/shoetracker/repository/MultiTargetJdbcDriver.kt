package org.shoetracker.repository

import io.quarkus.runtime.annotations.RegisterForReflection
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager
import java.sql.DriverPropertyInfo
import java.util.Properties
import java.util.logging.Logger

@RegisterForReflection(
    targets = [
        MultiTargetJdbcDriver::class,
        org.h2.Driver::class,
        org.mariadb.jdbc.Driver::class,
    ],
)
class MultiTargetJdbcDriver : Driver {
    private val h2Driver = org.h2.Driver()
    private val mariaDbDriver = org.mariadb.jdbc.Driver()

    override fun connect(
        url: String?,
        info: Properties?,
    ): Connection? {
        val trimmed = url?.trim() ?: return null
        return when {
            trimmed.startsWith("jdbc:h2:", ignoreCase = true) ->
                h2Driver.connect(normalizePrefix(trimmed, "jdbc:h2:"), info)
            trimmed.startsWith("jdbc:mariadb:", ignoreCase = true) ->
                mariaDbDriver.connect(normalizePrefix(trimmed, "jdbc:mariadb:"), info)
            trimmed.startsWith("jdbc:mysql:", ignoreCase = true) -> {
                val normalizedMysql = normalizePrefix(trimmed, "jdbc:mysql:")
                val targetUrl =
                    if (mariaDbDriver.acceptsURL(normalizedMysql)) {
                        normalizedMysql
                    } else {
                        "jdbc:mariadb:" + normalizedMysql.substring("jdbc:mysql:".length)
                    }
                mariaDbDriver.connect(targetUrl, info)
            }
            else -> null
        }
    }

    override fun acceptsURL(url: String?): Boolean {
        val trimmed = url?.trim() ?: return false
        return trimmed.startsWith("jdbc:h2:", ignoreCase = true) ||
            trimmed.startsWith("jdbc:mariadb:", ignoreCase = true) ||
            trimmed.startsWith("jdbc:mysql:", ignoreCase = true)
    }

    override fun getPropertyInfo(
        url: String?,
        info: Properties?,
    ): Array<DriverPropertyInfo> {
        val trimmed = url?.trim() ?: return emptyArray()
        return when {
            trimmed.startsWith("jdbc:h2:", ignoreCase = true) ->
                h2Driver.getPropertyInfo(normalizePrefix(trimmed, "jdbc:h2:"), info)
            trimmed.startsWith("jdbc:mariadb:", ignoreCase = true) ->
                mariaDbDriver.getPropertyInfo(normalizePrefix(trimmed, "jdbc:mariadb:"), info)
            trimmed.startsWith("jdbc:mysql:", ignoreCase = true) -> {
                val normalizedMysql = normalizePrefix(trimmed, "jdbc:mysql:")
                mariaDbDriver.getPropertyInfo(
                    if (!mariaDbDriver.acceptsURL(normalizedMysql)) {
                        "jdbc:mariadb:" + normalizedMysql.substring("jdbc:mysql:".length)
                    } else {
                        normalizedMysql
                    },
                    info,
                )
            }
            else -> emptyArray()
        }
    }

    private fun normalizePrefix(
        url: String,
        lowercasePrefix: String,
    ): String = if (url.startsWith(lowercasePrefix)) {
        url
    } else {
        lowercasePrefix + url.substring(lowercasePrefix.length)
    }

    override fun getMajorVersion(): Int = 1

    override fun getMinorVersion(): Int = 0

    override fun jdbcCompliant(): Boolean = true

    override fun getParentLogger(): Logger = Logger.getLogger(MultiTargetJdbcDriver::class.java.name)

    companion object {
        init {
            DriverManager.registerDriver(MultiTargetJdbcDriver())
        }
    }
}
