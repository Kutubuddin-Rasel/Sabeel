package com.kitalonlabs.sabeel.data.repository

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kitalonlabs.sabeel.data.local.db.SabeelDatabase
import com.kitalonlabs.sabeel.domain.model.DhikrType
import kotlinx.coroutines.Dispatchers
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
class WirdRepositoryImplTest {

    private lateinit var db: SabeelDatabase
    private lateinit var repo: WirdRepositoryImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SabeelDatabase::class.java)
            .allowMainThreadQueries().build()
        repo = WirdRepositoryImpl(db.wirdDao(), Dispatchers.Unconfined)
    }

    @After
    fun teardown() = db.close()

    @Test
    fun addToWird_appendsAtEnd_andClampsTargetToOne() = runTest {
        repo.addToWird("A", 33)
        repo.addToWird("B", 0) // clamps to 1
        val items = repo.observeWird().first()
        assertEquals(listOf("A", "B"), items.map { it.dhikrKey })
        assertEquals(listOf(0, 1), items.map { it.position })
        assertEquals(1, items.first { it.dhikrKey == "B" }.target)
    }

    @Test
    fun updateTarget_changesTarget_keepsPosition() = runTest {
        repo.addToWird("A", 33)
        repo.updateTarget("A", 100)
        val item = repo.observeWird().first().first()
        assertEquals(100, item.target)
        assertEquals(0, item.position)
    }

    @Test
    fun removeFromWird_deletes() = runTest {
        repo.addToWird("A", 33)
        repo.removeFromWird("A")
        assertEquals(emptyList<String>(), repo.observeWird().first().map { it.dhikrKey })
    }

    @Test
    fun reorder_appliesNewOrder() = runTest {
        repo.addToWird("A", 33)
        repo.addToWird("B", 33)
        repo.reorder(listOf("B", "A"))
        assertEquals(listOf("B", "A"), repo.observeWird().first().map { it.dhikrKey })
    }

    @Test
    fun seedDefaultIfEmpty_isNoOp() = runTest {
        repo.seedDefaultIfEmpty()
        val items = repo.observeWird().first()
        assertEquals(emptyList<String>(), items.map { it.dhikrKey })
    }
}
