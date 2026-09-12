package com.fpculcasi.carezze.ui.profile

import com.fpculcasi.carezze.domain.model.Language
import com.fpculcasi.carezze.domain.model.TemperatureUnit
import com.fpculcasi.carezze.domain.model.User
import com.fpculcasi.carezze.domain.repository.AuthRepository
import com.fpculcasi.carezze.domain.usecase.auth.ObserveAuthStateUseCase
import com.fpculcasi.carezze.domain.usecase.auth.SignOutUseCase
import com.fpculcasi.carezze.domain.usecase.user.ObserveUserUseCase
import com.fpculcasi.carezze.domain.usecase.user.SyncUserUseCase
import com.fpculcasi.carezze.ui.auth.AuthUiState
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
class ProfileViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val observeAuthState = mockk<ObserveAuthStateUseCase>()
    private val observeUser = mockk<ObserveUserUseCase>()
    private val syncUser = mockk<SyncUserUseCase>(relaxed = true)
    private val signOut = mockk<SignOutUseCase>(relaxed = true)
    private val authRepository = mockk<AuthRepository>()

    private lateinit var viewModel: ProfileViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { authRepository.currentUser } returns null
    }

    @AfterEach
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = ProfileViewModel(observeAuthState, observeUser, syncUser, signOut, authRepository)
    }

    @Test
    fun `authState is SignedOut when auth stream emits null`() =
        runTest(testDispatcher) {
            every { observeAuthState() } returns flowOf(null)
            createViewModel()
            val job = launch { viewModel.authState.collect {} }
            assertEquals(AuthUiState.SignedOut, viewModel.authState.value)
            job.cancel()
        }

    @Test
    fun `authState is Anonymous when user is anonymous`() =
        runTest(testDispatcher) {
            val user = fakeUser(isAnonymous = true)
            every { observeAuthState() } returns flowOf(user)
            createViewModel()
            val job = launch { viewModel.authState.collect {} }
            assertEquals(AuthUiState.Anonymous(user), viewModel.authState.value)
            job.cancel()
        }

    @Test
    fun `authState is Authenticated when user is not anonymous`() =
        runTest(testDispatcher) {
            val user = fakeUser(isAnonymous = false)
            every { observeAuthState() } returns flowOf(user)
            createViewModel()
            val job = launch { viewModel.authState.collect {} }
            assertEquals(AuthUiState.Authenticated(user), viewModel.authState.value)
            job.cancel()
        }

    // region helpers

    private fun fakeUser(isAnonymous: Boolean) =
        User(
            id = "uid-1",
            email = if (isAnonymous) null else "test@example.com",
            displayName = "Test",
            language = Language.IT,
            temperatureUnit = TemperatureUnit.C,
            quietHoursStart = "22:00",
            quietHoursEnd = "08:00",
            personAccess = emptyList(),
            therapyAccess = emptyList(),
            isAnonymous = isAnonymous,
        )

    // endregion
}
