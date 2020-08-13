package cookpad.com.socialconnect.internal

import com.github.scribejava.core.model.Token

internal sealed class ViewState {
    data class LoadUrl(val authUrl: String) : ViewState()
    data class FinishWithToken(val token: Token) : ViewState()
    data class FinishWithError(val error: Throwable) : ViewState()
}
