package com.example.it1110app.Activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.it1110app.DbQuery
import com.example.it1110app.MainActivity
import com.example.it1110app.MyCompleteListener
import com.example.it1110app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPassword: TextView
    private lateinit var signUp: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: Dialog
    private lateinit var dialogText: TextView
    private lateinit var gSignIn: LinearLayout
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val TAG = "LoginActivity"
    private val RC_SIGN_IN = 104

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextNumberPassword)
        loginButton = findViewById(R.id.loginButton)
        forgotPassword = findViewById(R.id.textView2)
        signUp = findViewById(R.id.textView4)
        auth = FirebaseAuth.getInstance()
        gSignIn = findViewById(R.id.signInGBtn)

        progressDialog = Dialog(this@LoginActivity)
        progressDialog.setContentView(R.layout.dialog_layout)
        progressDialog.setCancelable(false) //not cancel dialog when touch outside
        progressDialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialogText = progressDialog.findViewById(R.id.dialog_text)
        dialogText.text = "Login..."



        var gso : GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        loginButton.setOnClickListener {
            if (validation()) {
                login()
            }
        }

        signUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        gSignIn.setOnClickListener {
            googleSignIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                //Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                //Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this@LoginActivity, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        progressDialog.show()
        var credential : AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                   // Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(this@LoginActivity, "Google Sign In Success.", Toast.LENGTH_SHORT).show()
                    val user : FirebaseUser = auth.currentUser!!

                    //check if it is the first time login or not
                    //if first time, create user data in firebase
                    if (task.getResult().additionalUserInfo!!.isNewUser())
                    {
                        DbQuery().createUserData(user.email.toString(), user.displayName.toString(), object :
                            MyCompleteListener {
                            override fun onSuccess() {

                                //load categories before going to main activity
                                DbQuery().loadData(object : MyCompleteListener {
                                    override fun onSuccess() {
                                        progressDialog.dismiss()
                                        var intent : Intent = Intent(this@LoginActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    override fun onFailure() {
                                        progressDialog.dismiss()
                                        Toast.makeText(this@LoginActivity, "Error in loading categories.", Toast.LENGTH_SHORT).show()
                                    }
                                })

                            }

                            override fun onFailure() {
                                progressDialog.dismiss()
                                Toast.makeText(this@LoginActivity, "Error in creating user data.", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        //if not first time user
                        DbQuery().loadData(object : MyCompleteListener {
                            override fun onSuccess() {
                                progressDialog.dismiss()
                                var intent : Intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                            override fun onFailure() {
                                progressDialog.dismiss()
                                Toast.makeText(this@LoginActivity, "Error in loading categories.", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithCredential:failure", task.exception)
                    progressDialog.dismiss()
                    Toast.makeText(this@LoginActivity, task.exception?.message, Toast.LENGTH_SHORT).show()


                }
            }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

    }

    private fun validation(): Boolean {
        if (email.text.toString().isEmpty()) {
            email.error = "Email is required"
            return false
        }
        if (password.text.toString().isEmpty()) {
            password.error = "Password is required"
            return false
        }
        return true
    }

    private fun googleSignIn() {
        // Google sign-in logic here
        var signInIntent : Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun login() {
        progressDialog.show()

        auth.signInWithEmailAndPassword(email.text.toString().trim(), password.text.toString().trim())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this@LoginActivity, "Login Success.", Toast.LENGTH_SHORT).show()

                    DbQuery().loadData(object : MyCompleteListener {
                        override fun onSuccess() {
                            progressDialog.dismiss()
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        override fun onFailure() {
                            progressDialog.dismiss()
                            Toast.makeText(this@LoginActivity, "Error in loading categories.", Toast.LENGTH_SHORT).show()
                        }
                    })


                } else {
                    // If sign in fails, display a message to the user.
                    progressDialog.dismiss()
                    Toast.makeText(this@LoginActivity, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
}