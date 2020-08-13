package cookpad.com.socialconnect

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Default QueryStringStrategy for OAuth2 providers
 */
@Parcelize
class DefaultStrategyOAuth2 : QueryStringStrategy, Parcelable {
    override fun extractCode(uri: Uri): String? {
        return uri.getQueryParameter("code")
    }

    override fun extractError(uri: Uri): String? {
        return uri.getQueryParameter("error")
    }
}
