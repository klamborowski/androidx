/*
 * Copyright 2022 The Android Open Source Project
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

package androidx.emoji2.emojipicker.utils

import androidx.test.filters.SdkSuppress
import androidx.test.filters.SmallTest
import org.junit.Assert.assertEquals
import org.junit.Test

@SmallTest
class UnicodeRenderableManagerTest {
    @Test
    @SdkSuppress(minSdkVersion = 21, maxSdkVersion = 22)
    fun testGetClosestRenderable_lowerVersionTrimmed() {
        // #️⃣
        assertEquals(
            UnicodeRenderableManager.getClosestRenderable("\u0023\uFE0F\u20E3"),
            "\u0023\u20E3"
        )
    }

    @Test
    @SdkSuppress(minSdkVersion = 23)
    fun testGetClosestRenderable_higherVersionNoTrim() {
        // #️⃣
        assertEquals(
            UnicodeRenderableManager.getClosestRenderable("\u0023\uFE0F\u20E3"),
            "\u0023\uFE0F\u20E3"
        )
    }
}