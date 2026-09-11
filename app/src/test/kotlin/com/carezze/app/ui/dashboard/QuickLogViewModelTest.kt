package com.fpculcasi.carezze.ui.dashboard

import com.fpculcasi.carezze.domain.model.Language
import com.fpculcasi.carezze.domain.model.Medication
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
import com.fpculcasi.carezze.domain.usecase.therapy.LogMedicationUseCase
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
import org.junit.jupiter.api.Assertions.assertFalse
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
    private val logMedication = mockk<LogMedicationUseCase>()

    private lateinit var viewModel: QuickLogViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel =
            QuickLogViewModel(
                logActivity, authRepository, observeTherapies, addManualMedicationLog, logMedication,
            )
    }

    @AfterEach
    fun teardown() {
        Dispatchers.resetMain()
    }

    // region logMedication (manuale)

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

    // endregion

    // region confirmScheduledDose

    @Test
    fun `confirmScheduledDose sets isSaved on success`() =
        runTest {
            every { authRepository.currentUser } returns fakeUser()
            coEvery {
                logMedication(
                    personId = "pid-1",
                    therapyId = "tid-1",
                    medicationId = "med-1",
                    scheduledTime = any(),
                    status = MedicationStatus.TAKEN,
                    userId = "uid-1",
                )
            } returns Result.success(fakeLog(isManual = false))

            viewModel.confirmScheduledDose("pid-1", fakeScheduledDose())

            assertTrue(viewModel.state.value.isSaved)
            assertFalse(viewModel.state.value.isLoading)
        }

    @Test
    fun `confirmScheduledDose sets error on failure`() =
        runTest {
            every { authRepository.currentUser } returns fakeUser()
            coEvery {
                logMedication(any(), any(), any(), any(), any(), any())
            } returns Result.failure(Exception("sync error"))

            viewModel.confirmScheduledDose("pid-1", fakeScheduledDose())

            assertEquals("sync error", viewModel.state.value.error)
            assertFalse(viewModel.state.value.isLoading)
        }

    @Test
    fun `confirmScheduledDose does nothing when currentUser is null`() =
        runTest {
            every { authRepository.currentUser } returns null

            viewModel.confirmScheduledDose("pid-1", fakeScheduledDose())

            assertFalse(viewModel.state.value.isLoading)
            assertFalse(viewModel.state.value.isSaved)
        }

    // endregion

    // region selectType THERAPY

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
    fun `selectType THERAPY computes scheduledDoses from medication scheduledTimes`() =
        runTest {
            val med = fakeMedication(scheduledTimes = listOf("08:00", "16:00"))
            val therapy = fakeTherapy(id = "t1", isActive = true, medications = listOf(med))
            every { observeTherapies("pid-1") } returns flowOf(listOf(therapy))

            viewModel.selectType(ActivityLogType.THERAPY, "pid-1")

            val doses = viewModel.state.value.scheduledDoses
            assertEquals(2, doses.size)
            assertEquals("08:00", doses[0].timeLabel)
            assertEquals("16:00", doses[1].timeLabel)
            assertEquals("t1", doses[0].therapyId)
            assertEquals("med-1", doses[0].medicationId)
        }

    // endregion

    // region manual dose flow

    @Test
    fun `enterManualDoseFlow sets showManualDoseFlow to true`() =
        runTest {
            viewModel.enterManualDoseFlow()

            assertTrue(viewModel.state.value.showManualDoseFlow)
        }

    @Test
    fun `exitManualDoseFlow resets showManualDoseFlow and selection`() =
        runTest {
            every { observeTherapies("pid-1") } returns flowOf(listOf(fakeTherapy()))
            viewModel.selectType(ActivityLogType.THERAPY, "pid-1")
            viewModel.enterManualDoseFlow()
            viewModel.selectTherapy("t1")
            viewModel.selectMedication("m1")

            viewModel.exitManualDoseFlow()

            assertFalse(viewModel.state.value.showManualDoseFlow)
            assertNull(viewModel.state.value.selectedTherapyId)
            assertNull(viewModel.state.value.selectedMedicationId)
        }

    // endregion

    // region clearType

    @Test
    fun `clearType resets all therapy state`() =
        runTest {
            every { observeTherapies("pid-1") } returns flowOf(listOf(fakeTherapy()))
            viewModel.selectType(ActivityLogType.THERAPY, "pid-1")

            viewModel.clearType()

            assertEquals(emptyList<Therapy>(), viewModel.state.value.therapies)
            assertEquals(emptyList<ScheduledDose>(), viewModel.state.value.scheduledDoses)
            assertFalse(viewModel.state.value.showManualDoseFlow)
            assertNull(viewModel.state.value.selectedType)
            assertNull(viewModel.state.value.selectedTherapyId)
        }

    // endregion

    // region helpers

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

    private fun fakeMedication(scheduledTimes: List<String> = listOf("08:00")) =
        Medication(
            id = "med-1",
            name = "Paracetamolo",
            dosage = 5.0,
            dosageUnit = "ml",
            frequencyHours = 8,
            scheduledTimes = scheduledTimes,
            startDate = LocalDate.now(),
            notes = null,
        )

    private fun fakeTherapy(
        id: String = "tid-1",
        isActive: Boolean = true,
        medications: List<Medication> = emptyList(),
    ) = Therapy(
        id = id,
        personId = "pid-1",
        name = "Amoxicillina",
        createdBy = "uid-1",
        startDate = LocalDate.now(),
        duration = TherapyDuration.Fixed(7),
        isActive = isActive,
        members = mapOf("uid-1" to MemberRole.OWNER),
        medications = medications,
    )

    private fun fakeLog(isManual: Boolean = true) =
        MedicationLog(
            id = "log-1",
            therapyId = "tid-1",
            medicationId = "med-1",
            scheduledTime = Instant.now(),
            actualTime = Instant.now(),
            status = MedicationStatus.TAKEN,
            loggedBy = "uid-1",
            isManual = isManual,
        )

    private fun fakeScheduledDose() =
        ScheduledDose(
            therapyId = "tid-1",
            therapyName = "Amoxicillina",
            medicationId = "med-1",
            medicationName = "Paracetamolo",
            timeLabel = "08:00",
            scheduledTime = Instant.now(),
        )

    // endregion
}
