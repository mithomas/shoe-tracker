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
        if (url == null) return null
        return when {
            url.startsWith("jdbc:h2:") -> h2Driver.connect(url, info)
            url.startsWith("jdbc:mariadb:") -> mariaDbDriver.connect(url, info)
            url.startsWith("jdbc:mysql:") -> {
                val targetUrl =
                    if (mariaDbDriver.acceptsURL(url)) {
                        url
                    } else {
                        "jdbc:mariadb:" + url.removePrefix("jdbc:mysql:")
                    }
                mariaDbDriver.connect(targetUrl, info)
            }
            else -> null
        }
    }

    override fun acceptsURL(url: String?): Boolean = url != null &&
        (
            url.startsWith("jdbc:h2:") ||
                url.startsWith("jdbc:mariadb:") ||
                url.startsWith("jdbc:mysql:")
            )

    override fun getPropertyInfo(
        url: String?,
        info: Properties?,
    ): Array<DriverPropertyInfo> = when {
        url == null -> emptyArray()
        url.startsWith("jdbc:h2:") -> h2Driver.getPropertyInfo(url, info)
        url.startsWith("jdbc:mariadb:") || url.startsWith("jdbc:mysql:") ->
            mariaDbDriver.getPropertyInfo(
                if (url.startsWith("jdbc:mysql:") && !mariaDbDriver.acceptsURL(url)) {
                    "jdbc:mariadb:" + url.removePrefix("jdbc:mysql:")
                } else {
                    url
                },
                info,
            )
        else -> emptyArray()
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
