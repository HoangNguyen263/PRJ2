package com.example.it1110app

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.it1110app.Adapters.TestAdapter

class TestActivity : AppCompatActivity() {
    private lateinit var testView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var backB: ImageView
    private lateinit var testAdapter: TestAdapter
    private lateinit var progressDialog: Dialog
    private lateinit var dialogText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // Initialize Firestore
        DbQuery.initializeFirestore()

        // Setup UI components
        testView = findViewById(R.id.test_recycler_view)
        toolbar = findViewById(R.id.toolbar)
        backB = findViewById(R.id.backBtt)

        // Back button behavior
        backB.setOnClickListener {
            finish()
        }

        // Set up Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Show loading dialog
        setupProgressDialog()

        // Load categories and set toolbar title
        if (DbQuery.g_catList.isNotEmpty() && DbQuery.g_selected_cat_index < DbQuery.g_catList.size) {
            supportActionBar?.title = DbQuery.g_catList[DbQuery.g_selected_cat_index].name
        } else {
            Toast.makeText(this, "Category data is missing!", Toast.LENGTH_SHORT).show()
            Log.e("TestActivity", "g_catList is empty or selected index is out of bounds.")
            finish()
            return
        }

        // Setup RecyclerView
        setupRecyclerView()

        // Load test data
        loadTestData()
    }

    private fun setupProgressDialog() {
        progressDialog = Dialog(this@TestActivity)
        progressDialog.setContentView(R.layout.dialog_layout)
        progressDialog.setCancelable(false) // Prevent dialog from closing when touched outside
        progressDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialogText = progressDialog.findViewById(R.id.dialog_text)
        dialogText.text = "Loading..."
        progressDialog.show()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this@TestActivity)
        layoutManager.orientation = RecyclerView.VERTICAL
        testView.layoutManager = layoutManager

        // Initialize adapter with empty list
        testAdapter = TestAdapter(mutableListOf())
        testView.adapter = testAdapter
    }

    private fun loadTestData() {
        DbQuery().loadTestData(object : MyCompleteListener {
            override fun onSuccess() {
                DbQuery().loadMyScores(object: MyCompleteListener {
                    override fun onSuccess() {
                        if (DbQuery.g_testList.isNotEmpty()) {
                            testAdapter = TestAdapter(DbQuery.g_testList)
                            testView.adapter = testAdapter
                        } else {
                            Toast.makeText(this@TestActivity, "No test data available!", Toast.LENGTH_SHORT).show()
                        }
                        progressDialog.dismiss()

                    }

                    override fun onFailure() {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this@TestActivity,
                            "Something went wrong! Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

                progressDialog.dismiss()
            }

            override fun onFailure() {
                progressDialog.dismiss()
                Toast.makeText(
                    this@TestActivity,
                    "Something went wrong! Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
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
