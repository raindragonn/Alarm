package com.bluepig.alarm.ui.media.tube

import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.DialogYoutubeNoticeBinding
import com.bluepig.alarm.databinding.FragmentTubeSearchBinding
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.result.TubeAuthRequiredException
import com.bluepig.alarm.domain.result.isLoading
import com.bluepig.alarm.ui.media.select.MediaSelectBottomSheetDialogFragment
import com.bluepig.alarm.util.ext.setOnEnterListener
import com.bluepig.alarm.util.ext.setOnLoadMore
import com.bluepig.alarm.util.ext.showErrorToast
import com.bluepig.alarm.util.ext.viewLifeCycleScope
import com.bluepig.alarm.util.ext.viewRepeatOnLifeCycle
import com.bluepig.alarm.util.logger.BpLogger
import com.bluepig.alarm.util.viewBinding
import com.google.android.gms.common.AccountPicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TubeSearchFragment : Fragment(R.layout.fragment_tube_search) {
    private val _binding: FragmentTubeSearchBinding by viewBinding(FragmentTubeSearchBinding::bind)
    private val _vm: TubeSearchViewModel by viewModels()
    private val _adapter by lazy { TubeSearchAdapter(::onItemClick) }
    private val _credentialLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(), ::credentialCallBack
    )

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
            _vm.search(etSearch.text.toString())
        }

        viewLifeCycleScope.launch {
            val hint = if (_vm.isOnlyLinkSearch()) {
                R.string.hint_tube_url_search
            } else {
                R.string.hint_tube_search
            }
            etSearch.hint = getString(hint)
        }

        etSearch.setOnEnterListener {
            viewLifeCycleScope.launch {
                if (checkOAuthLogin()) {
                    BpLogger.logMediaSearch(TubeMedia::class.java.simpleName, it)
                    _vm.search(it)
                }
            }
        }

        viewRepeatOnLifeCycle(Lifecycle.State.STARTED) {
            _vm.tubeList
                .stateIn(this)
                .collect(::searchListHandle)
        }
    }

    private suspend fun checkOAuthLogin(): Boolean {
        val savedSelectName = _vm.getSavedSelectName()
        return if (savedSelectName.isNullOrBlank()) {
            val intent = newChooseAccountIntent()
            openOauthNotice {
                _credentialLauncher.launch(intent)
            }
            false
        } else {
            true
        }
    }

    // GoogleAccountCredential.newChooseAccountIntent와 같은 값 (credential은 core:network에 있음)
    private fun newChooseAccountIntent(): Intent = AccountPicker.newChooseAccountIntent(
        AccountPicker.AccountChooserOptions.Builder()
            .setAllowableAccountsTypes(listOf(GOOGLE_ACCOUNT_TYPE))
            .setAlwaysShowAccountPicker(true)
            .build()
    )

    private fun credentialCallBack(result: ActivityResult) {
        val accountName = result.data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME) ?: return
        viewLifeCycleScope.launch {
            _vm.setSavedSelectName(accountName)
            _vm.search(_binding.etSearch.text.toString())
        }
    }

    private fun openOauthNotice(action: () -> Unit) {
        val dialogBinding = DialogYoutubeNoticeBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.shape_rounded_background)

        dialogBinding.apply {
            val appName = getString(R.string.app_name)
            tvDescription.text =
                getString(R.string.alert_request_oauth_description, appName, appName)
            btnAgree.text = getString(R.string.alert_request_oauth_agree, appName)
            btnAgree.setOnClickListener {
                dialog.dismiss()
                action.invoke()
            }
            btnDisagree.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
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
        val requestIntent = (throwable as? TubeAuthRequiredException)?.resolution as? Intent
        if (requestIntent != null) {
            viewLifeCycleScope.launch { _vm.setSavedSelectName("") }
            _credentialLauncher.launch(requestIntent)
        } else {
            showErrorToast(throwable)
        }
    }

    private fun setLoadingVisible(isVisible: Boolean) {
        _binding.pbLoading.isVisible = isVisible
    }

    private companion object {
        const val GOOGLE_ACCOUNT_TYPE = "com.google"
    }
}
