package org.shoetracker.domain

import com.fasterxml.uuid.Generators
import java.util.UUID

object UUIDv7 {
    // RFC 9562 UUIDv7 generator from com.fasterxml.uuid:java-uuid-generator
    private val generator by lazy { Generators.timeBasedEpochGenerator() }

    fun generate(): UUID = generator.generate()
}
