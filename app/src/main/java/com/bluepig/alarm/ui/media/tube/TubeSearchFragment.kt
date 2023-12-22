package com.bluepig.alarm.ui.media.tube

import android.accounts.AccountManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.FragmentTubeSearchBinding
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.preferences.AppPreferences
import com.bluepig.alarm.domain.result.onFailureWitLoading
import com.bluepig.alarm.domain.result.onLoading
import com.bluepig.alarm.util.ext.setOnEnterListener
import com.bluepig.alarm.util.ext.setOnLoadMore
import com.bluepig.alarm.util.ext.showErrorToast
import com.bluepig.alarm.util.ext.viewRepeatOnLifeCycle
import com.bluepig.alarm.util.viewBinding
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TubeSearchFragment : Fragment(R.layout.fragment_tube_search) {
    private val _binding: FragmentTubeSearchBinding by viewBinding(FragmentTubeSearchBinding::bind)
    private val _vm: TubeSearchViewModel by viewModels()
    private val _adapter by lazy { TubeSearchAdapter(::onItemClick) }
    private val _credentialLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(), ::credentialCallBack
    )

    @Inject
    lateinit var credential: GoogleAccountCredential

    @Inject
    lateinit var appPref: AppPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() = with(_binding) {
        rvSearch.adapter = _adapter


        rvSearch.setOnLoadMore {
            _vm.search(etSearch.text.toString())
        }

        etSearch.setOnEnterListener {
            if (checkOAuthLogin()) {
                _vm.search(it)
            }
        }

        viewRepeatOnLifeCycle(Lifecycle.State.STARTED) {
            _vm.tubeList
                .stateIn(this)
                .collect(::searchListHandle)
        }
    }

    private fun checkOAuthLogin(): Boolean {
        return if (appPref.selectedAccountName.isNullOrBlank()) {
            val intent = credential.newChooseAccountIntent()
            _credentialLauncher.launch(intent)
            false
        } else {
            credential.selectedAccountName = appPref.selectedAccountName
            true
        }
    }

    private fun credentialCallBack(result: ActivityResult) {
        val accountName = result.data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME) ?: return
        credential.selectedAccountName = accountName
        appPref.selectedAccountName = accountName
        _vm.search(_binding.etSearch.text.toString())
    }

    private fun searchListHandle(result: Result<List<TubeMedia>>) {
        result.onSuccess { list ->
            Timber.d(list.size.toString())
            _adapter.submitList(list)
        }.onFailureWitLoading {
            requestOauth(it)
        }.onLoading(::changeLoadingState)
    }

    private fun onItemClick(tubeMedia: TubeMedia) {
        // TODO: open Select Dialog
    }

    private fun requestOauth(throwable: Throwable) {
        if (throwable is UserRecoverableAuthIOException) {
            val requestIntent = throwable.intent
            _credentialLauncher.launch(requestIntent)
        } else {
            showErrorToast(throwable)
        }
    }

    private fun changeLoadingState(isVisible: Boolean) {
        _binding.pbLoading.isVisible = isVisible
    }
}