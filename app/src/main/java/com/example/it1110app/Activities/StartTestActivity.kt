package com.example.it1110app.Activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.it1110app.DbQuery
import com.example.it1110app.MyCompleteListener
import com.example.it1110app.R

class StartTestActivity : AppCompatActivity() {
    private lateinit var catName : TextView
    private lateinit var testNo : TextView
    private lateinit var totalQ : TextView
    private lateinit var bestScore: TextView
    private lateinit var time: TextView
    private lateinit var startTestB : Button
    private lateinit var backB : ImageView
    private lateinit var progressDialog: Dialog
    private lateinit var dialogText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_test)

        init()

        progressDialog = Dialog(this@StartTestActivity)
        progressDialog.setContentView(R.layout.dialog_layout)
        progressDialog.setCancelable(false) //not cancel dialog when touch outside
        progressDialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialogText = progressDialog.findViewById(R.id.dialog_text)
        dialogText.text = "Loading..."

        progressDialog.show()

        //load the question, display the total number of questions in a particular test
        DbQuery().loadQuestions(object : MyCompleteListener {
            override fun onSuccess() {
                //load the question
                setData()

                progressDialog.dismiss()
            }

            override fun onFailure() {
                //error
                progressDialog.dismiss()
                Toast.makeText(this@StartTestActivity, "Something went wrong! Please try again", Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun init(){
        //initialize variables
        catName = findViewById(R.id.st_cat_name)
        testNo = findViewById(R.id.st_test_no)
        totalQ = findViewById(R.id.st_total_ques)
        bestScore = findViewById(R.id.st_best_score)
        time = findViewById(R.id.st_time)
        startTestB = findViewById(R.id.start_testB)
        backB = findViewById(R.id.st_backB)

        backB.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }
        })

        startTestB.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                //start test
                var intent : Intent = Intent(this@StartTestActivity, QuestionActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    private fun setData(){
        catName.text = DbQuery.g_catList[DbQuery.g_selected_cat_index].name
        testNo.text = "Test No: ${DbQuery.g_selected_test_index + 1}"
        totalQ.text = "${DbQuery.g_questionList.size}"
        bestScore.text = "${DbQuery.g_testList[DbQuery.g_selected_test_index].topScore}"
        time.text = "${DbQuery.g_testList[DbQuery.g_selected_test_index].time} min"
    }
}