package cookpad.com.sample

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.scribejava.apis.GitHubApi
import com.github.scribejava.apis.TwitterApi
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth2AccessToken
import cookpad.com.socialconnect.ConnectResult
import cookpad.com.socialconnect.ConnectResultError
import cookpad.com.socialconnect.ConnectResultOk
import cookpad.com.socialconnect.OAuth10ServiceConfig
import cookpad.com.socialconnect.OAuth20ServiceConfig
import kotlinx.android.synthetic.main.connections_fragment.bt_github
import kotlinx.android.synthetic.main.connections_fragment.bt_twitter

class ConnectionsFragment : Fragment(R.layout.connections_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bt_twitter.setOnClickListener {
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
        }

        bt_github.setOnClickListener {
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
        }

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
    }

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

    companion object {
        const val RESULT_CONNECTION_TWITTER = "result_connection_twitter"
        const val RESULT_CONNECTION_GITHUB = "result_connection_github"
    }
}
