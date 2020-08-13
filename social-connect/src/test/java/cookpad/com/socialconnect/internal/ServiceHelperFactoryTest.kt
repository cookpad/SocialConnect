package cookpad.com.socialconnect.internal

import android.os.Parcel
import com.github.scribejava.apis.GitHubApi
import com.github.scribejava.apis.TwitterApi
import cookpad.com.socialconnect.DefaultStrategyOAuth1
import cookpad.com.socialconnect.DefaultStrategyOAuth2
import cookpad.com.socialconnect.OAuth10ServiceConfig
import cookpad.com.socialconnect.OAuth20ServiceConfig
import cookpad.com.socialconnect.OAuthServiceConfig
import cookpad.com.socialconnect.QueryStringStrategy
import org.junit.Assert.assertThrows
import org.junit.Test


class ServiceHelperFactoryTest {

    @Test
    fun verifyOAuth10ServiceConfig() {
        val serviceHelper = ServiceHelperFactory(
            serviceConfig = OAuth10ServiceConfig(
                apiKey = API_KEY,
                apiSecret = API_SECRET,
                callback = CALLBACK_URL,
                clazz = TwitterApi::class.java
            )
        ).serviceHelper() as ServiceHelperOAuth10


        assert(serviceHelper.callbackUrl() == CALLBACK_URL)
        assert(serviceHelper.service.api is TwitterApi)
        assert(serviceHelper.queryStringStrategy is DefaultStrategyOAuth1)
    }

    @Test
    fun verifyOAuth20ServiceConfig() {
        val serviceHelper = ServiceHelperFactory(
            serviceConfig = OAuth20ServiceConfig(
                apiKey = API_KEY,
                apiSecret = API_SECRET,
                callback = CALLBACK_URL,
                clazz = GitHubApi::class.java
            )
        ).serviceHelper() as ServiceHelperOAuth20


        assert(serviceHelper.callbackUrl() == CALLBACK_URL)
        assert(serviceHelper.service.api is GitHubApi)
        assert(serviceHelper.queryStringStrategy is DefaultStrategyOAuth2)
    }

    @Test
    fun verifyWrongConfig() {
        val exception = assertThrows(IllegalStateException::class.java) {
            ServiceHelperFactory(
                serviceConfig = object : OAuthServiceConfig {
                    override val apiKey: String
                        get() = error("Not required")
                    override val apiSecret: String
                        get() = error("Not required")
                    override val scope: String?
                        get() = error("Not required")
                    override val callback: String?
                        get() = error("Not required")
                    override val queryStringStrategy: QueryStringStrategy
                        get() = error("Not required")

                    override fun writeToParcel(dest: Parcel?, flags: Int) = error("Not required")
                    override fun describeContents() = error("Not required")
                }
            ).serviceHelper()
        }

        assert(exception.message == ServiceHelperFactory.errorMessageConfigType)
    }

    companion object {
        private const val CALLBACK_URL = "CALLBACK_URL"
        private const val API_KEY = "API_KEY"
        private const val API_SECRET = "API_SECRET"
    }
}
