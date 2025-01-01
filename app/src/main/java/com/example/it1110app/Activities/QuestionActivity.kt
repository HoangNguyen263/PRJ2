package com.example.it1110app.Activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.it1110app.Adapters.QuestionGridAdapter
import com.example.it1110app.Adapters.QuestionsAdapter
import com.example.it1110app.DbQuery
import com.example.it1110app.DbQuery.Companion.NOT_VISITED
import com.example.it1110app.DbQuery.Companion.REVIEW
import com.example.it1110app.DbQuery.Companion.UNANSWERED
import com.example.it1110app.DbQuery.Companion.g_questionList
import com.example.it1110app.R
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class QuestionActivity : AppCompatActivity() {
    private lateinit var questionsView : RecyclerView
    private lateinit var tvQuesId : TextView
    private lateinit var timerTV : TextView
    private lateinit var catNameTV : TextView
    private lateinit var submitB: Button
    private lateinit var markB : Button
    private lateinit var clearSelB: Button
    private lateinit var prevQuesB: ImageButton
    private lateinit var nextQuesB: ImageButton
    private lateinit var quesListB: ImageView
    private var quesId by Delegates.notNull<Int>()
    lateinit var quesAdapter : QuestionsAdapter
    private lateinit var drawer: DrawerLayout
    private lateinit var drawerCloseB : ImageButton
    private lateinit var quesListGV : GridView //number of questions that we have and the status of each
    private lateinit var markImage : ImageView
    private lateinit var quesGridAdapter : QuestionGridAdapter
    private lateinit var timer : CountDownTimer
    private var timeLeft: Long? = null
    private lateinit var bookmarkB : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.questions_list_layout)

        init()

        //adapter to render question
        quesAdapter = QuestionsAdapter(g_questionList)
        questionsView.adapter = quesAdapter

        var layoutManager : LinearLayoutManager = LinearLayoutManager(this@QuestionActivity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        questionsView.layoutManager = layoutManager

        //set the number of questions in the grid
        quesGridAdapter = QuestionGridAdapter(this@QuestionActivity, g_questionList.size)
        quesListGV.adapter = quesGridAdapter

        //use snap helper to snap to the nearest question
        setSnapHelper()

        //use button to navigate question
        setClickListeners()

        //timer
        startTimer()
    }

    private fun init(){
        questionsView = findViewById(R.id.questions_view)
        tvQuesId = findViewById(R.id.tv_quesId)
        timerTV = findViewById(R.id.tv_time)
        catNameTV = findViewById(R.id.qa_catName)
        submitB = findViewById(R.id.submitB)
        markB = findViewById(R.id.markB)
        clearSelB = findViewById(R.id.clear_selB)
        prevQuesB = findViewById(R.id.prev_quesB)
        nextQuesB = findViewById(R.id.next_quesB)
        quesListB = findViewById(R.id.ques_list_gridB)
        drawer = findViewById(R.id.drawer_layout)
        drawerCloseB = findViewById(R.id.drawerCloseB)
        quesId = 0
        markImage = findViewById(R.id.mark_image)

        tvQuesId.text = "1/${DbQuery.g_questionList.size}"
        catNameTV.text = DbQuery.g_catList[DbQuery.g_selected_cat_index].name

        quesListGV = findViewById(R.id.ques_list_gv)

        g_questionList[0].status = UNANSWERED

        bookmarkB= findViewById(R.id.qa_bookmark)

        if (g_questionList.get(0).isBookmarked){
            bookmarkB.setImageResource(R.drawable.ic_bookmarked)
        } else {
            bookmarkB.setImageResource(R.drawable.ic_bookmark)
        }
    }

    /*
    * ensure that the RecyclerView snaps to the nearest item when scrolling.
    * This is particularly useful for a horizontal list of questions,
    * as it provides a smooth and user-friendly navigation experience.
    * */
    private fun setSnapHelper(){
        var snapHelper : SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(questionsView)

        //create a callback
        questionsView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            //called when the scroll state changes (drag left or right)
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                var view : View? = snapHelper.findSnapView(recyclerView.layoutManager)
                quesId = recyclerView.layoutManager?.getPosition(view!!)!!

                if (DbQuery.g_questionList[quesId].status == NOT_VISITED){
                    DbQuery.g_questionList[quesId].status = DbQuery.UNANSWERED
                }

                if (g_questionList[quesId].status == REVIEW){
                    markImage.visibility = View.VISIBLE
                }
                else {
                    markImage.visibility = View.GONE
                }

                tvQuesId.text = "${quesId+1}/${DbQuery.g_questionList.size}"

                if (g_questionList.get(quesId).isBookmarked){
                    bookmarkB.setImageResource(R.drawable.ic_bookmarked)
                } else {
                    bookmarkB.setImageResource(R.drawable.ic_bookmark)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    private fun setClickListeners(){
        prevQuesB.setOnClickListener {
            if(quesId > 0){
                questionsView.smoothScrollToPosition(quesId-1)
            }
        }

        nextQuesB.setOnClickListener {
            if(quesId < DbQuery.g_questionList.size-1){
                questionsView.smoothScrollToPosition(quesId+1)
            }
        }

        clearSelB.setOnClickListener{
            DbQuery.g_questionList[quesId].selectedAns = -1
            g_questionList[quesId].status = UNANSWERED
            markImage.visibility = View.GONE
            quesAdapter.notifyDataSetChanged()
        }

        quesListB.setOnClickListener(View.OnClickListener {
            if (!drawer.isDrawerOpen(GravityCompat.END)) {
                quesGridAdapter.notifyDataSetChanged()
                drawer.openDrawer(GravityCompat.END)
            } //open question list
        })

        drawerCloseB.setOnClickListener {
            if(drawer.isDrawerOpen(GravityCompat.END)){
                drawer.closeDrawer(GravityCompat.END)
            }
        }

        markB.setOnClickListener{
            if(markImage.visibility != View.VISIBLE){
                markImage.visibility = View.VISIBLE
                DbQuery.g_questionList[quesId].status = DbQuery.REVIEW
            }
            else {
                markImage.visibility = View.GONE
                if (DbQuery.g_questionList[quesId].selectedAns != -1){
                    DbQuery.g_questionList[quesId].status = DbQuery.ANSWERED
                }
                else {
                    DbQuery.g_questionList[quesId].status = DbQuery.UNANSWERED
                }
            }
        }

        submitB.setOnClickListener {
            submitTest()
        }

        bookmarkB.setOnClickListener {
            addToBookMark()
        }

    }

    private fun addToBookMark(){
        if (g_questionList.get(quesId).isBookmarked){
            g_questionList.get(quesId).isBookmarked = false
            bookmarkB.setImageResource(R.drawable.ic_bookmark)
        }else {
            g_questionList.get(quesId).isBookmarked = true
            bookmarkB.setImageResource(R.drawable.ic_bookmarked)
        }
    }

    private fun submitTest(){
        var builder:AlertDialog.Builder = AlertDialog.Builder(this@QuestionActivity)
        builder.setCancelable(true)

        var view : View = layoutInflater.inflate(R.layout.alert_dialog_layout,null)
        val cancel:Button = view.findViewById(R.id.cancelB)
        val ok:Button = view.findViewById(R.id.confirmB)
        builder.setView(view)

        var alertDialog:AlertDialog = builder.create()
        cancel.setOnClickListener {
            alertDialog.dismiss()
        }
        ok.setOnClickListener {
            timer.cancel()
            alertDialog.dismiss()

            var intent:Intent= Intent(this@QuestionActivity, ScoreActivity::class.java)
            //total time taken in milliseconds
            var totalTime : Long = (DbQuery.g_testList.get(DbQuery.g_selected_test_index).time * 60 * 1000).toLong()
            val timeTaken = totalTime - (timeLeft ?: 0)
            Log.d("QuestionActivity", "totalTime: $totalTime")
            Log.d("QuestionActivity", "timeLeft: $timeLeft")
            Log.d("QuestionActivity", "timeTaken: $timeTaken")
            //Log.d("QuestionActivity", "totalTime - timeLeft: ${totalTime - timeLeft!!}")
            intent.putExtra("TIME_TAKEN",totalTime - timeLeft!!)
            startActivity(intent)
            finish()
        }

        alertDialog.show()
    }

    public fun goToQuestion(position:Int){
        questionsView.smoothScrollToPosition(position)
        if (drawer.isDrawerOpen(GravityCompat.END)){
            drawer.closeDrawer(GravityCompat.END)
        }
    }

    private fun startTimer(){
        var totalTime : Int = DbQuery.g_testList.get(DbQuery.g_selected_test_index).time * 60 * 1000 //convert to milliseconds

        //start timer
        timer = object : CountDownTimer(totalTime.toLong(), 1000){
            override fun onTick(remainingTime: Long) {
                timeLeft = remainingTime
                var time : String = String.format("%02d:%02d min",
                    TimeUnit.MILLISECONDS.toMinutes(remainingTime),
                    TimeUnit.MILLISECONDS.toSeconds(remainingTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTime))
                )

                timerTV.text = time
            }

            override fun onFinish() {
                var intent:Intent= Intent(this@QuestionActivity, ScoreActivity::class.java)
                //total time taken in milliseconds
                var totalTime : Long = (DbQuery.g_testList.get(DbQuery.g_selected_test_index).time * 60 * 1000).toLong()
                Log.d("QuestionActivity", "timeLeft: $timeLeft")
                Log.d("QuestionActivity", "totalTime - timeLeft: ${totalTime - timeLeft!!}")
                intent.putExtra("TIME_TAKEN",totalTime - timeLeft!!)
                startActivity(intent)
                finish()
            }
        }
        timer.start()
    }
}