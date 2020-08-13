package cookpad.com.socialconnect.internal

import android.content.Context
import android.os.Build
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.WebView

@SuppressWarnings("deprecation")
fun WebView.clearCookies(context: Context) {
    clearCache(true)
    clearHistory()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
    } else {
        val cookieSyncManager = CookieSyncManager.createInstance(context)
        cookieSyncManager.startSync()
        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookie()
        cookieManager.removeSessionCookie()
        cookieSyncManager.stopSync()
        cookieSyncManager.sync()
    }
}
