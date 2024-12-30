package com.example.it1110app

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}