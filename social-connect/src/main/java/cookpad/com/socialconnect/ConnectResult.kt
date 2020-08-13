package cookpad.com.socialconnect

import android.os.Parcelable
import com.github.scribejava.core.model.Token
import kotlinx.android.parcel.Parcelize

sealed class ConnectResult

/**
 * Encodes a success response providing a Token instance: either an OAuth1Token or an OAuth2Token
 */
@Parcelize
data class ConnectResultOk(val token: Token) : ConnectResult(), Parcelable

/**
 * Encodes an failure response with the exception
 */
@Parcelize
data class ConnectResultError(val error: Throwable) : ConnectResult(), Parcelable
