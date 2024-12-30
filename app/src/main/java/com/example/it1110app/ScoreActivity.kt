package com.example.it1110app

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.it1110app.Models.QuestionModel
import java.util.concurrent.TimeUnit

class ScoreActivity : AppCompatActivity() {
    private lateinit var scoreTV : TextView
    private lateinit var timeTV : TextView
    private lateinit var totalQTV: TextView
    private lateinit var correctQTV: TextView
    private lateinit var wrongQTV: TextView
    private lateinit var unattemptedQTV: TextView
    private lateinit var leaderB: Button
    private lateinit var reAttemptB: Button
    private lateinit var viewAnsB: Button
    private var timeTaken : Long = 0
    private lateinit var progressDialog: Dialog
    private lateinit var dialogText: TextView
    private var finalScore : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        var toolbar:Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Result"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        progressDialog = Dialog(this@ScoreActivity)
        progressDialog.setContentView(R.layout.dialog_layout)
        progressDialog.setCancelable(false) //not cancel dialog when touch outside
        progressDialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogText = progressDialog.findViewById(R.id.dialog_text)
        dialogText.text = "Loading..."
        progressDialog.show()

        init()

        loadData()

        setBookMark()

        viewAnsB.setOnClickListener {
            var intent: Intent = Intent(this@ScoreActivity,AnswerActivity::class.java)
            startActivity(intent)
        }

        reAttemptB.setOnClickListener {
            reAttempt()
        }

        //save result to db
        saveResult()
    }

    private fun init(){
        scoreTV = findViewById(R.id.score)
        timeTV = findViewById(R.id.time)
        totalQTV = findViewById(R.id.totalQ)
        correctQTV = findViewById(R.id.correctQ)
        wrongQTV = findViewById(R.id.wrongQ)
        unattemptedQTV = findViewById(R.id.un_attemptedQ)
        leaderB = findViewById(R.id.leaderboardB)
        reAttemptB = findViewById(R.id.reattemptB)
        viewAnsB = findViewById(R.id.view_answerB)
    }

    private fun loadData(){
        var correctQ = 0
        var wrongQ = 0
        var unattemptedQ = 0

        for (i in 0..DbQuery.g_questionList.size - 1){
            if (DbQuery.g_questionList[i].selectedAns == -1){
                unattemptedQ++
            }else {
                if (DbQuery.g_questionList[i].selectedAns == DbQuery.g_questionList[i].correctAns){
                    correctQ++
                }else{
                    wrongQ++
                }
            }
        }

        correctQTV.setText(correctQ.toString())
        wrongQTV.setText(wrongQ.toString())
        unattemptedQTV.setText(unattemptedQ.toString())

        totalQTV.setText(DbQuery.g_questionList.size.toString())

        finalScore = (correctQ * 100)/DbQuery.g_questionList.size
        scoreTV.setText(finalScore.toString())

        timeTaken = intent.getLongExtra("TIME_TAKEN",0)
        var time : String = String.format("%02d:%02d min",
            TimeUnit.MILLISECONDS.toMinutes(timeTaken),
            TimeUnit.MILLISECONDS.toSeconds(timeTaken) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeTaken))
        )
        timeTV.setText(time)
    }

    private fun setBookMark() {
        for (i in 0..DbQuery.g_questionList.size - 1) {
            var question : QuestionModel = DbQuery.g_questionList[i]
            if (question.isBookmarked) {
                if (!DbQuery.g_bookmarkIdList.contains(question.qId)) {
                    DbQuery.g_bookmarkIdList.add(question.qId!!)
                    DbQuery.myProfile.bookmarksCount = DbQuery.g_bookmarkIdList.size
                }
            } else {
                if (DbQuery.g_bookmarkIdList.contains(question.qId)) {
                    DbQuery.g_bookmarkIdList.remove(question.qId!!)
                    DbQuery.myProfile.bookmarksCount = DbQuery.g_bookmarkIdList.size
                }
            }

        }
    }

    private fun reAttempt(){
        for (i in 0..DbQuery.g_questionList.size - 1){
            DbQuery.g_questionList[i].selectedAns = -1 //set it to default value
            DbQuery.g_questionList[i].status = DbQuery.NOT_VISITED //reset status
        }

        var intent: Intent = Intent(this@ScoreActivity,StartTestActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveResult(){
        //function to DbQuery to save result to database
        DbQuery().saveResult(finalScore, object : MyCompleteListener {
            override fun onSuccess() {
                progressDialog.dismiss()
            }

            override fun onFailure() {

                Toast.makeText(this@ScoreActivity, "Failed to save result", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        })
    }

    // Handle back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}