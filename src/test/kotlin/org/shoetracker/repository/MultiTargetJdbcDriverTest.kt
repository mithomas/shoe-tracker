package org.shoetracker.repository

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.sql.DriverManager
import java.sql.SQLException
import java.util.Properties

class MultiTargetJdbcDriverTest {
    private val driver = MultiTargetJdbcDriver()

    @Test
    fun testAcceptsH2AndMariaDbAndMysqlUrls() {
        assertThat(driver.acceptsURL("jdbc:h2:file:./build/test-data/driver-test;MODE=MySQL")).isTrue()
        assertThat(driver.acceptsURL("jdbc:mariadb://localhost:3306/shoetracker")).isTrue()
        assertThat(driver.acceptsURL("jdbc:mysql://localhost:3306/shoetracker")).isTrue()
        assertThat(driver.acceptsURL("  JDBC:MARIADB://localhost:3306/shoetracker  ")).isTrue()
        assertThat(driver.acceptsURL("  JDBC:MYSQL://localhost:3306/shoetracker  ")).isTrue()
        assertThat(driver.acceptsURL("  JDBC:H2:mem:test  ")).isTrue()
        assertThat(driver.acceptsURL("jdbc:postgresql://localhost:5432/db")).isFalse()
        assertThat(driver.acceptsURL(null)).isFalse()
    }

    @Test
    fun testConnectDelegatesToH2DriverAndReportsProductName() {
        val url = "  JDBC:H2:mem:multitarget_test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE  "
        driver.connect(url, Properties()).use { conn ->
            assertThat(conn).isNotNull
            assertThat(conn!!.metaData.databaseProductName).isEqualTo("H2")
        }
    }

    @Test
    fun testConnectDelegatesToMariaDbDriverForMariaDbAndMysqlUrls() {
        assertThatThrownBy {
            driver.connect("jdbc:mariadb://127.0.0.1:1/shoetracker?connectTimeout=100", Properties())
        }.isInstanceOf(SQLException::class.java)

        assertThatThrownBy {
            driver.connect("  JDBC:MYSQL://127.0.0.1:1/shoetracker?connectTimeout=100  ", Properties())
        }.isInstanceOf(SQLException::class.java)
    }

    @Test
    fun testMariaDbLocalSocketUsesUnixDomainSocketViaJnaInsteadOfNullAddress() {
        // When JNA is present on the classpath, ?localSocket= attempts AF_UNIX socket connection
        // and throws SQLException (missing socket file) rather than IllegalArgumentException("connect: The address can't be null")
        assertThatThrownBy {
            driver.connect(
                "jdbc:mariadb://localhost/shoetracker?localSocket=/tmp/nonexistent-shoetracker-mysqld.sock",
                Properties(),
            )
        }.isInstanceOf(SQLException::class.java)
            .hasMessageContaining("Socket fail to connect")
    }

    @Test
    fun testUnsupportedJdbcPrefixReturnsNullAndDriverManagerThrowsSqlException() {
        assertThat(driver.connect("jdbc:postgresql://localhost:5432/db", Properties())).isNull()
        assertThat(driver.connect(null, Properties())).isNull()
        assertThat(driver.getPropertyInfo("jdbc:postgresql://localhost:5432/db", Properties())).isEmpty()
        assertThat(driver.getPropertyInfo("jdbc:h2:mem:test", Properties())).isNotNull()
        assertThat(driver.getPropertyInfo("jdbc:mysql://localhost:3306/db", Properties())).isNotNull()

        assertThatThrownBy {
            DriverManager.getConnection("jdbc:postgresql://localhost:5432/db")
        }.isInstanceOf(SQLException::class.java)
    }
}
