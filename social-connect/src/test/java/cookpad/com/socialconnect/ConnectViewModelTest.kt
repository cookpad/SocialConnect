package cookpad.com.socialconnect

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.scribejava.core.model.Token
import cookpad.com.socialconnect.internal.ConnectViewModel
import cookpad.com.socialconnect.internal.ServiceHelper
import cookpad.com.socialconnect.internal.ViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ConnectViewModelTest {
    @Suppress("unused")
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    @ExperimentalCoroutinesApi
    @Test
    fun verifyGetUrlSuccess() {
        val authUrl = "authUrl"
        val serviceHelper = object : ServiceHelper {
            override suspend fun token(url: String) = error("Not required")
            override suspend fun authUrl() = authUrl
            override fun callbackUrl() = error("Not required")
        }

        val viewModel = ConnectViewModel(serviceHelper, testCoroutineDispatcher)

        val viewState = viewModel.viewStates.getOrAwaitValue() as ViewState.LoadUrl
        assert(viewState.authUrl == authUrl)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun verifyGetUrlError() {
        val error = IOException()
        val serviceHelper = object : ServiceHelper {
            override suspend fun token(url: String) = error("Not required")
            override suspend fun authUrl() = throw error
            override fun callbackUrl() = error("Not required")
        }

        val viewModel = ConnectViewModel(serviceHelper, testCoroutineDispatcher)

        val viewState = viewModel.viewStates.getOrAwaitValue() as ViewState.FinishWithError
        assert(viewState.error == error)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun verifyGetTokenSuccess() {
        val tokenValue = object : Token("") {}
        val authUrl = "authUrl"
        val serviceHelper = object : ServiceHelper {
            override suspend fun token(url: String) = tokenValue
            override suspend fun authUrl() = authUrl
            override fun callbackUrl() = error("Not required")
        }

        val viewModel = ConnectViewModel(serviceHelper, testCoroutineDispatcher)
        viewModel.retrieveToken(authUrl)

        val viewState = viewModel.viewStates.getOrAwaitValue() as ViewState.FinishWithToken
        assert(viewState.token == tokenValue)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun verifyGetTokenError() {
        val error = IOException()
        val authUrl = "authUrl"
        val serviceHelper = object : ServiceHelper {
            override suspend fun token(url: String) = throw error
            override suspend fun authUrl() = authUrl
            override fun callbackUrl() = error("Not required")
        }

        val viewModel = ConnectViewModel(serviceHelper, testCoroutineDispatcher)
        viewModel.retrieveToken(authUrl)

        val viewState = viewModel.viewStates.getOrAwaitValue() as ViewState.FinishWithError
        assert(viewState.error == error)
    }
}
