/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.health.connect.client.records

import androidx.annotation.RestrictTo
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.kilograms
import java.time.Instant
import java.time.ZoneOffset

/**
 * Captures the user's body water mass. Each record represents a single instantaneous measurement.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class BodyWaterMassRecord(
    override val time: Instant,
    override val zoneOffset: ZoneOffset?,
    /** Mass in [Mass] unit. Required field. Valid range: 0-1000 kilograms. */
    public val mass: Mass,
    override val metadata: Metadata = Metadata.EMPTY,
) : InstantaneousRecord {

    init {
        mass.requireNotLess(other = mass.zero(), name = "mass")
        mass.requireNotMore(other = MAX_BODY_WATER_MASS, name = "mass")
    }

    /*
     * Generated by the IDE: Code -> Generate -> "equals() and hashCode()".
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BodyWaterMassRecord) return false

        if (mass != other.mass) return false
        if (time != other.time) return false
        if (zoneOffset != other.zoneOffset) return false
        if (metadata != other.metadata) return false

        return true
    }

    /*
     * Generated by the IDE: Code -> Generate -> "equals() and hashCode()".
     */
    override fun hashCode(): Int {
        var result = mass.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + (zoneOffset?.hashCode() ?: 0)
        result = 31 * result + metadata.hashCode()
        return result
    }

    private companion object {
        private val MAX_BODY_WATER_MASS = 1000.kilograms
    }
}
