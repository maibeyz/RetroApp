package com.mai.retroapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mai.retroapp.databinding.ActivitySessionSettingsBinding

class SessionSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_session_settings)

        binding = ActivitySessionSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSaveSettings.setOnClickListener {
            val timer = binding.editTextTimer.text.toString().toIntOrNull() ?: 3
            val isAnonymous = binding.checkBoxAnonymous.isChecked
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("TIMER", timer)
                putExtra("IS_ANONYMOUS", isAnonymous)
            }
            startActivity(intent)
            finish()
        }
    }
}

