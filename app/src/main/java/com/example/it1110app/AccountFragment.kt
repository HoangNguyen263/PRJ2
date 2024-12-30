package com.example.it1110app

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.it1110app.DbQuery.Companion.g_usersCount
import com.example.it1110app.DbQuery.Companion.g_usersList
import com.example.it1110app.DbQuery.Companion.myPerformance
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private lateinit var logoutB: LinearLayout
    private lateinit var profile_img_text : TextView
    private lateinit var name: TextView
    private lateinit var score: TextView
    private lateinit var rank: TextView
    private lateinit var leaderB: LinearLayout
    private lateinit var profileB: LinearLayout
    private lateinit var bookmarkB: LinearLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var progressDialog: Dialog
    private lateinit var dialogText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_account, container, false)

        initViews(view)

        var toolbar : Toolbar = activity?.findViewById(R.id.toolbar)!!
        (activity as? AppCompatActivity)?.supportActionBar?.title = "My Account"

        progressDialog = Dialog(requireContext())
        progressDialog.setContentView(R.layout.dialog_layout)
        progressDialog.setCancelable(false) // Prevent dialog from closing when touched outside
        progressDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialogText = progressDialog.findViewById(R.id.dialog_text)
        dialogText.text = "Loading..."

        var userName : String = DbQuery.myProfile.name
        profile_img_text.text = userName.toUpperCase().substring(0,1)
        name.text = userName

        score.text= DbQuery.myPerformance.score.toString()

        if (DbQuery.g_usersList.size == 0){
            progressDialog.show()
            DbQuery().getTopUsers(object : MyCompleteListener {
                override fun onSuccess() {
                    if (DbQuery.myPerformance.score != 0){
                        if (!DbQuery.isMeOnTopList){
                          calculateRank();
                        }
                        score.text = "Score : " + DbQuery.myPerformance.score.toString()
                        rank.text = "Rank - " + DbQuery.myPerformance.rank.toString()
                    }
                    progressDialog.dismiss()
                }

                override fun onFailure() {
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
            })
        } else {
            score.text = "Score : " + DbQuery.myPerformance.score.toString()
            if(myPerformance.score != 0){
                rank.text = "Rank - " + DbQuery.myPerformance.rank.toString()
                }

        }

        logoutB.setOnClickListener {
            //sign out the user from Firebase
            FirebaseAuth.getInstance().signOut()

            //sign out the user from Google
            var gso : GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            var mGoogleClient : GoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

            mGoogleClient.signOut().addOnCompleteListener {
                var intent : Intent = Intent(context, LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                activity?.finish()

            }
        }

        bookmarkB.setOnClickListener{
            var intentbm : Intent = Intent(context, BookmarkActivity::class.java)
            startActivity(intentbm)
        }

        profileB.setOnClickListener {
            var intent : Intent = Intent(context, MyProfileActivity::class.java)
            startActivity(intent)
        }

        leaderB.setOnClickListener {
            bottomNavigationView.selectedItemId = R.id.navi_leaderboard
        }

        return view
    }

    private fun initViews(view: View){
        logoutB = view.findViewById(R.id.logoutB)
        profile_img_text = view.findViewById(R.id.profile_img_text)
        name = view.findViewById(R.id.profile_name)
        score = view.findViewById(R.id.total_score)
        rank = view.findViewById(R.id.rank)
        leaderB = view.findViewById(R.id.leaderboardB)
        profileB = view.findViewById(R.id.profileB)
        bookmarkB = view.findViewById(R.id.bookmarkB)
        bottomNavigationView = activity?.findViewById(R.id.bottom_nav_bar)!!
    }

    private fun calculateRank(){
        val lowTopScore = g_usersList[g_usersList.size - 1].score
        val remaining_slots = g_usersCount - 20


        //user's relative position among the remaining users
        val mySlot = myPerformance.score * remaining_slots / lowTopScore

        //If the user's score is not equal to the lowest top score
        var rank = if (lowTopScore != myPerformance.score) {
            g_usersCount - mySlot
        } else {
            21
        }

        myPerformance.rank = rank
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): AccountFragment {
            val fragment = AccountFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}