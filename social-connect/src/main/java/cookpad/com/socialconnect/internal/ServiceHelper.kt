package cookpad.com.socialconnect.internal

import android.net.Uri
import com.github.scribejava.core.model.OAuth1RequestToken
import com.github.scribejava.core.model.Token
import com.github.scribejava.core.oauth.OAuth10aService
import com.github.scribejava.core.oauth.OAuth20Service
import cookpad.com.socialconnect.QueryStringStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal interface ServiceHelper {
    suspend fun token(url: String): Token
    suspend fun authUrl(): String
    fun callbackUrl(): String
}

internal class ServiceHelperOAuth10(
    val service: OAuth10aService,
    val queryStringStrategy: QueryStringStrategy
) : ServiceHelper {
    private lateinit var oAuth1RequestToken: OAuth1RequestToken

    override suspend fun token(url: String): Token {
        val uri = Uri.parse(url)

        val error: String? = queryStringStrategy.extractError(uri)
        if (error?.isNotEmpty() == true) throw java.lang.RuntimeException(error)

        val verifier: String? = queryStringStrategy.extractCode(uri)
        return withContext(Dispatchers.IO) { service.getAccessToken(oAuth1RequestToken, verifier) }
    }

    override suspend fun authUrl(): String {
        return withContext(Dispatchers.IO) {
            oAuth1RequestToken = service.requestToken
            service.getAuthorizationUrl(oAuth1RequestToken)
        }
    }

    override fun callbackUrl(): String = service.callback
}

internal class ServiceHelperOAuth20(
    val service: OAuth20Service,
    val queryStringStrategy: QueryStringStrategy
) : ServiceHelper {

    override suspend fun token(url: String): Token {
        val uri = Uri.parse(url)
        val error: String? = queryStringStrategy.extractError(uri)
        if (error?.isNotEmpty() == true) throw java.lang.RuntimeException(error)

        val code: String? = queryStringStrategy.extractCode(uri)
        return withContext(Dispatchers.IO) { service.getAccessToken(code) }
    }

    override suspend fun authUrl(): String = withContext(Dispatchers.IO) { service.authorizationUrl }

    override fun callbackUrl(): String = service.callback
}
