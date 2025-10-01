package com.pharma.taskmanager.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.pharma.taskmanager.data.database.TaskManagerDatabase
import com.pharma.taskmanager.data.model.Task
import com.pharma.taskmanager.data.model.TaskPriority
import com.pharma.taskmanager.data.model.TaskStatus
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Unit test for TaskRepository using Hilt's TestDatabaseModule.
 * This test uses the in-memory database provided by TestDatabaseModule.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class TaskRepositoryUnitTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

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
    fun `insertTask should add task to database`() = runTest {
        // Given
        val task = createSampleTask(id = 1, title = "Unit Test Task")

        // When
        repository.insertTask(task)
        val result = repository.getTaskById(1).first()

        // Then
        assertNotNull(result)
        assertEquals("Unit Test Task", result.title)
        assertEquals(TaskPriority.HIGH, result.priority)
    }

    @Test
    fun `deleteTask should remove task from database`() = runTest {
        // Given
        val task = createSampleTask(id = 1, title = "Task to Delete")
        repository.insertTask(task)

        // When
        repository.deleteTask(task)
        val result = repository.getTaskById(1).first()

        // Then
        assertNull(result)
    }

    @Test
    fun `updateTaskStatus should change task status`() = runTest {
        // Given
        val task = createSampleTask(id = 1, status = TaskStatus.PENDING)
        repository.insertTask(task)

        // When
        repository.updateTaskStatus(1, TaskStatus.IN_PROGRESS)
        val result = repository.getTaskById(1).first()

        // Then
        assertNotNull(result)
        assertEquals(TaskStatus.IN_PROGRESS, result.status)
    }

    @Test
    fun `searchTasks should return matching tasks`() = runTest {
        // Given
        val task1 = createSampleTask(id = 1, title = "Important Meeting")
        val task2 = createSampleTask(id = 2, title = "Buy Groceries")
        val task3 = createSampleTask(id = 3, title = "Team Meeting")
        
        repository.insertTask(task1)
        repository.insertTask(task2)
        repository.insertTask(task3)

        // When
        val searchResult = repository.searchTasks("Meeting").first()

        // Then
        assertEquals(2, searchResult.size)
        assertEquals(true, searchResult.any { it.title.contains("Meeting") })
    }

    @Test
    fun `getTasksByStatus should return tasks with specific status`() = runTest {
        // Given
        val pendingTask = createSampleTask(id = 1, status = TaskStatus.PENDING)
        val completedTask = createSampleTask(id = 2, status = TaskStatus.COMPLETED)
        val inProgressTask = createSampleTask(id = 3, status = TaskStatus.IN_PROGRESS)
        
        repository.insertTask(pendingTask)
        repository.insertTask(completedTask)
        repository.insertTask(inProgressTask)

        // When
        val pendingTasks = repository.getTasksByStatus(TaskStatus.PENDING).first()

        // Then
        assertEquals(1, pendingTasks.size)
        assertEquals(TaskStatus.PENDING, pendingTasks[0].status)
    }

    @Test
    fun `getTasksByPriority should return tasks with specific priority`() = runTest {
        // Given
        val highPriorityTask = createSampleTask(id = 1, priority = TaskPriority.HIGH)
        val lowPriorityTask = createSampleTask(id = 2, priority = TaskPriority.LOW)
        val mediumPriorityTask = createSampleTask(id = 3, priority = TaskPriority.MEDIUM)
        
        repository.insertTask(highPriorityTask)
        repository.insertTask(lowPriorityTask)
        repository.insertTask(mediumPriorityTask)

        // When
        val highPriorityTasks = repository.getTasksByPriority(TaskPriority.HIGH).first()

        // Then
        assertEquals(1, highPriorityTasks.size)
        assertEquals(TaskPriority.HIGH, highPriorityTasks[0].priority)
    }

    private fun createSampleTask(
        id: Long = 1,
        title: String = "Sample Task",
        description: String = "Sample Description",
        priority: TaskPriority = TaskPriority.HIGH,
        status: TaskStatus = TaskStatus.PENDING
    ): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            priority = priority,
            status = status,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }
}