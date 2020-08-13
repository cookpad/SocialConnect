package cookpad.com.socialconnect.internal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.ExecutionException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

internal class ConnectViewModel(
    private val serviceHelper: ServiceHelper,
    private val coroutineDispatcher: CoroutineContext = EmptyCoroutineContext
) : ViewModel() {
    private val _viewStates = MutableLiveData<ViewState>()
    val viewStates: LiveData<ViewState>
        get() = _viewStates

    init {
        viewModelScope.launch(context = coroutineDispatcher) {
            safeExecution {
                _viewStates.value = ViewState.LoadUrl(serviceHelper.authUrl())
            }
        }
    }

    fun retrieveToken(url: String) {
        viewModelScope.launch(context = coroutineDispatcher) {
            safeExecution {
                _viewStates.value = ViewState.FinishWithToken(serviceHelper.token(url))
            }
        }
    }

    private suspend fun safeExecution(exec: suspend () -> Unit) {
        try {
            exec()
        } catch (error: IOException) {
            _viewStates.value = ViewState.FinishWithError(error)
        } catch (error: InterruptedException) {
            _viewStates.value = ViewState.FinishWithError(error)
        } catch (error: ExecutionException) {
            _viewStates.value = ViewState.FinishWithError(error)
        }
    }
}
