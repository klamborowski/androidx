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

package androidx.room.writer

import COMMON
import androidx.room.DatabaseProcessingStep
import androidx.room.compiler.processing.util.Source
import androidx.room.compiler.processing.util.XTestInvocation
import androidx.room.compiler.processing.util.runKspTest
import androidx.room.processor.Context
import loadTestSource
import org.junit.Test

// Dany's Kotlin codegen test playground (and tests too)
class KotlinCodeGenTest {

    val databaseSrc = Source.kotlin(
        "MyDatabase.kt",
        """
        import androidx.room.*

        @Database(entities = [MyEntity::class], version = 1, exportSchema = false)
        abstract class MyDatabase : RoomDatabase() {
          abstract fun getDao(): MyDao
        }
        """.trimIndent()
    )

    @Test
    fun pojoRowAdapter_variableProperty() {
        val testName = object {}.javaClass.enclosingMethod!!.name
        val src = Source.kotlin(
            "MyDao.kt",
            """
            import androidx.room.*

            @Dao
            interface MyDao {
              @Query("SELECT * FROM MyEntity")
              fun getEntity(): MyEntity
              
              @Insert
              fun addEntity(item: MyEntity)
            }

            @Entity
            class MyEntity(
                @PrimaryKey
                var pk: Int
            ) {
                var variable: Long = 0
            }
            """.trimIndent()
        )
        runTest(
            sources = listOf(src, databaseSrc),
            expectedFilePath = getTestGoldenPath(testName)
        )
    }

    @Test
    fun pojoRowAdapter_variableProperty_java() {
        val testName = object {}.javaClass.enclosingMethod!!.name
        val src = Source.kotlin(
            "MyDao.kt",
            """
            import androidx.room.*

            @Dao
            interface MyDao {
              @Query("SELECT * FROM MyEntity")
              fun getEntity(): MyEntity
            }
            """.trimIndent()
        )
        val javaEntity = Source.java(
            "MyEntity",
            """
            import androidx.room.*;
            
            @Entity
            public class MyEntity {
              @PrimaryKey
              private long mValue;
              
              public long getValue() { return mValue; }
              public void setValue(long value) { mValue = value; }
            }
            """.trimIndent()
        )
        runTest(
            sources = listOf(src, javaEntity, databaseSrc),
            expectedFilePath = getTestGoldenPath(testName)
        )
    }

    @Test
    fun pojoRowAdapter_internalVisibility() {
        val testName = object {}.javaClass.enclosingMethod!!.name
        val src = Source.kotlin(
            "MyDao.kt",
            """
            import androidx.room.*

            @Dao
            interface MyDao {
              @Query("SELECT * FROM MyEntity")
              fun getEntity(): MyEntity
              
              @Insert
              fun addEntity(item: MyEntity)
            }

            @Entity
            class MyEntity(
                @PrimaryKey
                val pk: Int,
                internal val internalVal: Long
            ) {
                internal var internalVar: Long = 0
                var internalSetterVar: Long = 0
                    internal set
            }
            """.trimIndent()
        )
        runTest(
            sources = listOf(src, databaseSrc),
            expectedFilePath = getTestGoldenPath(testName)
        )
    }

    @Test
    fun pojoRowAdapter_primitives() {
        val testName = object {}.javaClass.enclosingMethod!!.name
        val src = Source.kotlin(
            "MyDao.kt",
            """
            import androidx.room.*

            @Dao
            interface MyDao {
              @Query("SELECT * FROM MyEntity")
              fun getEntity(): MyEntity
              
              @Insert
              fun addEntity(item: MyEntity)
            }

            @Entity
            data class MyEntity(
                @PrimaryKey
                val int: Int,
                val short: Short,
                val byte: Byte,
                val long: Long,
                val char: Char,
                val float: Float,
                val double: Double,
            )
            """.trimIndent()
        )
        runTest(
            sources = listOf(src, databaseSrc),
            expectedFilePath = getTestGoldenPath(testName)
        )
    }

    @Test
    fun pojoRowAdapter_primitives_nullable() {
        val testName = object {}.javaClass.enclosingMethod!!.name
        val src = Source.kotlin(
            "MyDao.kt",
            """
            import androidx.room.*

            @Dao
            interface MyDao {
              @Query("SELECT * FROM MyEntity")
              fun getEntity(): MyEntity
              
              @Insert
              fun addEntity(item: MyEntity)
            }

            @Entity
            data class MyEntity(
                @PrimaryKey
                val int: Int?,
                val short: Short?,
                val byte: Byte?,
                val long: Long?,
                val char: Char?,
                val float: Float?,
                val double: Double?,
            )
            """.trimIndent()
        )
        runTest(
            sources = listOf(src, databaseSrc),
            expectedFilePath = getTestGoldenPath(testName)
        )
    }

    @Test
    fun pojoRowAdapter_boolean() {
        val testName = object {}.javaClass.enclosingMethod!!.name
        val src = Source.kotlin(
            "MyDao.kt",
            """
            import androidx.room.*

            @Dao
            interface MyDao {
              @Query("SELECT * FROM MyEntity")
              fun getEntity(): MyEntity
              
              @Insert
              fun addEntity(item: MyEntity)
            }

            @Entity
            data class MyEntity(
                @PrimaryKey
                val pk: Int,
                val boolean: Boolean,
                val nullableBoolean: Boolean?,
            )
            """.trimIndent()
        )
        runTest(
            sources = listOf(src, databaseSrc),
            expectedFilePath = getTestGoldenPath(testName)
        )
    }

    @Test
    fun pojoRowAdapter_string() {
        val testName = object {}.javaClass.enclosingMethod!!.name
        val src = Source.kotlin(
            "MyDao.kt",
            """
            import androidx.room.*

            @Dao
            interface MyDao {
              @Query("SELECT * FROM MyEntity")
              fun getEntity(): MyEntity
              
              @Insert
              fun addEntity(item: MyEntity)
            }

            @Entity
            data class MyEntity(
                @PrimaryKey
                val string: String,
                val nullableString: String?,
            )
            """.trimIndent()
        )
        runTest(
            sources = listOf(src, databaseSrc),
            expectedFilePath = getTestGoldenPath(testName)
        )
    }

    @Test
    fun pojoRowAdapter_byteArray() {
        val testName = object {}.javaClass.enclosingMethod!!.name
        val src = Source.kotlin(
            "MyDao.kt",
            """
            import androidx.room.*

            @Dao
            interface MyDao {
              @Query("SELECT * FROM MyEntity")
              fun getEntity(): MyEntity
              
              @Insert
              fun addEntity(item: MyEntity)
            }

            @Entity
            data class MyEntity(
                @PrimaryKey
                val pk: Int,
                val byteArray: ByteArray,
                val nullableByteArray: ByteArray?,
            )
            """.trimIndent()
        )
        runTest(
            sources = listOf(src, databaseSrc),
            expectedFilePath = getTestGoldenPath(testName)
        )
    }

    @Test
    fun pojoRowAdapter_enum() {
        val testName = object {}.javaClass.enclosingMethod!!.name
        val src = Source.kotlin(
            "MyDao.kt",
            """
            import androidx.room.*

            @Dao
            interface MyDao {
              @Query("SELECT * FROM MyEntity")
              fun getEntity(): MyEntity
              
              @Insert
              fun addEntity(item: MyEntity)
            }

            @Entity
            data class MyEntity(
                @PrimaryKey
                val pk: Int,
                val enum: Fruit,
                val nullableEnum: Fruit?,
            )

            enum class Fruit {
                APPLE,
                BANANA
            }
            """.trimIndent()
        )
        runTest(
            sources = listOf(src, databaseSrc),
            expectedFilePath = getTestGoldenPath(testName)
        )
    }

    @Test
    fun pojoRowAdapter_uuid() {
        val testName = object {}.javaClass.enclosingMethod!!.name
        val src = Source.kotlin(
            "MyDao.kt",
            """
            import androidx.room.*
            import java.util.UUID

            @Dao
            interface MyDao {
              @Query("SELECT * FROM MyEntity")
              fun getEntity(): MyEntity
              
              @Insert
              fun addEntity(item: MyEntity)
            }

            @Entity
            data class MyEntity(
                @PrimaryKey
                val pk: Int,
                val uuid: UUID,
                val nullableUuid: UUID?,
            )

            enum class Fruit {
                APPLE,
                BANANA
            }
            """.trimIndent()
        )
        runTest(
            sources = listOf(src, databaseSrc),
            expectedFilePath = getTestGoldenPath(testName)
        )
    }

    @Test
    fun pojoRowAdapter_embedded() {
        val testName = object {}.javaClass.enclosingMethod!!.name
        val src = Source.kotlin(
            "MyDao.kt",
            """
            import androidx.room.*
            import java.util.UUID

            @Dao
            interface MyDao {
              @Query("SELECT * FROM MyEntity")
              fun getEntity(): MyEntity
              
              @Insert
              fun addEntity(item: MyEntity)
            }

            @Entity
            data class MyEntity(
                @PrimaryKey
                val pk: Int,
                @Embedded
                val foo: Foo,
                @Embedded(prefix = "nullable")
                val nullableFoo: Foo?,
            )

            data class Foo(
                val numberData: Long,
                val stringData: String
            )
            """.trimIndent()
        )
        runTest(
            sources = listOf(src, databaseSrc),
            expectedFilePath = getTestGoldenPath(testName)
        )
    }

    @Test
    fun pojoRowAdapter_customTypeConverter() {
        val testName = object {}.javaClass.enclosingMethod!!.name
        val src = Source.kotlin(
            "MyDao.kt",
            """
            import androidx.room.*
            import java.util.UUID

            @Dao
            interface MyDao {
              @Query("SELECT * FROM MyEntity")
              fun getEntity(): MyEntity
              
              @Insert
              fun addEntity(item: MyEntity)
            }

            @Entity
            @TypeConverters(FooConverter::class)
            data class MyEntity(
                @PrimaryKey
                val pk: Int,
                val foo: Foo,
            )

            data class Foo(val data: String)

            class FooConverter {
                @TypeConverter
                fun fromString(data: String): Foo = Foo(data)
                @TypeConverter
                fun toString(foo: Foo): String = foo.data
            }
            """.trimIndent()
        )
        runTest(
            sources = listOf(src, databaseSrc),
            expectedFilePath = getTestGoldenPath(testName)
        )
    }

    @Test
    fun coroutineResultBinder() {
        val testName = object {}.javaClass.enclosingMethod!!.name
        val src = Source.kotlin(
            "MyDao.kt",
            """
            import androidx.room.*
            import java.util.UUID

            @Dao
            interface MyDao {
              @Query("SELECT * FROM MyEntity")
              suspend fun getEntity(): MyEntity
            }

            @Entity
            data class MyEntity(
                @PrimaryKey
                val pk: Int,
            )
            """.trimIndent()
        )
        runTest(
            sources = listOf(src, databaseSrc, COMMON.COROUTINES_ROOM),
            expectedFilePath = getTestGoldenPath(testName)
        )
    }

    @Test
    fun basicParameterAdapter_string() {
        val testName = object {}.javaClass.enclosingMethod!!.name
        val src = Source.kotlin(
            "MyDao.kt",
            """
            import androidx.room.*

            @Dao
            interface MyDao {
              @Query("SELECT * FROM MyEntity WHERE string = :arg")
              fun stringParam(arg: String): MyEntity

              @Query("SELECT * FROM MyEntity WHERE string = :arg")
              fun nullableStringParam(arg: String?): MyEntity
            }

            @Entity
            data class MyEntity(
                @PrimaryKey
                val string: String,
            )
            """.trimIndent()
        )
        runTest(
            sources = listOf(src, databaseSrc),
            expectedFilePath = getTestGoldenPath(testName)
        )
    }

    @Test
    fun collectionParameterAdapter_string() {
        val testName = object {}.javaClass.enclosingMethod!!.name
        val src = Source.kotlin(
            "MyDao.kt",
            """
            import androidx.room.*

            @Dao
            interface MyDao {
              @Query("SELECT * FROM MyEntity WHERE string IN (:arg)")
              fun listOfString(arg: List<String>): MyEntity

              @Query("SELECT * FROM MyEntity WHERE string IN (:arg)")
              fun nullableListOfString(arg: List<String>?): MyEntity

              @Query("SELECT * FROM MyEntity WHERE string IN (:arg)")
              fun listOfNullableString(arg: List<String?>): MyEntity

              @Query("SELECT * FROM MyEntity WHERE string IN (:arg)")
              fun setOfString(arg: Set<String>): MyEntity
            }

            @Entity
            data class MyEntity(
                @PrimaryKey
                val string: String,
            )
            """.trimIndent()
        )
        runTest(
            sources = listOf(src, databaseSrc),
            expectedFilePath = getTestGoldenPath(testName)
        )
    }

    @Test
    fun arrayParameterAdapter() {
        val testName = object {}.javaClass.enclosingMethod!!.name
        val src = Source.kotlin(
            "MyDao.kt",
            """
            import androidx.room.*

            @Dao
            interface MyDao {
              @Query("SELECT * FROM MyEntity WHERE id IN (:arg)")
              fun arrayOfString(arg: Array<String>): MyEntity

              @Query("SELECT * FROM MyEntity WHERE id IN (:arg)")
              fun nullableArrayOfString(arg: Array<String>?): MyEntity

              @Query("SELECT * FROM MyEntity WHERE id IN (:arg)")
              fun arrayOfNullableString(arg: Array<String?>): MyEntity

              @Query("SELECT * FROM MyEntity WHERE id IN (:arg)")
              fun varargOfString(vararg arg: String): MyEntity

              @Query("SELECT * FROM MyEntity WHERE id IN (:arg)")
              fun varargOfNullableString(vararg arg: String?): MyEntity

              @Query("SELECT * FROM MyEntity WHERE id IN (:arg)")
              fun primitiveIntArray(arg: IntArray): MyEntity

              @Query("SELECT * FROM MyEntity WHERE id IN (:arg)")
              fun nullablePrimitiveIntArray(arg: IntArray?): MyEntity
            }

            @Entity
            data class MyEntity(
                @PrimaryKey
                val id: String,
            )
            """.trimIndent()
        )
        runTest(
            sources = listOf(src, databaseSrc),
            expectedFilePath = getTestGoldenPath(testName)
        )
    }

    @Test
    fun preparedQueryAdapter() {
        val testName = object {}.javaClass.enclosingMethod!!.name
        val src = Source.kotlin(
            "MyDao.kt",
            """
            import androidx.room.*

            @Dao
            interface MyDao {
              @Query("INSERT INTO MyEntity (id) VALUES (:id)")
              fun insertEntity(id: Long)

              @Query("INSERT INTO MyEntity (id) VALUES (:id)")
              fun insertEntityReturnLong(id: Long): Long

              @Query("UPDATE MyEntity SET text = :text")
              fun updateEntity(text: String)

              @Query("UPDATE MyEntity SET text = :text WHERE id = :id")
              fun updateEntityReturnInt(id: Long, text: String): Int

              @Query("DELETE FROM MyEntity")
              fun deleteEntity()

              @Query("DELETE FROM MyEntity")
              fun deleteEntityReturnInt(): Int
            }

            @Entity
            data class MyEntity(
                @PrimaryKey
                val id: Long,
                val text: String
            )
            """.trimIndent()
        )
        runTest(
            sources = listOf(src, databaseSrc),
            expectedFilePath = getTestGoldenPath(testName)
        )
    }

    @Test
    fun rawQuery() {
        val testName = object {}.javaClass.enclosingMethod!!.name
        val src = Source.kotlin(
            "MyDao.kt",
            """
            import androidx.room.*
            import androidx.sqlite.db.SupportSQLiteQuery

            @Dao
            interface MyDao {
              @RawQuery(observedEntities = [MyEntity::class])
              fun getEntity(sql: SupportSQLiteQuery): MyEntity
            }

            @Entity
            data class MyEntity(
                @PrimaryKey
                val pk: Long,
            )
            """.trimIndent()
        )
        runTest(
            sources = listOf(src, databaseSrc),
            expectedFilePath = getTestGoldenPath(testName)
        )
    }

    private fun getTestGoldenPath(testName: String): String {
        return "kotlinCodeGen/$testName.kt"
    }

    private fun runTest(
        sources: List<Source>,
        expectedFilePath: String,
        handler: (XTestInvocation) -> Unit = { }
    ) {
        runKspTest(
            sources = sources,
            options = mapOf(Context.BooleanProcessorOptions.GENERATE_KOTLIN.argName to "true"),
        ) {
            val databaseFqn = "androidx.room.Database"
            DatabaseProcessingStep().process(
                it.processingEnv,
                mapOf(databaseFqn to it.roundEnv.getElementsAnnotatedWith(databaseFqn)),
                it.roundEnv.isProcessingOver
            )
            it.assertCompilationResult {
                this.generatedSource(
                    loadTestSource(
                        expectedFilePath,
                        "MyDao_Impl"
                    )
                )
                this.hasNoWarnings()
            }
            handler.invoke(it)
        }
    }
}