package com.example.it1110app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class WelcomeActivity : AppCompatActivity() {
    lateinit var textView: TextView
    lateinit var imageView: ImageView
    lateinit var mAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_welcome)
        textView = findViewById(R.id.textView)
        imageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.soict)
        val alphaAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.spash_anim)
        textView.startAnimation(alphaAnimation)
        imageView.startAnimation(alphaAnimation)
        val handler = Handler(Looper.getMainLooper())

        mAuth = FirebaseAuth.getInstance()

        DbQuery.g_firestore = Firebase.firestore

        handler.postDelayed(object : Runnable{
            override fun run() {

                if (mAuth.currentUser != null) {

                    DbQuery().loadData(object : MyCompleteListener {
                        override fun onSuccess() {
                            //load categories
                            val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        override fun onFailure() {
                            //error
                            Toast.makeText(
                                this@WelcomeActivity,
                                "Error in loading categories.",
                                Toast.LENGTH_SHORT,
                            ).show()

                        }
                    })

                } else {
                    val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                //finish()
            }
        }, 5000)
    }
}