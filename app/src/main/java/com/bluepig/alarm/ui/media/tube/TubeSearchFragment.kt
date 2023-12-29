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
import androidx.navigation.fragment.findNavController
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.FragmentTubeSearchBinding
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.preferences.AppPreferences
import com.bluepig.alarm.domain.result.isLoading
import com.bluepig.alarm.ui.media.select.MediaSelectBottomSheetDialogFragment
import com.bluepig.alarm.util.ext.setOnEnterListener
import com.bluepig.alarm.util.ext.setOnLoadMore
import com.bluepig.alarm.util.ext.showErrorToast
import com.bluepig.alarm.util.ext.viewRepeatOnLifeCycle
import com.bluepig.alarm.util.logger.BpLogger
import com.bluepig.alarm.util.viewBinding
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.stateIn
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
    lateinit var appPreferences: AppPreferences

    private val _onlyLinkSearch
        get() = appPreferences.showOnlyYoutubeLink

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    override fun onResume() {
        super.onResume()
        BpLogger.logScreenView(TubeSearchFragment::class.java.simpleName)
    }

    private fun initViews() = with(_binding) {
        rvSearch.adapter = _adapter
        rvSearch.setOnLoadMore {
            _vm.search(etSearch.text.toString(), _onlyLinkSearch)
        }

        etSearch.hint =
            if (_onlyLinkSearch) getString(R.string.hint_tube_url_search) else getString(R.string.hint_tube_search)

        etSearch.setOnEnterListener {
            if (checkOAuthLogin()) {
                BpLogger.logMediaSearch(TubeMedia::class.java.simpleName, it)
                _vm.search(it, _onlyLinkSearch)
            }
        }

        viewRepeatOnLifeCycle(Lifecycle.State.STARTED) {
            _vm.tubeList
                .stateIn(this)
                .collect(::searchListHandle)
        }
    }

    private fun checkOAuthLogin(): Boolean {
        val savedSelectName = _vm.getSavedSelectName()
        return if (savedSelectName.isNullOrBlank()) {
            val intent = credential.newChooseAccountIntent()
            _credentialLauncher.launch(intent)
            false
        } else {
            credential.selectedAccountName = savedSelectName
            true
        }
    }

    private fun credentialCallBack(result: ActivityResult) {
        val accountName = result.data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME) ?: return
        credential.selectedAccountName = accountName
        _vm.setSavedSelectName(accountName)
        _vm.search(_binding.etSearch.text.toString(), _onlyLinkSearch)
    }

    private fun searchListHandle(result: Result<List<TubeMedia>>) {
        result.onSuccess { list ->
            setLoadingVisible(false)
            _adapter.submitList(list)
            _binding.tvSearchEmpty.isVisible = list.isEmpty()
            _binding.rvSearch.isVisible = list.isNotEmpty()
        }.onFailure {
            if (it.isLoading) {
                setLoadingVisible(true)
            } else {
                setLoadingVisible(false)
                requestOauth(it)
            }
        }
    }

    private fun onItemClick(tubeMedia: TubeMedia) {
        MediaSelectBottomSheetDialogFragment.openWithMedia(findNavController(), tubeMedia)
    }

    private fun requestOauth(throwable: Throwable) {
        if (throwable is UserRecoverableAuthIOException) {
            val requestIntent = throwable.intent
            _credentialLauncher.launch(requestIntent)
        } else {
            showErrorToast(throwable)
        }
    }

    private fun setLoadingVisible(isVisible: Boolean) {
        _binding.pbLoading.isVisible = isVisible
    }
}