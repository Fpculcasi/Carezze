package com.fpculcasi.carezze.ui.dashboard

import com.fpculcasi.carezze.domain.model.Language
import com.fpculcasi.carezze.domain.model.MemberRole
import com.fpculcasi.carezze.domain.model.Person
import com.fpculcasi.carezze.domain.model.TemperatureUnit
import com.fpculcasi.carezze.domain.model.User
import com.fpculcasi.carezze.domain.repository.AuthRepository
import com.fpculcasi.carezze.domain.usecase.activity.ObserveActivityLogsUseCase
import com.fpculcasi.carezze.domain.usecase.person.ObservePersonColorUseCase
import com.fpculcasi.carezze.domain.usecase.person.ObservePersonsUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val observePersons = mockk<ObservePersonsUseCase>()
    private val observeActivityLogs = mockk<ObserveActivityLogsUseCase>()
    private val observePersonColor = mockk<ObservePersonColorUseCase>()
    private val authRepository = mockk<AuthRepository>()

    private val alice = fakePerson(id = "alice-id", name = "Alice", nickname = null)
    private val bob = fakePerson(id = "bob-id", name = "Bob", nickname = "Bobby")

    private lateinit var viewModel: DashboardViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { authRepository.currentUser } returns fakeUser()
        every { observePersons("uid-1") } returns flowOf(listOf(alice, bob))
        every { observePersonColor(any()) } returns flowOf(0)
        every { observeActivityLogs(any(), any(), any()) } returns flowOf(emptyList())
        viewModel = DashboardViewModel(observePersons, observeActivityLogs, observePersonColor, authRepository)
    }

    @AfterEach
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `filteredPersons shows all persons when no filter`() =
        runTest(testDispatcher) {
            val job = launch { viewModel.filteredPersons.collect {} }
            assertEquals(listOf(alice, bob), viewModel.filteredPersons.value)
            job.cancel()
        }

    @Test
    fun `selectPerson shows only selected person`() =
        runTest(testDispatcher) {
            val job = launch { viewModel.filteredPersons.collect {} }
            viewModel.selectPerson("alice-id")
            assertEquals(listOf(alice), viewModel.filteredPersons.value)
            job.cancel()
        }

    @Test
    fun `selectPerson null restores all persons`() =
        runTest(testDispatcher) {
            val job = launch { viewModel.filteredPersons.collect {} }
            viewModel.selectPerson("alice-id")
            viewModel.selectPerson(null)
            assertEquals(listOf(alice, bob), viewModel.filteredPersons.value)
            job.cancel()
        }

    @Test
    fun `searchQuery filters by name case-insensitive`() =
        runTest(testDispatcher) {
            val job = launch { viewModel.filteredPersons.collect {} }
            viewModel.setSearchQuery("alice")
            assertEquals(listOf(alice), viewModel.filteredPersons.value)
            job.cancel()
        }

    @Test
    fun `searchQuery filters by nickname case-insensitive`() =
        runTest(testDispatcher) {
            val job = launch { viewModel.filteredPersons.collect {} }
            viewModel.setSearchQuery("Bobby")
            assertEquals(listOf(bob), viewModel.filteredPersons.value)
            job.cancel()
        }

    @Test
    fun `searchQuery blank shows all persons`() =
        runTest(testDispatcher) {
            val job = launch { viewModel.filteredPersons.collect {} }
            viewModel.setSearchQuery("alice")
            viewModel.setSearchQuery("")
            assertEquals(listOf(alice, bob), viewModel.filteredPersons.value)
            job.cancel()
        }

    @Test
    fun `toggleViewMode switches CARD to FEED`() =
        runTest(testDispatcher) {
            viewModel.toggleViewMode()
            assertEquals(DashboardViewMode.FEED, viewModel.viewMode.value)
        }

    @Test
    fun `toggleViewMode switches FEED back to CARD`() =
        runTest(testDispatcher) {
            viewModel.toggleViewMode()
            viewModel.toggleViewMode()
            assertEquals(DashboardViewMode.CARD, viewModel.viewMode.value)
        }

    // region helpers

    private fun fakePerson(
        id: String,
        name: String,
        nickname: String?,
    ) = Person(
        id = id,
        name = name,
        nickname = nickname,
        createdBy = "uid-1",
        members = mapOf("uid-1" to MemberRole.OWNER),
    )

    private fun fakeUser() =
        User(
            id = "uid-1",
            email = null,
            displayName = "Test",
            language = Language.IT,
            temperatureUnit = TemperatureUnit.C,
            quietHoursStart = "22:00",
            quietHoursEnd = "08:00",
            personAccess = emptyList(),
            therapyAccess = emptyList(),
            isAnonymous = false,
        )

    // endregion
}
