package com.mai.retroapp.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mai.retroapp.R

class JoinAnonymus : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var buttonJoin: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_join_anonymus)

        editTextName = findViewById(R.id.editTextName)
        buttonJoin = findViewById(R.id.buttonJoin)

        buttonJoin.setOnClickListener {
            val name = editTextName.text.toString()
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("USERNAME", name)
                    putExtra("IS_ANONYMOUS", true)
                }
                startActivity(intent)
            }
        }
    }
}

