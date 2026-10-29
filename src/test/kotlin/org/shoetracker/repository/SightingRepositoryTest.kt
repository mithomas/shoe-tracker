package org.shoetracker.repository

import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.shoetracker.domain.GeoLocation
import org.shoetracker.domain.Sighting
import java.io.PrintWriter
import java.sql.Connection
import java.sql.SQLException
import java.sql.SQLFeatureNotSupportedException
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.logging.Logger
import javax.sql.DataSource

@QuarkusTest
class SightingRepositoryTest {
    @Inject
    lateinit var repository: SightingRepository

    @Inject
    lateinit var dataSource: DataSource

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun testEagerStatementsAndCrudOperations() {
        val now = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        val sighting =
            Sighting(
                id = "018f4a2b-1234-7000-8000-111122223333",
                userId = "EagerUser",
                location = GeoLocation(latitude = 50.1109, longitude = 8.6821),
                recordedAt = now,
                createdAt = now,
            )

        // Insert
        repository.insert(sighting)
        assertThat(repository.countSightings(null)).isEqualTo(1L)
        assertThat(repository.countSightings("EagerUser")).isEqualTo(1L)
        assertThat(repository.countSightings("OtherUser")).isEqualTo(0L)

        // Find
        val items = repository.findSightings("EagerUser", limit = 10, offset = 0)
        assertThat(items).hasSize(1)
        assertThat(items[0].id).isEqualTo(sighting.id)
        assertThat(items[0].userId).isEqualTo("EagerUser")
        assertThat(items[0].recordedAt).isEqualTo(now)
        assertThat(items[0].createdAt).isEqualTo(now)

        // DeleteById
        val deleted = repository.deleteById(sighting.id)
        assertThat(deleted).isTrue()
        assertThat(repository.countSightings(null)).isEqualTo(0L)

        // Delete non-existent
        val deletedNonExistent = repository.deleteById("non-existent")
        assertThat(deletedNonExistent).isFalse()
    }

    @Test
    fun testConcurrentMultiThreadedCrudOperations() {
        val threadCount = 8
        val recordsPerThread = 10
        val executor = Executors.newFixedThreadPool(threadCount)
        try {
            val baseTime = Instant.parse("2026-10-29T12:00:00.123Z").truncatedTo(ChronoUnit.MILLIS)
            val tasks =
                (0 until threadCount).map { threadIdx ->
                    Callable {
                        for (i in 0 until recordsPerThread) {
                            val id = "018f4a2b-%04d-7000-8000-%012d".format(threadIdx, i)
                            val timestamp = baseTime.plusMillis((threadIdx * 100 + i).toLong())
                            val sighting =
                                Sighting(
                                    id = id,
                                    userId = "User-$threadIdx",
                                    location =
                                    GeoLocation(
                                        latitude = 50.0 + threadIdx * 0.01,
                                        longitude = 8.0 + i * 0.01,
                                    ),
                                    recordedAt = timestamp,
                                    createdAt = timestamp,
                                )
                            repository.insert(sighting)
                            val tempId = "018f4a2b-%04d-7000-9000-%012d".format(threadIdx, i)
                            repository.insert(sighting.copy(id = tempId))
                            val userSightings = repository.findSightings("User-$threadIdx", limit = 50, offset = 0)
                            assertThat(userSightings).isNotEmpty
                            val userCount = repository.countSightings("User-$threadIdx")
                            assertThat(userCount).isGreaterThanOrEqualTo(1L)
                            assertThat(repository.deleteById(tempId)).isTrue()
                        }
                    }
                }

            val futures = executor.invokeAll(tasks)
            futures.forEach { it.get() }

            val totalExpected = (threadCount * recordsPerThread).toLong()
            assertThat(repository.countSightings(null)).isEqualTo(totalExpected)
            val allSightings = repository.findSightings(null, limit = 500, offset = 0)
            assertThat(allSightings).hasSize(totalExpected.toInt())
        } finally {
            executor.shutdownNow()
        }
    }

    @Test
    fun testFailFastStartupValidationOnCorruptOrUnreachableDataSource() {
        val failingDataSource =
            object : DataSource {
                override fun getConnection(): Connection = throw SQLException("Simulated database unreachable")

                override fun getConnection(
                    username: String?,
                    password: String?,
                ): Connection = throw SQLException("Simulated database unreachable")

                override fun getLogWriter(): PrintWriter? = null

                override fun setLogWriter(out: PrintWriter?) {}

                override fun setLoginTimeout(seconds: Int) {}

                override fun getLoginTimeout(): Int = 0

                override fun getParentLogger(): Logger = throw SQLFeatureNotSupportedException()

                override fun <T : Any?> unwrap(iface: Class<T>?): T = throw SQLException()

                override fun isWrapperFor(iface: Class<*>?): Boolean = false
            }

        val badRepo = SightingRepository(failingDataSource)

        assertThatThrownBy { badRepo.init() }
            .isInstanceOf(SQLException::class.java)
            .hasMessageContaining("Simulated database unreachable")
    }
}
