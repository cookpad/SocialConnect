package cookpad.com.socialconnect.internal

import android.annotation.TargetApi
import android.os.Build
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

internal class OAuthWebViewClient(
    private val retrieveToken: (String) -> Unit,
    private val callbackUrl: String
) : WebViewClient() {

    @SuppressWarnings("deprecation")
    override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
        if (!url.startsWith(callbackUrl)) {
            return super.shouldOverrideUrlLoading(webView, url)
        }

        webView.visibility = View.GONE
        retrieveToken(url)
        return true
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(webView: WebView, request: WebResourceRequest): Boolean {
        val url = request.url.toString()
        if (!url.startsWith(callbackUrl)) {
            return super.shouldOverrideUrlLoading(webView, request)
        }

        webView.visibility = View.GONE
        retrieveToken(url)
        return true
    }
}
