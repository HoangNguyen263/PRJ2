package com.example.it1110app.Activities

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.it1110app.DbQuery
import com.example.it1110app.MyCompleteListener
import com.example.it1110app.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MyProfileActivity : AppCompatActivity() {
    private lateinit var name: EditText
    private lateinit var phone: EditText
    private lateinit var email: EditText
    private lateinit var editB: LinearLayout
    private lateinit var saveB: Button
    private lateinit var cancelB: Button
    private lateinit var profileText: TextView
    private lateinit var buttonLayout: LinearLayout
    private var nameStr : String = ""
    private var phoneStr : String = ""
    private lateinit var progressDialog: Dialog
    private lateinit var dialogText: TextView
    private lateinit var changePasswordButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        name = findViewById(R.id.my_profile_name)
        phone = findViewById(R.id.my_profile_phone)
        email = findViewById(R.id.my_profile_email)
        profileText = findViewById(R.id.profile_text)

        editB = findViewById(R.id.editB)
        cancelB = findViewById(R.id.cancelB)
        saveB = findViewById(R.id.saveB)
        buttonLayout = findViewById(R.id.button_layout)

        var toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Profile"

        progressDialog = Dialog(this@MyProfileActivity)
        progressDialog.setContentView(R.layout.dialog_layout)
        progressDialog.setCancelable(false) // Prevent dialog from closing when touched outside
        progressDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialogText = progressDialog.findViewById(R.id.dialog_text)
        dialogText.text = "Uploading Data..."

        //disable the edit text fields until the user clicks the edit button
        disableEdit()

        editB.setOnClickListener {
            enableEdit()
        }

        cancelB.setOnClickListener {
            disableEdit()
        }

        saveB.setOnClickListener {
            //save the user's profile details
            if (validate()){
                saveData()
            }
        }

        changePasswordButton = findViewById(R.id.change_password_button)
        auth = FirebaseAuth.getInstance()

        val user: FirebaseUser? = auth.currentUser
        if (user != null) {
            for (profile in user.providerData) {
                if (profile.providerId == EmailAuthProvider.PROVIDER_ID) {
                    changePasswordButton.visibility = View.VISIBLE
                } else {
                    changePasswordButton.visibility = View.GONE
                }
            }
        }

        changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }

    }

    private fun disableEdit() {
        name.isEnabled = false
        phone.isEnabled = false
        email.isEnabled = false

        //display cancel and save buttons only when the user clicks the edit button
        buttonLayout.visibility = LinearLayout.GONE

        name.text = Editable.Factory.getInstance().newEditable(DbQuery.myProfile.name)
        email.text = Editable.Factory.getInstance().newEditable(DbQuery.myProfile.email)

        if (DbQuery.myProfile.phone != null) {
            phone.text = Editable.Factory.getInstance().newEditable(DbQuery.myProfile.phone)
        }

        //display the first letter of the user's name in the profile text
        profileText.text = DbQuery.myProfile.name.uppercase().substring(0, 1)
    }

    private fun enableEdit(){
        name.isEnabled = true
        phone.isEnabled = true

        //display cancel and save buttons only when the user clicks the edit button
        buttonLayout.visibility = LinearLayout.VISIBLE

    }

    private fun validate(): Boolean {
        nameStr = name.text.toString()
        phoneStr = phone.text.toString()

        if (nameStr.isNullOrEmpty()) {
            name.error = "Name is required"
            return false
        }

        if (phoneStr.isNullOrEmpty() || phoneStr.length != 10 || !phoneStr.all { it.isDigit() }) {
            phone.error = "Phone must be 10 digits and contain only numbers"
            return false
        }

        return true
    }

    private fun saveData() {
        //save the user's profile details
        progressDialog.show()
        if (nameStr.isEmpty()){
            phoneStr = ""
        }
        DbQuery().saveProfileData(nameStr, phoneStr, object : MyCompleteListener {
            override fun onSuccess() {
                Toast.makeText(this@MyProfileActivity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                disableEdit()
                progressDialog.dismiss()
            }

            override fun onFailure() {
                Toast.makeText(this@MyProfileActivity, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        })
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val currentPassword = dialogView.findViewById<EditText>(R.id.current_password)
        val newPassword = dialogView.findViewById<EditText>(R.id.new_password)
        val confirmNewPassword = dialogView.findViewById<EditText>(R.id.confirm_new_password)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Change Password")
            .setPositiveButton("Confirm") { _, _ ->
                val currentPasswordStr = currentPassword.text.toString()
                val newPasswordStr = newPassword.text.toString()
                val confirmNewPasswordStr = confirmNewPassword.text.toString()

                if (newPasswordStr == confirmNewPasswordStr) {
                    changePassword(currentPasswordStr, newPasswordStr)
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun changePassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser
        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Password change failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}