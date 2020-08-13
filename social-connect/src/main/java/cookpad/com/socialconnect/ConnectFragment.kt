package cookpad.com.socialconnect

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.navArgs
import cookpad.com.socialconnect.internal.ConnectViewModel
import cookpad.com.socialconnect.internal.OAuthWebViewClient
import cookpad.com.socialconnect.internal.ServiceHelperFactory
import cookpad.com.socialconnect.internal.ViewState
import cookpad.com.socialconnect.internal.clearCookies
import kotlinx.android.synthetic.main.fragment_connect.webView

/**
 * Entry point of SocialConnect library. To use it, add the `connect_graph` graph as a destination
 * to your nav_graph file.
 * This will generate `ConnectionsFragmentDirections.actionConnectionsFragmentToConnectFragment` which expects an
 * `OAuthServiceConfig` instance and provides the `NavDirection` to access `ConnectFragment` from your nav graph.
 * To retrieve the result back, listen for changes findNavController().currentBackStackEntry?.savedStateHandle
 * expecting a type of `ConnectResult`.
 */
class ConnectFragment : Fragment(R.layout.fragment_connect) {
    private val navArgs by navArgs<ConnectFragmentArgs>()
    private val serviceHelper by lazy {
        ServiceHelperFactory(navArgs.serviceConfig).serviceHelper()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel = ViewModelProvider(this,
            object : ViewModelProvider.NewInstanceFactory() {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return ConnectViewModel(serviceHelper) as T
                }
            }
        ).get(ConnectViewModel::class.java)

        webView.apply {
            clearCookies(requireContext())

            webViewClient = OAuthWebViewClient(
                retrieveToken = { url -> viewModel.retrieveToken(url) },
                callbackUrl = serviceHelper.callbackUrl()
            )
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
        }

        viewModel.viewStates.observe(viewLifecycleOwner, Observer { viewState ->
            when (viewState) {
                is ViewState.LoadUrl -> webView.loadUrl(viewState.authUrl)
                is ViewState.FinishWithToken -> {
                    findNavController(this).apply {
                        previousBackStackEntry?.savedStateHandle?.set(
                            navArgs.keyRequestCode,
                            ConnectResultOk(viewState.token)
                        )
                        popBackStack()
                    }
                }
                is ViewState.FinishWithError -> {
                    findNavController(this).apply {
                        previousBackStackEntry?.savedStateHandle?.set(
                            navArgs.keyRequestCode,
                            ConnectResultError(viewState.error)
                        )
                        popBackStack()
                    }
                }
            }
        })
    }
}
