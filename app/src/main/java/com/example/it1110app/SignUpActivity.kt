package com.example.it1110app

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var signupB: Button
    private lateinit var backB: ImageView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var emailString: String
    private lateinit var passwordString: String
    private lateinit var confirmPasswordString: String
    private lateinit var nameString: String
    private lateinit var progressDialog : Dialog
    private lateinit var dialogText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        name = findViewById(R.id.username)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.cfpassword)
        signupB = findViewById(R.id.signUpButton)
        backB = findViewById(R.id.backButton)


        progressDialog = Dialog(this@SignUpActivity)
        progressDialog.setContentView(R.layout.dialog_layout)
        progressDialog.setCancelable(false)
        progressDialog.getWindow()?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialogText = progressDialog.findViewById(R.id.dialog_text)
        dialogText.text = "Signing up..."

        mAuth = FirebaseAuth.getInstance()

        backB.setOnClickListener {
            finish();
        }

        signupB.setOnClickListener {
            if (validation()) {
                signUpNewUser()
            }

        }


    }

    private fun validation(): Boolean {
        nameString = name.text.toString().trim() //delete space in the start and end
        passwordString = password.text.toString().trim()
        emailString = email.text.toString().trim()
        confirmPasswordString = confirmPassword.text.toString().trim()

        if (nameString.isEmpty()) {
            name.error = "Name is required"
            return false
        }

        if (emailString.isEmpty()) {
            email.error = "Email is required"
            return false
        }

        if (passwordString.isEmpty()) {
            password.error = "Password is required"
            return false
        }

        if (confirmPasswordString.isEmpty()) {
            confirmPassword.error = "Confirm Password is required"
            return false
        }

        if (passwordString != confirmPasswordString) {
            Toast.makeText(
                this@SignUpActivity,
                "Password and Confirm Password must be the same.",
                Toast.LENGTH_SHORT,
            ).show()
            return false
        }

        return true
    }

    private fun signUpNewUser() {
        //show progress dialog
        progressDialog.show()
        mAuth.createUserWithEmailAndPassword(emailString, passwordString)
            .addOnCompleteListener(this@SignUpActivity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        this@SignUpActivity,
                        "SIGNUP successful.",
                        Toast.LENGTH_SHORT,
                    ).show()

                    //before loading main activity, we need to create user data in firebase
                    DbQuery().createUserData(emailString, nameString,object : MyCompleteListener {
                        override fun onSuccess(){

                            DbQuery().loadData(object : MyCompleteListener {
                                override fun onSuccess(){
                                    progressDialog.dismiss()
                                    var intent : Intent = Intent(this@SignUpActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }

                                override fun onFailure(){
                                    Toast.makeText(
                                        this@SignUpActivity,
                                        "Error in loading categories.",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                    progressDialog.dismiss()
                                }
                            })

                        }

                        override fun onFailure(){
                            Toast.makeText(
                                this@SignUpActivity,
                                "Error in creating user data.",
                                Toast.LENGTH_SHORT,
                            ).show()
                            progressDialog.dismiss()
                        }
                    })


                } else {
                    // If sign in fails, display a message to the user.
                    progressDialog.dismiss()
                    Toast.makeText(
                       this@SignUpActivity,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                }
            }
    }



}