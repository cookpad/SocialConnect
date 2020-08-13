package cookpad.com.socialconnect

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Default QueryStringStrategy for OAuth1 providers
 */
@Parcelize
class DefaultStrategyOAuth1 : QueryStringStrategy, Parcelable {
    override fun extractCode(uri: Uri): String? {
        return uri.getQueryParameter("oauth_verifier")
    }

    override fun extractError(uri: Uri): String? {
        return uri.getQueryParameter("error")
    }
}
