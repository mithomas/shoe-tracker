package org.shoetracker.repository

import io.agroal.api.configuration.supplier.AgroalDataSourceConfigurationSupplier
import io.agroal.api.exceptionsorter.MySQLExceptionSorter
import io.quarkus.agroal.runtime.AgroalConnectionConfigurer
import io.quarkus.agroal.runtime.JdbcDriver
import io.quarkus.arc.Unremovable
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.enterprise.context.ApplicationScoped

/**
 * Configures Agroal connection pooling for `quarkus.datasource.db-kind=other` (`MultiTargetJdbcDriver`).
 *
 * Installs [MySQLExceptionSorter] so Agroal detects fatal SQLState `08*` connection exceptions
 * across both H2 and MariaDB/MySQL as well as MariaDB/MySQL fatal server error codes,
 * while avoiding the fallback `UnknownDbAgroalConnectionConfigurer` startup warning.
 */
@ApplicationScoped
@Unremovable
@JdbcDriver("other")
@RegisterForReflection
class MultiTargetAgroalConnectionConfigurer : AgroalConnectionConfigurer {
    override fun disableSslSupport(
        databaseKind: String?,
        dataSourceConfiguration: AgroalDataSourceConfigurationSupplier?,
        additionalProperties: MutableMap<String, String>?,
    ) {
        // Handled per-driver via JDBC URL parameters when needed
    }

    override fun setExceptionSorter(
        databaseKind: String?,
        dataSourceConfiguration: AgroalDataSourceConfigurationSupplier?,
    ) {
        dataSourceConfiguration
            ?.connectionPoolConfiguration()
            ?.exceptionSorter(MySQLExceptionSorter())
    }
}
