package com.bluepig.alarm.util.ext

import android.view.LayoutInflater
import android.view.ViewGroup

val ViewGroup.inflater: LayoutInflater
    get() = LayoutInflater.from(context)