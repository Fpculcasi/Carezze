package com.fpculcasi.carezze.ui.dashboard

import com.fpculcasi.carezze.domain.model.Language
import com.fpculcasi.carezze.domain.model.MedicationLog
import com.fpculcasi.carezze.domain.model.MedicationStatus
import com.fpculcasi.carezze.domain.model.MemberRole
import com.fpculcasi.carezze.domain.model.TemperatureUnit
import com.fpculcasi.carezze.domain.model.Therapy
import com.fpculcasi.carezze.domain.model.TherapyDuration
import com.fpculcasi.carezze.domain.model.User
import com.fpculcasi.carezze.domain.repository.AuthRepository
import com.fpculcasi.carezze.domain.usecase.activity.LogActivityUseCase
import com.fpculcasi.carezze.domain.usecase.therapy.AddManualMedicationLogUseCase
import com.fpculcasi.carezze.domain.usecase.therapy.ObserveTherapiesUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class QuickLogViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val logActivity = mockk<LogActivityUseCase>(relaxed = true)
    private val authRepository = mockk<AuthRepository>()
    private val observeTherapies = mockk<ObserveTherapiesUseCase>()
    private val addManualMedicationLog = mockk<AddManualMedicationLogUseCase>()

    private lateinit var viewModel: QuickLogViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = QuickLogViewModel(logActivity, authRepository, observeTherapies, addManualMedicationLog)
    }

    @AfterEach
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `logMedication does nothing when currentUser is null`() =
        runTest {
            every { authRepository.currentUser } returns null

            viewModel.logMedication("pid-1", "tid-1", "med-1")

            assertEquals(false, viewModel.state.value.isLoading)
            assertEquals(false, viewModel.state.value.isSaved)
            assertNull(viewModel.state.value.error)
        }

    @Test
    fun `logMedication sets isSaved on success`() =
        runTest {
            every { authRepository.currentUser } returns fakeUser()
            coEvery {
                addManualMedicationLog("pid-1", "tid-1", "med-1", any(), "uid-1")
            } returns Result.success(fakeLog())

            viewModel.logMedication("pid-1", "tid-1", "med-1")

            assertTrue(viewModel.state.value.isSaved)
            assertEquals(false, viewModel.state.value.isLoading)
        }

    @Test
    fun `logMedication sets error on failure`() =
        runTest {
            every { authRepository.currentUser } returns fakeUser()
            coEvery {
                addManualMedicationLog(any(), any(), any(), any(), any())
            } returns Result.failure(Exception("network error"))

            viewModel.logMedication("pid-1", "tid-1", "med-1")

            assertEquals("network error", viewModel.state.value.error)
            assertEquals(false, viewModel.state.value.isLoading)
        }

    @Test
    fun `selectType THERAPY loads only active therapies`() =
        runTest {
            val active = fakeTherapy(id = "t1", isActive = true)
            val inactive = fakeTherapy(id = "t2", isActive = false)
            every { observeTherapies("pid-1") } returns flowOf(listOf(active, inactive))

            viewModel.selectType(ActivityLogType.THERAPY, "pid-1")

            assertEquals(listOf(active), viewModel.state.value.therapies)
        }

    @Test
    fun `clearType resets therapy state`() =
        runTest {
            every { observeTherapies("pid-1") } returns flowOf(listOf(fakeTherapy()))
            viewModel.selectType(ActivityLogType.THERAPY, "pid-1")

            viewModel.clearType()

            assertEquals(emptyList<Therapy>(), viewModel.state.value.therapies)
            assertNull(viewModel.state.value.selectedType)
            assertNull(viewModel.state.value.selectedTherapyId)
        }

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

    private fun fakeTherapy(
        id: String = "tid-1",
        isActive: Boolean = true,
    ) = Therapy(
        id = id,
        personId = "pid-1",
        name = "Amoxicillina",
        createdBy = "uid-1",
        startDate = LocalDate.now(),
        duration = TherapyDuration.Fixed(7),
        isActive = isActive,
        members = mapOf("uid-1" to MemberRole.OWNER),
        medications = emptyList(),
    )

    private fun fakeLog() =
        MedicationLog(
            id = "log-1",
            therapyId = "tid-1",
            medicationId = "med-1",
            scheduledTime = Instant.now(),
            actualTime = Instant.now(),
            status = MedicationStatus.TAKEN,
            loggedBy = "uid-1",
            isManual = true,
        )
}
