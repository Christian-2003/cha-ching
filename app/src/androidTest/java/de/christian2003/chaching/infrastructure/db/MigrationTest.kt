package de.christian2003.chaching.infrastructure.db

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.christian2003.chaching.plugin.infrastructure.db.ChaChingDatabase
import de.christian2003.chaching.plugin.infrastructure.db.MIGRATION_1_2
import de.christian2003.chaching.plugin.infrastructure.db.MIGRATION_2_3
import okio.IOException
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.jvm.Throws
import kotlin.uuid.Uuid


@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val TEST_DB = "migration_test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        instrumentation = InstrumentationRegistry.getInstrumentation(),
        assetsFolder = ChaChingDatabase::class.java.canonicalName!!,
        openFactory = FrameworkSQLiteOpenHelperFactory()
    )


    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        val salaryTypeId: Uuid = Uuid.random()
        val holidayPayTypeId: Uuid = Uuid.random()
        val transfer1Id: Uuid = Uuid.random()
        val transfer2Id: Uuid = Uuid.random()
        val transfer3Id: Uuid = Uuid.random()

        //Create database on version 1:
        var db = helper.createDatabase(TEST_DB, 1).apply {
            //Types:
            val salaryTypeStmt = compileStatement("INSERT INTO `types` (`typeId`, `name`, `icon`, `isHoursWorkedEditable`, `created`, `edited`) VALUES(?, ?, ?, ?, ?, ?)")
            salaryTypeStmt.bindBlob(1, salaryTypeId.toByteArray())
            salaryTypeStmt.bindString(2, "Salary")
            salaryTypeStmt.bindLong(3, 1)
            salaryTypeStmt.bindLong(4, 1)
            salaryTypeStmt.bindLong(5, LocalDateTime.of(2025, 4, 1, 12, 34, 3).toEpochSecond(ZoneOffset.UTC))
            salaryTypeStmt.bindLong(6, LocalDateTime.of(2025, 4, 1, 12, 34, 3).toEpochSecond(ZoneOffset.UTC))
            salaryTypeStmt.executeInsert()

            val holidayPayTypeStmt = compileStatement("INSERT INTO `types` (`typeId`, `name`, `icon`, `isHoursWorkedEditable`, `created`, `edited`) VALUES(?, ?, ?, ?, ?, ?)")
            holidayPayTypeStmt.bindBlob(1, holidayPayTypeId.toByteArray())
            holidayPayTypeStmt.bindString(2, "Holiday Pay")
            holidayPayTypeStmt.bindLong(3, 2)
            holidayPayTypeStmt.bindLong(4, 0)
            holidayPayTypeStmt.bindLong(5, LocalDateTime.of(2025, 4, 1, 12, 35, 58).toEpochSecond(ZoneOffset.UTC))
            holidayPayTypeStmt.bindLong(6, LocalDateTime.of(2025, 4, 13, 15, 16, 23).toEpochSecond(ZoneOffset.UTC))
            holidayPayTypeStmt.executeInsert()

            //Transfers:
            val transfer1Stmt = compileStatement("INSERT INTO `transfers` (`transferId`, `type`, `value`, `hoursWorked`, `isSalary`, `valueDate`, `created`, `edited`) VALUES(?, ?, ?, ?, ?, ?, ?, ?)")
            transfer1Stmt.bindBlob(1, transfer1Id.toByteArray())
            transfer1Stmt.bindBlob(2, salaryTypeId.toByteArray())
            transfer1Stmt.bindLong(3, 123456)
            transfer1Stmt.bindLong(4, 140)
            transfer1Stmt.bindLong(5, 0)
            transfer1Stmt.bindLong(6, LocalDate.of(2025, 4, 29).toEpochDay())
            transfer1Stmt.bindLong(7, LocalDateTime.of(2025, 4, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC))
            transfer1Stmt.bindLong(8, LocalDateTime.of(2025, 4, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC))
            transfer1Stmt.executeInsert()

            val transfer2Stmt = compileStatement("INSERT INTO `transfers` (`transferId`, `type`, `value`, `hoursWorked`, `isSalary`, `valueDate`, `created`, `edited`) VALUES(?, ?, ?, ?, ?, ?, ?, ?)")
            transfer2Stmt.bindBlob(1, transfer2Id.toByteArray())
            transfer2Stmt.bindBlob(2, salaryTypeId.toByteArray())
            transfer2Stmt.bindLong(3, 12345)
            transfer2Stmt.bindLong(4, 160)
            transfer2Stmt.bindLong(5, 0)
            transfer2Stmt.bindLong(6, LocalDate.of(2025, 5, 30).toEpochDay())
            transfer2Stmt.bindLong(7, LocalDateTime.of(2025, 5, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC))
            transfer2Stmt.bindLong(8, LocalDateTime.of(2025, 5, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC))
            transfer2Stmt.executeInsert()

            val transfer3Stmt = compileStatement("INSERT INTO `transfers` (`transferId`, `type`, `value`, `hoursWorked`, `isSalary`, `valueDate`, `created`, `edited`) VALUES(?, ?, ?, ?, ?, ?, ?, ?)")
            transfer3Stmt.bindBlob(1, transfer3Id.toByteArray())
            transfer3Stmt.bindBlob(2, holidayPayTypeId.toByteArray())
            transfer3Stmt.bindLong(3, 1234)
            transfer3Stmt.bindLong(4, 0)
            transfer3Stmt.bindLong(5, 0)
            transfer3Stmt.bindLong(6, LocalDate.of(2025, 5, 30).toEpochDay())
            transfer3Stmt.bindLong(7, LocalDateTime.of(2025, 5, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC))
            transfer3Stmt.bindLong(8, LocalDateTime.of(2025, 5, 28, 15, 18, 23).toEpochSecond(ZoneOffset.UTC))
            transfer3Stmt.executeInsert()

            close()
        }

        //Migrate to version 2:
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)

        //Validate data after migration:
        val typesCursor = db.query("SELECT * FROM `types` ORDER BY `icon` ASC")
        val transfersCursor = db.query("SELECT * FROM `transfers` ORDER BY `value` DESC")

        //Salary type:
        Assert.assertTrue(typesCursor.moveToFirst())
        Assert.assertEquals(salaryTypeId, Uuid.fromByteArray(typesCursor.getBlob(typesCursor.getColumnIndex("typeId"))))
        Assert.assertEquals("Salary", typesCursor.getString(typesCursor.getColumnIndex("name")))
        Assert.assertEquals(1, typesCursor.getInt(typesCursor.getColumnIndex("icon")))
        Assert.assertEquals(1, typesCursor.getInt(typesCursor.getColumnIndex("isHoursWorkedEditable")))
        Assert.assertEquals(LocalDateTime.of(2025, 4, 1, 12, 34, 3).toEpochSecond(ZoneOffset.UTC), typesCursor.getLong(typesCursor.getColumnIndex("created")))
        Assert.assertEquals(LocalDateTime.of(2025, 4, 1, 12, 34, 3).toEpochSecond(ZoneOffset.UTC), typesCursor.getLong(typesCursor.getColumnIndex("edited")))
        Assert.assertEquals(1, typesCursor.getInt(typesCursor.getColumnIndex("isEnabledInQuickAccess")))

        //Holiday Pay type:
        Assert.assertTrue(typesCursor.moveToNext())
        Assert.assertEquals(holidayPayTypeId, Uuid.fromByteArray(typesCursor.getBlob(typesCursor.getColumnIndex("typeId"))))
        Assert.assertEquals("Holiday Pay", typesCursor.getString(typesCursor.getColumnIndex("name")))
        Assert.assertEquals(2, typesCursor.getInt(typesCursor.getColumnIndex("icon")))
        Assert.assertEquals(0, typesCursor.getInt(typesCursor.getColumnIndex("isHoursWorkedEditable")))
        Assert.assertEquals(LocalDateTime.of(2025, 4, 1, 12, 35, 58).toEpochSecond(ZoneOffset.UTC), typesCursor.getLong(typesCursor.getColumnIndex("created")))
        Assert.assertEquals(LocalDateTime.of(2025, 4, 13, 15, 16, 23).toEpochSecond(ZoneOffset.UTC), typesCursor.getLong(typesCursor.getColumnIndex("edited")))
        Assert.assertEquals(1, typesCursor.getInt(typesCursor.getColumnIndex("isEnabledInQuickAccess")))

        //Transfer 1:
        Assert.assertTrue(transfersCursor.moveToFirst())
        Assert.assertEquals(transfer1Id, Uuid.fromByteArray(transfersCursor.getBlob(transfersCursor.getColumnIndex("transferId"))))
        Assert.assertEquals(salaryTypeId, Uuid.fromByteArray(transfersCursor.getBlob(transfersCursor.getColumnIndex("type"))))
        Assert.assertEquals(123456, transfersCursor.getInt(transfersCursor.getColumnIndex("value")))
        Assert.assertEquals(140, transfersCursor.getInt(transfersCursor.getColumnIndex("hoursWorked")))
        Assert.assertEquals(0, transfersCursor.getInt(transfersCursor.getColumnIndex("isSalary")))
        Assert.assertEquals(LocalDate.of(2025, 4, 29).toEpochDay(), transfersCursor.getLong(transfersCursor.getColumnIndex("valueDate")))
        Assert.assertEquals(LocalDateTime.of(2025, 4, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC), transfersCursor.getLong(transfersCursor.getColumnIndex("created")))
        Assert.assertEquals(LocalDateTime.of(2025, 4, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC), transfersCursor.getLong(transfersCursor.getColumnIndex("edited")))

        //Transfer 2:
        Assert.assertTrue(transfersCursor.moveToNext())
        Assert.assertEquals(transfer2Id, Uuid.fromByteArray(transfersCursor.getBlob(transfersCursor.getColumnIndex("transferId"))))
        Assert.assertEquals(salaryTypeId, Uuid.fromByteArray(transfersCursor.getBlob(transfersCursor.getColumnIndex("type"))))
        Assert.assertEquals(12345, transfersCursor.getInt(transfersCursor.getColumnIndex("value")))
        Assert.assertEquals(160, transfersCursor.getInt(transfersCursor.getColumnIndex("hoursWorked")))
        Assert.assertEquals(0, transfersCursor.getInt(transfersCursor.getColumnIndex("isSalary")))
        Assert.assertEquals(LocalDate.of(2025, 5, 30).toEpochDay(), transfersCursor.getLong(transfersCursor.getColumnIndex("valueDate")))
        Assert.assertEquals(LocalDateTime.of(2025, 5, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC), transfersCursor.getLong(transfersCursor.getColumnIndex("created")))
        Assert.assertEquals(LocalDateTime.of(2025, 5, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC), transfersCursor.getLong(transfersCursor.getColumnIndex("edited")))

        //Transfer 3:
        Assert.assertTrue(transfersCursor.moveToNext())
        Assert.assertEquals(transfer3Id, Uuid.fromByteArray(transfersCursor.getBlob(transfersCursor.getColumnIndex("transferId"))))
        Assert.assertEquals(holidayPayTypeId, Uuid.fromByteArray(transfersCursor.getBlob(transfersCursor.getColumnIndex("type"))))
        Assert.assertEquals(1234, transfersCursor.getInt(transfersCursor.getColumnIndex("value")))
        Assert.assertEquals(0, transfersCursor.getInt(transfersCursor.getColumnIndex("hoursWorked")))
        Assert.assertEquals(0, transfersCursor.getInt(transfersCursor.getColumnIndex("isSalary")))
        Assert.assertEquals(LocalDate.of(2025, 5, 30).toEpochDay(), transfersCursor.getLong(transfersCursor.getColumnIndex("valueDate")))
        Assert.assertEquals(LocalDateTime.of(2025, 5, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC), transfersCursor.getLong(transfersCursor.getColumnIndex("created")))
        Assert.assertEquals(LocalDateTime.of(2025, 5, 28, 15, 18, 23).toEpochSecond(ZoneOffset.UTC), transfersCursor.getLong(transfersCursor.getColumnIndex("edited")))

        typesCursor.close()
        transfersCursor.close()
    }


    @Test
    @Throws(IOException::class)
    fun migrate2To3() {
        val salaryTypeId: Uuid = Uuid.random()
        val holidayPayTypeId: Uuid = Uuid.random()
        val transfer1Id: Uuid = Uuid.random()
        val transfer2Id: Uuid = Uuid.random()
        val transfer3Id: Uuid = Uuid.random()

        //Create database on version 2:
        var db = helper.createDatabase(TEST_DB, 2).apply {
            //Types:
            val salaryTypeStmt = compileStatement("INSERT INTO `types` (`typeId`, `name`, `icon`, `isHoursWorkedEditable`, `created`, `edited`, `isEnabledInQuickAccess`) VALUES(?, ?, ?, ?, ?, ?, ?)")
            salaryTypeStmt.bindBlob(1, salaryTypeId.toByteArray())
            salaryTypeStmt.bindString(2, "Salary")
            salaryTypeStmt.bindLong(3, 1)
            salaryTypeStmt.bindLong(4, 1)
            salaryTypeStmt.bindLong(5, LocalDateTime.of(2025, 4, 1, 12, 34, 3).toEpochSecond(ZoneOffset.UTC))
            salaryTypeStmt.bindLong(6, LocalDateTime.of(2025, 4, 1, 12, 34, 3).toEpochSecond(ZoneOffset.UTC))
            salaryTypeStmt.bindLong(7, 1)
            salaryTypeStmt.executeInsert()

            val holidayPayTypeStmt = compileStatement("INSERT INTO `types` (`typeId`, `name`, `icon`, `isHoursWorkedEditable`, `created`, `edited`, `isEnabledInQuickAccess`) VALUES(?, ?, ?, ?, ?, ?, ?)")
            holidayPayTypeStmt.bindBlob(1, holidayPayTypeId.toByteArray())
            holidayPayTypeStmt.bindString(2, "Holiday Pay")
            holidayPayTypeStmt.bindLong(3, 2)
            holidayPayTypeStmt.bindLong(4, 0)
            holidayPayTypeStmt.bindLong(5, LocalDateTime.of(2025, 4, 1, 12, 35, 58).toEpochSecond(ZoneOffset.UTC))
            holidayPayTypeStmt.bindLong(6, LocalDateTime.of(2025, 4, 13, 15, 16, 23).toEpochSecond(ZoneOffset.UTC))
            holidayPayTypeStmt.bindLong(7, 0)
            holidayPayTypeStmt.executeInsert()

            //Transfers:
            val transfer1Stmt = compileStatement("INSERT INTO `transfers` (`transferId`, `type`, `value`, `hoursWorked`, `isSalary`, `valueDate`, `created`, `edited`) VALUES(?, ?, ?, ?, ?, ?, ?, ?)")
            transfer1Stmt.bindBlob(1, transfer1Id.toByteArray())
            transfer1Stmt.bindBlob(2, salaryTypeId.toByteArray())
            transfer1Stmt.bindLong(3, 123456)
            transfer1Stmt.bindLong(4, 140)
            transfer1Stmt.bindLong(5, 0)
            transfer1Stmt.bindLong(6, LocalDate.of(2025, 4, 29).toEpochDay())
            transfer1Stmt.bindLong(7, LocalDateTime.of(2025, 4, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC))
            transfer1Stmt.bindLong(8, LocalDateTime.of(2025, 4, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC))
            transfer1Stmt.executeInsert()

            val transfer2Stmt = compileStatement("INSERT INTO `transfers` (`transferId`, `type`, `value`, `hoursWorked`, `isSalary`, `valueDate`, `created`, `edited`) VALUES(?, ?, ?, ?, ?, ?, ?, ?)")
            transfer2Stmt.bindBlob(1, transfer2Id.toByteArray())
            transfer2Stmt.bindBlob(2, salaryTypeId.toByteArray())
            transfer2Stmt.bindLong(3, 12345)
            transfer2Stmt.bindLong(4, 160)
            transfer2Stmt.bindLong(5, 0)
            transfer2Stmt.bindLong(6, LocalDate.of(2025, 5, 30).toEpochDay())
            transfer2Stmt.bindLong(7, LocalDateTime.of(2025, 5, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC))
            transfer2Stmt.bindLong(8, LocalDateTime.of(2025, 5, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC))
            transfer2Stmt.executeInsert()

            val transfer3Stmt = compileStatement("INSERT INTO `transfers` (`transferId`, `type`, `value`, `hoursWorked`, `isSalary`, `valueDate`, `created`, `edited`) VALUES(?, ?, ?, ?, ?, ?, ?, ?)")
            transfer3Stmt.bindBlob(1, transfer3Id.toByteArray())
            transfer3Stmt.bindBlob(2, holidayPayTypeId.toByteArray())
            transfer3Stmt.bindLong(3, 1234)
            transfer3Stmt.bindLong(4, 0)
            transfer3Stmt.bindLong(5, 0)
            transfer3Stmt.bindLong(6, LocalDate.of(2025, 5, 30).toEpochDay())
            transfer3Stmt.bindLong(7, LocalDateTime.of(2025, 5, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC))
            transfer3Stmt.bindLong(8, LocalDateTime.of(2025, 5, 28, 15, 18, 23).toEpochSecond(ZoneOffset.UTC))
            transfer3Stmt.executeInsert()

            close()
        }

        //Migrate to version 3:
        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, MIGRATION_2_3)

        //Validate data after migration:
        val typesCursor = db.query("SELECT * FROM `types` ORDER BY `icon` ASC")
        val transfersCursor = db.query("SELECT * FROM `transfers` ORDER BY `value` DESC")

        //Salary type:
        Assert.assertTrue(typesCursor.moveToFirst())
        Assert.assertEquals(salaryTypeId, Uuid.fromByteArray(typesCursor.getBlob(typesCursor.getColumnIndex("typeId"))))
        Assert.assertEquals("Salary", typesCursor.getString(typesCursor.getColumnIndex("name")))
        Assert.assertEquals(1, typesCursor.getInt(typesCursor.getColumnIndex("icon")))
        Assert.assertEquals(1, typesCursor.getInt(typesCursor.getColumnIndex("isHoursWorkedEditable")))
        Assert.assertEquals(LocalDateTime.of(2025, 4, 1, 12, 34, 3).toEpochSecond(ZoneOffset.UTC), typesCursor.getLong(typesCursor.getColumnIndex("created")))
        Assert.assertEquals(LocalDateTime.of(2025, 4, 1, 12, 34, 3).toEpochSecond(ZoneOffset.UTC), typesCursor.getLong(typesCursor.getColumnIndex("edited")))
        Assert.assertEquals(1, typesCursor.getInt(typesCursor.getColumnIndex("isEnabledInQuickAccess")))
        Assert.assertEquals(1, typesCursor.getInt(typesCursor.getColumnIndex("isSalaryByDefault")))

        //Holiday Pay type:
        Assert.assertTrue(typesCursor.moveToNext())
        Assert.assertEquals(holidayPayTypeId, Uuid.fromByteArray(typesCursor.getBlob(typesCursor.getColumnIndex("typeId"))))
        Assert.assertEquals("Holiday Pay", typesCursor.getString(typesCursor.getColumnIndex("name")))
        Assert.assertEquals(2, typesCursor.getInt(typesCursor.getColumnIndex("icon")))
        Assert.assertEquals(0, typesCursor.getInt(typesCursor.getColumnIndex("isHoursWorkedEditable")))
        Assert.assertEquals(LocalDateTime.of(2025, 4, 1, 12, 35, 58).toEpochSecond(ZoneOffset.UTC), typesCursor.getLong(typesCursor.getColumnIndex("created")))
        Assert.assertEquals(LocalDateTime.of(2025, 4, 13, 15, 16, 23).toEpochSecond(ZoneOffset.UTC), typesCursor.getLong(typesCursor.getColumnIndex("edited")))
        Assert.assertEquals(0, typesCursor.getInt(typesCursor.getColumnIndex("isEnabledInQuickAccess")))
        Assert.assertEquals(1, typesCursor.getInt(typesCursor.getColumnIndex("isSalaryByDefault")))

        //Transfer 1:
        Assert.assertTrue(transfersCursor.moveToFirst())
        Assert.assertEquals(transfer1Id, Uuid.fromByteArray(transfersCursor.getBlob(transfersCursor.getColumnIndex("transferId"))))
        Assert.assertEquals(salaryTypeId, Uuid.fromByteArray(transfersCursor.getBlob(transfersCursor.getColumnIndex("type"))))
        Assert.assertEquals(123456, transfersCursor.getInt(transfersCursor.getColumnIndex("value")))
        Assert.assertEquals(140, transfersCursor.getInt(transfersCursor.getColumnIndex("hoursWorked")))
        Assert.assertEquals(0, transfersCursor.getInt(transfersCursor.getColumnIndex("isSalary")))
        Assert.assertEquals(LocalDate.of(2025, 4, 29).toEpochDay(), transfersCursor.getLong(transfersCursor.getColumnIndex("valueDate")))
        Assert.assertEquals(LocalDateTime.of(2025, 4, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC), transfersCursor.getLong(transfersCursor.getColumnIndex("created")))
        Assert.assertEquals(LocalDateTime.of(2025, 4, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC), transfersCursor.getLong(transfersCursor.getColumnIndex("edited")))

        //Transfer 2:
        Assert.assertTrue(transfersCursor.moveToNext())
        Assert.assertEquals(transfer2Id, Uuid.fromByteArray(transfersCursor.getBlob(transfersCursor.getColumnIndex("transferId"))))
        Assert.assertEquals(salaryTypeId, Uuid.fromByteArray(transfersCursor.getBlob(transfersCursor.getColumnIndex("type"))))
        Assert.assertEquals(12345, transfersCursor.getInt(transfersCursor.getColumnIndex("value")))
        Assert.assertEquals(160, transfersCursor.getInt(transfersCursor.getColumnIndex("hoursWorked")))
        Assert.assertEquals(0, transfersCursor.getInt(transfersCursor.getColumnIndex("isSalary")))
        Assert.assertEquals(LocalDate.of(2025, 5, 30).toEpochDay(), transfersCursor.getLong(transfersCursor.getColumnIndex("valueDate")))
        Assert.assertEquals(LocalDateTime.of(2025, 5, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC), transfersCursor.getLong(transfersCursor.getColumnIndex("created")))
        Assert.assertEquals(LocalDateTime.of(2025, 5, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC), transfersCursor.getLong(transfersCursor.getColumnIndex("edited")))

        //Transfer 3:
        Assert.assertTrue(transfersCursor.moveToNext())
        Assert.assertEquals(transfer3Id, Uuid.fromByteArray(transfersCursor.getBlob(transfersCursor.getColumnIndex("transferId"))))
        Assert.assertEquals(holidayPayTypeId, Uuid.fromByteArray(transfersCursor.getBlob(transfersCursor.getColumnIndex("type"))))
        Assert.assertEquals(1234, transfersCursor.getInt(transfersCursor.getColumnIndex("value")))
        Assert.assertEquals(0, transfersCursor.getInt(transfersCursor.getColumnIndex("hoursWorked")))
        Assert.assertEquals(0, transfersCursor.getInt(transfersCursor.getColumnIndex("isSalary")))
        Assert.assertEquals(LocalDate.of(2025, 5, 30).toEpochDay(), transfersCursor.getLong(transfersCursor.getColumnIndex("valueDate")))
        Assert.assertEquals(LocalDateTime.of(2025, 5, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC), transfersCursor.getLong(transfersCursor.getColumnIndex("created")))
        Assert.assertEquals(LocalDateTime.of(2025, 5, 28, 15, 18, 23).toEpochSecond(ZoneOffset.UTC), transfersCursor.getLong(transfersCursor.getColumnIndex("edited")))

        typesCursor.close()
        transfersCursor.close()
    }


    @Test
    @Throws(IOException::class)
    fun migrate1ToLatest() {
        val salaryTypeId: Uuid = Uuid.random()
        val holidayPayTypeId: Uuid = Uuid.random()
        val transfer1Id: Uuid = Uuid.random()
        val transfer2Id: Uuid = Uuid.random()
        val transfer3Id: Uuid = Uuid.random()

        //Create database on version 1:
        var db = helper.createDatabase(TEST_DB, 1).apply {
            //Types:
            val salaryTypeStmt = compileStatement("INSERT INTO `types` (`typeId`, `name`, `icon`, `isHoursWorkedEditable`, `created`, `edited`) VALUES(?, ?, ?, ?, ?, ?)")
            salaryTypeStmt.bindBlob(1, salaryTypeId.toByteArray())
            salaryTypeStmt.bindString(2, "Salary")
            salaryTypeStmt.bindLong(3, 1)
            salaryTypeStmt.bindLong(4, 1)
            salaryTypeStmt.bindLong(5, LocalDateTime.of(2025, 4, 1, 12, 34, 3).toEpochSecond(ZoneOffset.UTC))
            salaryTypeStmt.bindLong(6, LocalDateTime.of(2025, 4, 1, 12, 34, 3).toEpochSecond(ZoneOffset.UTC))
            salaryTypeStmt.executeInsert()

            val holidayPayTypeStmt = compileStatement("INSERT INTO `types` (`typeId`, `name`, `icon`, `isHoursWorkedEditable`, `created`, `edited`) VALUES(?, ?, ?, ?, ?, ?)")
            holidayPayTypeStmt.bindBlob(1, holidayPayTypeId.toByteArray())
            holidayPayTypeStmt.bindString(2, "Holiday Pay")
            holidayPayTypeStmt.bindLong(3, 2)
            holidayPayTypeStmt.bindLong(4, 0)
            holidayPayTypeStmt.bindLong(5, LocalDateTime.of(2025, 4, 1, 12, 35, 58).toEpochSecond(ZoneOffset.UTC))
            holidayPayTypeStmt.bindLong(6, LocalDateTime.of(2025, 4, 13, 15, 16, 23).toEpochSecond(ZoneOffset.UTC))
            holidayPayTypeStmt.executeInsert()

            //Transfers:
            val transfer1Stmt = compileStatement("INSERT INTO `transfers` (`transferId`, `type`, `value`, `hoursWorked`, `isSalary`, `valueDate`, `created`, `edited`) VALUES(?, ?, ?, ?, ?, ?, ?, ?)")
            transfer1Stmt.bindBlob(1, transfer1Id.toByteArray())
            transfer1Stmt.bindBlob(2, salaryTypeId.toByteArray())
            transfer1Stmt.bindLong(3, 123456)
            transfer1Stmt.bindLong(4, 140)
            transfer1Stmt.bindLong(5, 0)
            transfer1Stmt.bindLong(6, LocalDate.of(2025, 4, 29).toEpochDay())
            transfer1Stmt.bindLong(7, LocalDateTime.of(2025, 4, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC))
            transfer1Stmt.bindLong(8, LocalDateTime.of(2025, 4, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC))
            transfer1Stmt.executeInsert()

            val transfer2Stmt = compileStatement("INSERT INTO `transfers` (`transferId`, `type`, `value`, `hoursWorked`, `isSalary`, `valueDate`, `created`, `edited`) VALUES(?, ?, ?, ?, ?, ?, ?, ?)")
            transfer2Stmt.bindBlob(1, transfer2Id.toByteArray())
            transfer2Stmt.bindBlob(2, salaryTypeId.toByteArray())
            transfer2Stmt.bindLong(3, 12345)
            transfer2Stmt.bindLong(4, 160)
            transfer2Stmt.bindLong(5, 0)
            transfer2Stmt.bindLong(6, LocalDate.of(2025, 5, 30).toEpochDay())
            transfer2Stmt.bindLong(7, LocalDateTime.of(2025, 5, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC))
            transfer2Stmt.bindLong(8, LocalDateTime.of(2025, 5, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC))
            transfer2Stmt.executeInsert()

            val transfer3Stmt = compileStatement("INSERT INTO `transfers` (`transferId`, `type`, `value`, `hoursWorked`, `isSalary`, `valueDate`, `created`, `edited`) VALUES(?, ?, ?, ?, ?, ?, ?, ?)")
            transfer3Stmt.bindBlob(1, transfer3Id.toByteArray())
            transfer3Stmt.bindBlob(2, holidayPayTypeId.toByteArray())
            transfer3Stmt.bindLong(3, 1234)
            transfer3Stmt.bindLong(4, 0)
            transfer3Stmt.bindLong(5, 0)
            transfer3Stmt.bindLong(6, LocalDate.of(2025, 5, 30).toEpochDay())
            transfer3Stmt.bindLong(7, LocalDateTime.of(2025, 5, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC))
            transfer3Stmt.bindLong(8, LocalDateTime.of(2025, 5, 28, 15, 18, 23).toEpochSecond(ZoneOffset.UTC))
            transfer3Stmt.executeInsert()

            close()
        }

        //Migrate to latest version:
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)
        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, MIGRATION_2_3)

        //Validate data after migration:
        val typesCursor = db.query("SELECT * FROM `types` ORDER BY `icon` ASC")
        val transfersCursor = db.query("SELECT * FROM `transfers` ORDER BY `value` DESC")

        //Salary type:
        Assert.assertTrue(typesCursor.moveToFirst())
        Assert.assertEquals(salaryTypeId, Uuid.fromByteArray(typesCursor.getBlob(typesCursor.getColumnIndex("typeId"))))
        Assert.assertEquals("Salary", typesCursor.getString(typesCursor.getColumnIndex("name")))
        Assert.assertEquals(1, typesCursor.getInt(typesCursor.getColumnIndex("icon")))
        Assert.assertEquals(1, typesCursor.getInt(typesCursor.getColumnIndex("isHoursWorkedEditable")))
        Assert.assertEquals(LocalDateTime.of(2025, 4, 1, 12, 34, 3).toEpochSecond(ZoneOffset.UTC), typesCursor.getLong(typesCursor.getColumnIndex("created")))
        Assert.assertEquals(LocalDateTime.of(2025, 4, 1, 12, 34, 3).toEpochSecond(ZoneOffset.UTC), typesCursor.getLong(typesCursor.getColumnIndex("edited")))
        Assert.assertEquals(1, typesCursor.getInt(typesCursor.getColumnIndex("isEnabledInQuickAccess")))
        Assert.assertEquals(1, typesCursor.getInt(typesCursor.getColumnIndex("isSalaryByDefault")))

        //Holiday Pay type:
        Assert.assertTrue(typesCursor.moveToNext())
        Assert.assertEquals(holidayPayTypeId, Uuid.fromByteArray(typesCursor.getBlob(typesCursor.getColumnIndex("typeId"))))
        Assert.assertEquals("Holiday Pay", typesCursor.getString(typesCursor.getColumnIndex("name")))
        Assert.assertEquals(2, typesCursor.getInt(typesCursor.getColumnIndex("icon")))
        Assert.assertEquals(0, typesCursor.getInt(typesCursor.getColumnIndex("isHoursWorkedEditable")))
        Assert.assertEquals(LocalDateTime.of(2025, 4, 1, 12, 35, 58).toEpochSecond(ZoneOffset.UTC), typesCursor.getLong(typesCursor.getColumnIndex("created")))
        Assert.assertEquals(LocalDateTime.of(2025, 4, 13, 15, 16, 23).toEpochSecond(ZoneOffset.UTC), typesCursor.getLong(typesCursor.getColumnIndex("edited")))
        Assert.assertEquals(1, typesCursor.getInt(typesCursor.getColumnIndex("isEnabledInQuickAccess")))
        Assert.assertEquals(1, typesCursor.getInt(typesCursor.getColumnIndex("isSalaryByDefault")))

        //Transfer 1:
        Assert.assertTrue(transfersCursor.moveToFirst())
        Assert.assertEquals(transfer1Id, Uuid.fromByteArray(transfersCursor.getBlob(transfersCursor.getColumnIndex("transferId"))))
        Assert.assertEquals(salaryTypeId, Uuid.fromByteArray(transfersCursor.getBlob(transfersCursor.getColumnIndex("type"))))
        Assert.assertEquals(123456, transfersCursor.getInt(transfersCursor.getColumnIndex("value")))
        Assert.assertEquals(140, transfersCursor.getInt(transfersCursor.getColumnIndex("hoursWorked")))
        Assert.assertEquals(0, transfersCursor.getInt(transfersCursor.getColumnIndex("isSalary")))
        Assert.assertEquals(LocalDate.of(2025, 4, 29).toEpochDay(), transfersCursor.getLong(transfersCursor.getColumnIndex("valueDate")))
        Assert.assertEquals(LocalDateTime.of(2025, 4, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC), transfersCursor.getLong(transfersCursor.getColumnIndex("created")))
        Assert.assertEquals(LocalDateTime.of(2025, 4, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC), transfersCursor.getLong(transfersCursor.getColumnIndex("edited")))

        //Transfer 2:
        Assert.assertTrue(transfersCursor.moveToNext())
        Assert.assertEquals(transfer2Id, Uuid.fromByteArray(transfersCursor.getBlob(transfersCursor.getColumnIndex("transferId"))))
        Assert.assertEquals(salaryTypeId, Uuid.fromByteArray(transfersCursor.getBlob(transfersCursor.getColumnIndex("type"))))
        Assert.assertEquals(12345, transfersCursor.getInt(transfersCursor.getColumnIndex("value")))
        Assert.assertEquals(160, transfersCursor.getInt(transfersCursor.getColumnIndex("hoursWorked")))
        Assert.assertEquals(0, transfersCursor.getInt(transfersCursor.getColumnIndex("isSalary")))
        Assert.assertEquals(LocalDate.of(2025, 5, 30).toEpochDay(), transfersCursor.getLong(transfersCursor.getColumnIndex("valueDate")))
        Assert.assertEquals(LocalDateTime.of(2025, 5, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC), transfersCursor.getLong(transfersCursor.getColumnIndex("created")))
        Assert.assertEquals(LocalDateTime.of(2025, 5, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC), transfersCursor.getLong(transfersCursor.getColumnIndex("edited")))

        //Transfer 3:
        Assert.assertTrue(transfersCursor.moveToNext())
        Assert.assertEquals(transfer3Id, Uuid.fromByteArray(transfersCursor.getBlob(transfersCursor.getColumnIndex("transferId"))))
        Assert.assertEquals(holidayPayTypeId, Uuid.fromByteArray(transfersCursor.getBlob(transfersCursor.getColumnIndex("type"))))
        Assert.assertEquals(1234, transfersCursor.getInt(transfersCursor.getColumnIndex("value")))
        Assert.assertEquals(0, transfersCursor.getInt(transfersCursor.getColumnIndex("hoursWorked")))
        Assert.assertEquals(0, transfersCursor.getInt(transfersCursor.getColumnIndex("isSalary")))
        Assert.assertEquals(LocalDate.of(2025, 5, 30).toEpochDay(), transfersCursor.getLong(transfersCursor.getColumnIndex("valueDate")))
        Assert.assertEquals(LocalDateTime.of(2025, 5, 28, 15, 16, 23).toEpochSecond(ZoneOffset.UTC), transfersCursor.getLong(transfersCursor.getColumnIndex("created")))
        Assert.assertEquals(LocalDateTime.of(2025, 5, 28, 15, 18, 23).toEpochSecond(ZoneOffset.UTC), transfersCursor.getLong(transfersCursor.getColumnIndex("edited")))

        typesCursor.close()
        transfersCursor.close()
    }

}
