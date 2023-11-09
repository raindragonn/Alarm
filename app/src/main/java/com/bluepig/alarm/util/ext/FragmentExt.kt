package com.bluepig.alarm.util.ext

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope

val Fragment.viewLifeCycleScope: CoroutineScope
    get() = viewLifecycleOwner.lifecycleScope