# SocialConnect
SocialConnect simplifies the process of retrieving OAuth tokens from multiple social networks on Android by leveraging the [Navigation component](https://developer.android.com/guide/navigation/navigation-getting-started). This library has as many providers as [ScribeJava](https://github.com/scribejava/scribejava) has, as SocialConnect is built on top of it.

## Setup
Add the JitPack repository in your build.gradle (top level module):
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

Apply the SafeArgs plugin ([see how](https://developer.android.com/guide/navigation/navigation-pass-data)) and add next dependency in the build.gradle of your app module:
```gradle
dependencies {
    compile 'com.github.cookpad:SocialConnect:0.0.3'
}
```

## Usage
`ConnectFragment` is the entry point of SocialConnect library. To use it, add the `connect_graph` as a destination to your nav graph file, as follow:

```xml
<fragment
    android:id="@+id/yourFrgment"
    android:name=".YourFragment"
    tools:layout="@layout/your_fragment">
    <action
        android:id="@+id/action_yourFragment_to_connectFragment"
        app:destination="@id/connect_graph">
        <argument
            android:name="serviceConfig"
            app:argType="cookpad.com.socialconnect.OAuthServiceConfig" />
        <argument
            android:name="keyRequestCode"
            app:argType="string" />
    </action>
</fragment>
```

This will generate `YourFragmentDirections.actionConnectionsFragmentToConnectFragment` which expects an `OAuthServiceConfig` instance and provides the `NavDirection` to access `ConnectFragment` from your nav graph.

OAuth1 with Twitter provider:

```kotlin
findNavController().navigate(
    ConnectionsFragmentDirections.actionConnectionsFragmentToConnectFragment(
        keyRequestCode = RESULT_CONNECTION_TWITTER,
        serviceConfig = OAuth10ServiceConfig(
            apiKey = API_KEY_TWITTER,
            apiSecret = API_SECRET,
            callback = CALLBACK_URL,
            clazz = TwitterApi::class.java
        )
    )
)
```

OAuth2 with Github provider:

```kotlin
findNavController().navigate(
    ConnectionsFragmentDirections.actionConnectionsFragmentToConnectFragment(
        keyRequestCode = RESULT_CONNECTION_GITHUB,
        serviceConfig = OAuth20ServiceConfig(
            apiKey = API_KEY_GITHUB,
            apiSecret = API_SECRET_GITHUB,
            callback = CALLBACK_URL,
            clazz = GitHubApi::class.java
        )
    )
)
```

To retrieve the `Token` back, listen for changes on `findNavController().currentBackStackEntry?.savedStateHandle` expecting a type of `ConnectResult`: ([see](https://developer.android.com/guide/navigation/navigation-programmatic) for more info about how retrieve a result to the previous Destination with the navigation Component)

```kotlin
findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<ConnectResult>(RESULT_CONNECTION_TWITTER)
            ?.observe(viewLifecycleOwner, Observer { connectResult ->
                processConnectResult(nameProvider = "Twitter", connectResult = connectResult)
            })

findNavController().currentBackStackEntry?.savedStateHandle
    ?.getLiveData<ConnectResult>(RESULT_CONNECTION_GITHUB)
    ?.observe(viewLifecycleOwner, Observer { connectResult ->
        processConnectResult(nameProvider = "Github", connectResult = connectResult)
    })


private fun processConnectResult(nameProvider: String, connectResult: ConnectResult) {
    when (connectResult) {
        is ConnectResultOk -> {
            val token = connectResult.token
            val message = when (token) {
                is OAuth1AccessToken -> "Provider: $nameProvider, Token: ${token.token} Secret:${token.tokenSecret}"
                is OAuth2AccessToken -> "Provider: $nameProvider, AccessToken: ${token.accessToken}"
                else -> throw IllegalStateException(token::class.java.canonicalName)
            }
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
        is ConnectResultError -> {
            Toast.makeText(requireContext(), connectResult.error.message, Toast.LENGTH_LONG).show()
        }
    }
}
```  

## Sample app
Check the `:app` Gradle module for a showcase of Twitter and Github providers. But prior to build the module, you need to provide the `app/src/main/java/cookpad/com/sample/Credentials.kt` file with the credentials of both your [Twitter](https://developer.twitter.com/en/apps) and your [Github](https://docs.github.com/en/developers/apps/creating-a-github-app) app:

```kotlin
const val API_KEY_TWITTER = ""
const val API_SECRET = ""

const val API_KEY_GITHUB = ""
const val API_SECRET_GITHUB = ""

const val CALLBACK_URL = ""
``` 

## Proguard
Proguard is already handled via the `consumer-rules.pro` config file.


## Credits
Oauth core authentication: [ScribeJava](https://github.com/scribejava/scribejava)
