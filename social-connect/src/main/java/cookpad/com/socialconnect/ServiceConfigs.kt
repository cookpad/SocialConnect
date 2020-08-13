package cookpad.com.socialconnect

import android.os.Parcelable
import com.github.scribejava.core.builder.api.DefaultApi10a
import com.github.scribejava.core.builder.api.DefaultApi20
import kotlinx.android.parcel.Parcelize

interface OAuthServiceConfig : Parcelable {
    val apiKey: String
    val apiSecret: String
    val scope: String?
    val callback: String?
    val queryStringStrategy: QueryStringStrategy
}

/**
 * Holds all the information required to build a DefaultApi20 instance
 */
@Parcelize
data class OAuth20ServiceConfig(
    override val apiKey: String,
    override val apiSecret: String,
    override val scope: String? = null,
    override val callback: String? = null,
    override val queryStringStrategy: QueryStringStrategy = DefaultStrategyOAuth2(),
    val clazz: Class<out DefaultApi20>
) : OAuthServiceConfig

/**
 * Holds all the information required to build a DefaultApi10a instance
 */
@Parcelize
data class OAuth10ServiceConfig(
    override val apiKey: String,
    override val apiSecret: String,
    override val scope: String? = null,
    override val callback: String? = null,
    override val queryStringStrategy: QueryStringStrategy = DefaultStrategyOAuth1(),
    val clazz: Class<out DefaultApi10a>
) : OAuthServiceConfig
