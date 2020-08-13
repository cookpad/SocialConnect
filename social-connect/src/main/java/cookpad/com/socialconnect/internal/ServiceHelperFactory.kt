package cookpad.com.socialconnect.internal

import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.builder.api.DefaultApi10a
import com.github.scribejava.core.builder.api.DefaultApi20
import cookpad.com.socialconnect.OAuth10ServiceConfig
import cookpad.com.socialconnect.OAuth20ServiceConfig
import cookpad.com.socialconnect.OAuthServiceConfig

internal class ServiceHelperFactory(private val serviceConfig: OAuthServiceConfig) {
    fun serviceHelper(): ServiceHelper {
        return when (serviceConfig) {
            is OAuth10ServiceConfig -> {
                val api = invokeInstanceMethod(serviceConfig.clazz) as DefaultApi10a
                ServiceHelperOAuth10(
                    service = ServiceBuilder(serviceConfig.apiKey)
                        .apiSecret(serviceConfig.apiSecret)
                        .callback(serviceConfig.callback)
                        .run {
                            if (serviceConfig.scope?.isNotBlank() == true) withScope(serviceConfig.scope)
                            else this
                        }
                        .build(api),
                    queryStringStrategy = serviceConfig.queryStringStrategy
                )
            }
            is OAuth20ServiceConfig -> {
                val api = invokeInstanceMethod(serviceConfig.clazz) as DefaultApi20
                ServiceHelperOAuth20(
                    service = ServiceBuilder(serviceConfig.apiKey)
                        .apiSecret(serviceConfig.apiSecret)
                        .callback(serviceConfig.callback)
                        .run {
                            if (serviceConfig.scope?.isNotBlank() == true) defaultScope(serviceConfig.scope)
                            else this
                        }
                        .build(api),
                    queryStringStrategy = serviceConfig.queryStringStrategy
                )
            }
            else -> throw IllegalStateException(errorMessageConfigType)
        }
    }

    // This is brittle as it makes the assumption that all scribe API providers expose an static method called instance,
    // but the alternative would be mapping each one of those providers to parcelable types
    // which compromises the maintainability of this project.
    private fun invokeInstanceMethod(clazz: Class<*>) = clazz.getMethod("instance").invoke(null)

    companion object {
        internal const val errorMessageConfigType = "Provide an instance either of OAuth10ServiceConfig" +
                " or OAuth20ServiceConfig"
    }
}
