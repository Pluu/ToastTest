package com.pluu.windowinset

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pluu.alert.PluuMeesageAlert
import com.pluu.alert.PluuMeesageAlert.GravityType
import com.pluu.windowinset.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFullscreen(true)
        binding.checkFullScreen.setOnClickListener {
            setFullscreen(binding.checkFullScreen.isChecked)
        }

        binding.btnSlideInOfTop.setOnClickListener {
            val msg = "이것은 상단"
            if (binding.checkOfActivity.isChecked) {
                PluuMeesageAlert.make(this, msg)
                    .setParent(binding.root)
                    .setGravity(GravityType.TOP)
                    .setBackgroundColor(0xFFFFC107.toInt())
                    .show()
            } else {
                PluuMeesageAlert.make(this, msg)
                    .setGravity(GravityType.TOP)
                    .setBackgroundColor(0xFFFFC107.toInt())
                    .show()
            }
        }
        binding.btnSlideInOfBottom.setOnClickListener {
            val msg = "이것은 하단"
            if (binding.checkOfActivity.isChecked) {
                PluuMeesageAlert.make(this, msg)
                    .setParent(binding.root)
                    .setGravity(GravityType.BOTTOM)
                    .setBackgroundColor(0xFF03DAC5.toInt())
                    .show()
            } else {
                PluuMeesageAlert.make(this, msg)
                    .setGravity(GravityType.BOTTOM)
                    .setBackgroundColor(0xFF03DAC5.toInt())
                    .show()
            }
        }
    }

    private fun setFullscreen(isFullscreen: Boolean) {
        window.decorView.systemUiVisibility = if (isFullscreen) {
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        } else {
            0
        }
    }
}