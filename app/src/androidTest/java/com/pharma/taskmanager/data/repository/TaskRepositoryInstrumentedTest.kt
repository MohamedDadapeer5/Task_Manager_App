package com.pharma.taskmanager.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pharma.taskmanager.data.database.TaskManagerDatabase
import com.pharma.taskmanager.data.model.Task
import com.pharma.taskmanager.data.model.TaskPriority
import com.pharma.taskmanager.data.model.TaskStatus
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Example instrumented test showing how to use TestDatabaseModule.
 * This test runs on an Android device/emulator and uses the in-memory database
 * provided by TestDatabaseModule.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TaskRepositoryInstrumentedTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: TaskManagerDatabase
    
    @Inject
    lateinit var repository: TaskRepositoryImpl

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveTask() = runTest {
        // Given
        val task = Task(
            id = 1,
            title = "Test Task",
            description = "Test Description",
            priority = TaskPriority.HIGH,
            status = TaskStatus.PENDING,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // When
        repository.insertTask(task)
        val retrievedTasks = repository.getAllTasks().first()

        // Then
        assertEquals(1, retrievedTasks.size)
        assertEquals("Test Task", retrievedTasks[0].title)
        assertEquals(TaskPriority.HIGH, retrievedTasks[0].priority)
    }

    @Test
    fun searchTasksByTitle() = runTest {
        // Given
        val task1 = Task(
            id = 1,
            title = "Important Meeting",
            description = "Team meeting",
            priority = TaskPriority.HIGH,
            status = TaskStatus.PENDING,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val task2 = Task(
            id = 2,
            title = "Buy Groceries",
            description = "Weekly shopping",
            priority = TaskPriority.LOW,
            status = TaskStatus.PENDING,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        repository.insertTask(task1)
        repository.insertTask(task2)

        // When
        val searchResult = repository.searchTasks("Meeting").first()

        // Then
        assertEquals(1, searchResult.size)
        assertEquals("Important Meeting", searchResult[0].title)
    }

    @Test
    fun updateTaskStatus() = runTest {
        // Given
        val task = Task(
            id = 1,
            title = "Test Task",
            description = "Test Description",
            priority = TaskPriority.MEDIUM,
            status = TaskStatus.PENDING,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        repository.insertTask(task)

        // When
        repository.updateTaskStatus(1, TaskStatus.COMPLETED)
        val updatedTask = repository.getTaskById(1).first()

        // Then
        assertEquals(TaskStatus.COMPLETED, updatedTask?.status)
    }
}