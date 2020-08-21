package cookpad.com.socialconnect.internal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Suppress("TooGenericExceptionCaught")
internal class ConnectViewModel(
    private val serviceHelper: ServiceHelper,
    private val coroutineDispatcher: CoroutineContext = EmptyCoroutineContext
) : ViewModel() {
    private val _viewStates = MutableLiveData<ViewState>()
    val viewStates: LiveData<ViewState>
        get() = _viewStates

    init {
        viewModelScope.launch(context = coroutineDispatcher) {
            try {
                _viewStates.value = ViewState.LoadUrl(serviceHelper.authUrl())
            } catch (error: Throwable) {
                _viewStates.value = ViewState.FinishWithError(error)
            }
        }
    }

    fun retrieveToken(url: String) {
        viewModelScope.launch(context = coroutineDispatcher) {
            try {
                _viewStates.value = ViewState.FinishWithToken(serviceHelper.token(url))
            } catch (error: Throwable) {
                _viewStates.value = ViewState.FinishWithError(error)
            }
        }
    }
}
