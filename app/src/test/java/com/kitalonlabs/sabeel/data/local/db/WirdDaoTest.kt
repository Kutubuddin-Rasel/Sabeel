package com.kitalonlabs.sabeel.data.local.db

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kitalonlabs.sabeel.data.local.db.dao.WirdDao
import com.kitalonlabs.sabeel.data.local.db.entity.WirdItemEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
class WirdDaoTest {

    private lateinit var db: SabeelDatabase
    private lateinit var dao: WirdDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SabeelDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.wirdDao()
    }

    @After
    fun teardown() = db.close()

    @Test
    fun observeWird_returnsItemsOrderedByPosition() = runTest {
        dao.upsert(WirdItemEntity("B", 33, 1))
        dao.upsert(WirdItemEntity("A", 33, 0))
        val items = dao.observeWird().first()
        assertEquals(listOf("A", "B"), items.map { it.dhikrKey })
    }

    @Test
    fun upsert_isUniqueByKey() = runTest {
        dao.upsert(WirdItemEntity("A", 33, 0))
        dao.upsert(WirdItemEntity("A", 100, 0)) // same key overwrites, not duplicates
        val items = dao.observeWird().first()
        assertEquals(1, items.size)
        assertEquals(100, items.first().target)
    }

    @Test
    fun delete_removesByKey() = runTest {
        dao.upsert(WirdItemEntity("A", 33, 0))
        dao.delete("A")
        assertEquals(0, dao.count())
    }

    @Test
    fun maxPosition_isMinusOneWhenEmpty_thenHighest() = runTest {
        assertEquals(-1, dao.maxPosition())
        dao.upsert(WirdItemEntity("A", 33, 0))
        dao.upsert(WirdItemEntity("B", 33, 5))
        assertEquals(5, dao.maxPosition())
    }

    @Test
    fun getByKey_returnsRowOrNull() = runTest {
        dao.upsert(WirdItemEntity("A", 33, 0))
        assertEquals(33, dao.getByKey("A")?.target)
        assertNull(dao.getByKey("Z"))
    }

    @Test
    fun reorder_rewritesPositions() = runTest {
        dao.upsert(WirdItemEntity("A", 33, 0))
        dao.upsert(WirdItemEntity("B", 33, 1))
        dao.reorder(listOf(WirdItemEntity("B", 33, 0), WirdItemEntity("A", 33, 1)))
        assertEquals(listOf("B", "A"), dao.observeWird().first().map { it.dhikrKey })
    }
}
