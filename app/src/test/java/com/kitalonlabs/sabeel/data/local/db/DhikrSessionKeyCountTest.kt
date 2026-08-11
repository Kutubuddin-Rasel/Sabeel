package com.kitalonlabs.sabeel.data.local.db

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kitalonlabs.sabeel.data.local.db.dao.DhikrSessionDao
import com.kitalonlabs.sabeel.data.local.db.entity.DhikrSessionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
class DhikrSessionKeyCountTest {

    private lateinit var db: SabeelDatabase
    private lateinit var dao: DhikrSessionDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SabeelDatabase::class.java)
            .allowMainThreadQueries().build()
        dao = db.dhikrSessionDao()
    }

    @After
    fun teardown() = db.close()

    private fun session(key: String, count: Int, date: String) =
        DhikrSessionEntity(dhikrKey = key, count = count, target = 33,
            isComplete = true, dateKey = date, endedAt = 0L)

    @Test
    fun getCountsByKeyForDate_sumsPerKey_forThatDateOnly() = runTest {
        dao.insertSession(session("A", 33, "2026-07-03"))
        dao.insertSession(session("A", 33, "2026-07-03"))
        dao.insertSession(session("B", 10, "2026-07-03"))
        dao.insertSession(session("A", 99, "2026-07-02")) // other day — excluded

        val map = dao.getCountsByKeyForDate("2026-07-03").first()
            .associate { it.dhikrKey to it.total }

        assertEquals(66, map["A"])
        assertEquals(10, map["B"])
        assertEquals(null, map["Z"])
    }
}
