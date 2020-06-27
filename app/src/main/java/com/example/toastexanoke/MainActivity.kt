package com.example.toastexanoke

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.toastexanoke.databinding.ActivityMainBinding
import com.pluu.alert.PluuMeesageAlert
import com.pluu.alert.PluuMeesageAlert.GravityType

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSlideInOfTop.setOnClickListener {
            if (binding.checkOfActivity.isChecked) {
                PluuMeesageAlert.make(binding.root, "Top Message")
                    .setGravity(GravityType.TOP)
                    .setBackgroundColor(0xFFFFC107.toInt())
                    .show()
            } else {
                PluuMeesageAlert.make(this, "Top Message")
                    .setGravity(GravityType.TOP)
                    .setBackgroundColor(0xFFFFC107.toInt())
                    .show()
            }
        }
        binding.btnSlideInOfBottom.setOnClickListener {
            if (binding.checkOfActivity.isChecked) {
                PluuMeesageAlert.make(binding.root, "공부하세요.")
                    .setGravity(GravityType.BOTTOM)
                    .setBackgroundColor(0xFF03DAC5.toInt())
                    .show()
            } else {
                PluuMeesageAlert.make(this, "공부하세요.")
                    .setGravity(GravityType.BOTTOM)
                    .setBackgroundColor(0xFF03DAC5.toInt())
                    .show()
            }
        }
    }
}