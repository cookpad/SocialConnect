package cookpad.com.socialconnect

import android.net.Uri
import android.os.Parcelable

/**
 * Use it when some provider does not honor the standard query string formatter.
 */
interface QueryStringStrategy : Parcelable {
    /**
     * Given an uri, parse the query string to extract the associated oauth code.
     * @param uri the uri
     * @return the param code
     */
    fun extractCode(uri: Uri): String?

    /**
     * Given an uri, parse the query string to extract the associated error.
     * @param uri the uri
     * @return the error message
     */
    fun extractError(uri: Uri): String?
}
