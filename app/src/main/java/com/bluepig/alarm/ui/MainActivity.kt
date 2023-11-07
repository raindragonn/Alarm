package com.bluepig.alarm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bluepig.alarm.databinding.ActivityMainBinding
import com.bluepig.alarm.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val _binding: ActivityMainBinding by viewBinding(ActivityMainBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
    }
}