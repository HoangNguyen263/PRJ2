package com.example.it1110app

import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.Nullable
import androidx.collection.ArrayMap
import com.example.it1110app.Models.CategoryModel
import com.example.it1110app.Models.ProfileModel
import com.example.it1110app.Models.QuestionModel
import com.example.it1110app.Models.RankModel
import com.example.it1110app.Models.TestModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class DbQuery {
    //we do all db functions here
    companion object {
        lateinit var g_firestore: FirebaseFirestore
        var g_catList: MutableList<CategoryModel> = mutableListOf()

        var g_selected_cat_index: Int = 0
        var g_testList: MutableList<TestModel> = mutableListOf()
        var g_questionList: MutableList<QuestionModel> = mutableListOf()
        var g_selected_test_index: Int = 0

        var myProfile: ProfileModel = ProfileModel("NA", "NA", null,0)
        var myPerformance : RankModel = RankModel(0,-1,"NA")

        var g_usersList : MutableList<RankModel> = mutableListOf()
        var g_usersCount : Int = 0 //only load 20 users into Leaderboard
        var isMeOnTopList = false

        val NOT_VISITED: Int = 0
        val UNANSWERED: Int = 1
        val ANSWERED: Int = 2
        val REVIEW: Int = 3

        var g_bookmarkIdList : MutableList<String> = mutableListOf()

        var g_bookmarkList: MutableList<QuestionModel> = mutableListOf()

        var tmp: Int = 0

        fun initializeFirestore() {
            g_firestore = FirebaseFirestore.getInstance()
        }

        // Hàm tính tổng điểm cao nhất của các bài test
        private fun calculateTotalScore(): Int {
            return g_testList.sumBy { it.topScore }
        }
    }

    public fun createUserData(email : String, name: String,completeListener: MyCompleteListener ){
        var userData : MutableMap<String, Any> = ArrayMap()
        userData.put("EMAIL_ID",email)
        userData.put("NAME",name)
        userData.put("TOTAL_SCORE",0)
        userData.put("MY_BOOKMARKS",0)

        //add all data to a document in firebase
        //create document for USERS collection
        var userDoc: DocumentReference? =
            FirebaseAuth.getInstance().currentUser?.let {
                g_firestore.collection("USERS").document(it.uid)
            }
        //update account in document
        var batch:WriteBatch= g_firestore.batch()
        batch.set(userDoc!!,userData)

        var countdoc:DocumentReference = g_firestore.collection("USERS")
            .document("TOTAL_USERS")
        batch.update(countdoc,"COUNT", FieldValue.increment(1))
        batch.commit().addOnSuccessListener {
            completeListener.onSuccess()
        }.addOnFailureListener {
            completeListener.onFailure()
        }
    }

    //load top scores of each test and display to user
    public fun loadMyScores(completeListener: MyCompleteListener){
        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().uid!!)
            .collection("USER_DATA")
            .document("MY_SCORE")
            .get()
            .addOnSuccessListener(object : OnSuccessListener<DocumentSnapshot> {
                override fun onSuccess(documentSnapshot: DocumentSnapshot) {
                    for (i in 0..g_testList.size -1){
                        var top : Int = 0
                        if (documentSnapshot.get(g_testList.get(i).testId.toString()) != null){
                            top = documentSnapshot.getLong(g_testList.get(i).testId!!)!!.toInt()
                        }
                        g_testList.get(i).topScore = top
                    }
                    completeListener.onSuccess()
                }
            })
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    completeListener.onFailure()
                }
            })
    }

    // Hàm saveResult tối ưu
    public suspend fun saveResult(score: Int): Boolean {
        val batch: WriteBatch = g_firestore.batch()

        // Thêm dữ liệu bookmark nếu cần
        if (g_bookmarkIdList.isNotEmpty()) {
            val bmData: MutableMap<String, Any> = ArrayMap()
            g_bookmarkIdList.forEachIndexed { index, bookmarkId ->
                bmData["BM${index + 1}_ID"] = bookmarkId
            }

            val bmDoc: DocumentReference = g_firestore.collection("USERS")
                .document(FirebaseAuth.getInstance().uid!!)
                .collection("USER_DATA")
                .document("MY_BOOKMARKS")
            batch.set(bmDoc, bmData)
        }

        // Cập nhật thông tin người dùng
        val userDoc: DocumentReference = g_firestore.collection("USERS")
            .document(FirebaseAuth.getInstance().uid!!)
        Log.e("saveResult", "Top score1: ${g_testList[g_selected_test_index].topScore}, Score1: ${score}")


//        Log.e("saveResult", "Checkpoint 1: ${userData}")


        // Cập nhật điểm nếu cần
        Log.e("saveResult", "Top score2: ${g_testList[g_selected_test_index].topScore}, Score2: ${score}")
        if (score > g_testList[g_selected_test_index].topScore) {
            g_testList[g_selected_test_index].topScore = score

            val scoreDoc: DocumentReference = userDoc.collection("USER_DATA")
                .document("MY_SCORE")
            val testData: MutableMap<String, Any> = ArrayMap()
            testData[g_testList[g_selected_test_index].testId!!] = score
            batch.set(scoreDoc, testData, SetOptions.merge())
            Log.e("saveResult", "testData: ${testData}")
        }
        val totalScore = calculateTotalScore()
        val userData: MutableMap<String, Any> = ArrayMap()
        userData["TOTAL_SCORE"] = totalScore
        userData["MY_BOOKMARKS"] = g_bookmarkIdList.size
        batch.update(userDoc, userData)

        // Commit batch bằng coroutine
        return try {
            batch.commit().await() // Dùng `.await()` để chờ commit hoàn thành
            true
        } catch (e: Exception) {
            Log.e("saveResult", "Error: ${e.message}")
            false
        }
    }

    public fun loadCategories(completeListener: MyCompleteListener){
        g_catList.clear()
        g_firestore.collection("QUIZ")
            .get().addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(queryDocumentSnapshot: QuerySnapshot){
                    var docList :MutableMap<String, QueryDocumentSnapshot> = ArrayMap()

                    for (doc: QueryDocumentSnapshot in queryDocumentSnapshot){
                        docList.put(doc.id,doc)
                    }

                    //extract categories from document
                    var catListDoc: QueryDocumentSnapshot = docList.get("Categories")!!

                    var catCount: Long = catListDoc.getLong("COUNT")!!
                    for (i in 1..catCount){
                        var catID = catListDoc.getString("CAT${i}_ID")
                        var catDoc: DocumentSnapshot = docList.get(catID)!!

                        val noOfTest: Int = (catDoc.getLong("NO_OF_TEST") ?: 0).toInt()

                        var catName : String = catDoc.getString("NAME").toString()

                        g_catList.add(CategoryModel(catID,catName,noOfTest))
                    }
                    completeListener.onSuccess()
                }
            }).addOnFailureListener(object:OnFailureListener {
                override fun onFailure(e: Exception){
                    completeListener.onFailure()
                }
            })
    }

    public fun loadTestData(completeListener: MyCompleteListener) {
        g_testList.clear()

        g_firestore.collection("QUIZ").document(g_catList.get(g_selected_cat_index).docID!!)
            .collection("TESTS_LIST").document("TESTS_INFO")
            .get()
            .addOnSuccessListener(object : OnSuccessListener<DocumentSnapshot> {
                override fun onSuccess(documentSnapshot: DocumentSnapshot) {
                    var noOfTests: Int = g_catList.get(g_selected_cat_index).noOfTest ?: 0

                    for (i in 1..noOfTests) {

//                            g_testList.add(TestModel(documentSnapshot.getString("TEST${i}_ID"),
//                                0,
//                                documentSnapshot.getLong("TEST${i}_TIME")!!.toInt()))
                        val testId = documentSnapshot.getString("TEST${i}_ID")
                        val testTime = documentSnapshot.getLong("TEST${i}_TIME")?.toInt()

                        if (testId != null && testTime != null) {
                            g_testList.add(TestModel(testId, 0, testTime))
                        } else {
                            // Xử lý trường hợp không có dữ liệu
                            Log.e("DbQuery", "Missing data for ${testId}")
                        }

                    }

                    completeListener.onSuccess()
                }
            })
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    completeListener.onFailure()
                }
            })
    }

    public fun saveProfileData(name: String, phone: String, completeListener: MyCompleteListener){
        var userData : MutableMap<String, Any> = ArrayMap()
        userData.put("NAME",name)

        if (phone != ""){
            userData.put("PHONE",phone)
        }

        //add all data to a document in firebase
        //create document for USERS collection
        var userDoc: DocumentReference? =
            FirebaseAuth.getInstance().currentUser?.let {
                g_firestore.collection("USERS").document(it.uid)
            }
        //update account in document
        userDoc?.update(userData)?.addOnSuccessListener {
            myProfile.name = name
            if (phone != ""){
                myProfile.phone = phone
            }
            completeListener.onSuccess()
        }?.addOnFailureListener {
            completeListener.onFailure()
        }
    }

    //load user data to side navigation drawer
    public fun getUserData(completeListener: MyCompleteListener){
        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().uid!!)
            .get()
            .addOnSuccessListener(object : OnSuccessListener<DocumentSnapshot> {
                override fun onSuccess(documentSnapshot: DocumentSnapshot) {
                    myProfile.name = documentSnapshot.getString("NAME").toString()
                    myProfile.email = documentSnapshot.getString("EMAIL_ID").toString()
                    if (documentSnapshot.getString("PHONE") != null){
                        myProfile.phone = documentSnapshot.getString("PHONE").toString()
                    }
                    if (documentSnapshot.get("MY_BOOKMARKS") != 0){
                        myProfile.bookmarksCount = documentSnapshot.getLong("MY_BOOKMARKS")!!.toInt()
                    }
                    myPerformance.score = documentSnapshot.getLong("TOTAL_SCORE")!!.toInt()
                    myPerformance.name = documentSnapshot.getString("NAME").toString()
                    completeListener.onSuccess()
                }
            })
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    completeListener.onFailure()
                }
            })
    }

    public fun loadData(completeListener: MyCompleteListener){
        loadCategories(object : MyCompleteListener {
            override fun onSuccess() {
                getUserData(object : MyCompleteListener {
                    override fun onSuccess() {
                        getUsersCount(object: MyCompleteListener {
                            override fun onSuccess() {
                                loadbookmarkIds(completeListener)
                                completeListener.onSuccess()
                            }

                            override fun onFailure() {
                                completeListener.onFailure()
                            }
                        })
                    }

                    override fun onFailure() {
                        completeListener.onFailure()
                    }
                })
            }

            override fun onFailure() {
                completeListener.onFailure()
            }
        })
    }

    public fun loadQuestions(completeListener: MyCompleteListener){
        g_questionList.clear()
        //fetch questions from firebase and store in the list
        val category = g_catList[g_selected_cat_index].docID
        val test = g_testList[g_selected_test_index].testId
        Log.d("DbQuery", "Querying for CATEGORY: $category, TEST: $test")
        g_firestore.collection("Questions")
            .whereEqualTo("CATEGORY",g_catList.get(g_selected_cat_index).docID)
            .whereEqualTo("TEST",g_testList.get(g_selected_test_index).testId)
            .get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(queryDocumentSnapshot: QuerySnapshot) {
                    Log.d("DbQuery", "Total documents fetched: ${queryDocumentSnapshot.size()}")
                    for (doc: DocumentSnapshot in queryDocumentSnapshot){
                        Log.d("DbQuery", "Document data: ${doc.data}")
                        var isBookmarked : Boolean = false
                        if (g_bookmarkIdList.contains(doc.id)){
                            isBookmarked = true
                        }

                        g_questionList.add(
                            QuestionModel(
                                doc.id,
                                doc.getString("QUESTION"),
                            doc.getString("A"),
                            doc.getString("B"),
                            doc.getString("C"),
                            doc.getString("D"),
                            doc.getLong("ANSWER")?.toInt(),
                            -1,
                            NOT_VISITED,
                            isBookmarked
                            )
                        )
                    }
                    Log.d("DbQuery", "Question size: ${g_questionList.size}")
                    completeListener.onSuccess()
                }
            })
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {

                    completeListener.onFailure()
                }
            })
    }

    public fun getTopUsers(completeListener: MyCompleteListener){
        g_usersList.clear()

        var myUID = FirebaseAuth.getInstance().uid

        g_firestore.collection("USERS")
            .whereGreaterThan("TOTAL_SCORE",0)
            .orderBy("TOTAL_SCORE", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limitToLast(20)
            .get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(queryDocumentSnapshot: QuerySnapshot) {
                    for (doc: QueryDocumentSnapshot in queryDocumentSnapshot){
                        var rank = 1
                        g_usersList.add(RankModel(doc.getLong("TOTAL_SCORE")!!.toInt(),
                            rank,
                            doc.getString("NAME")!!
                        ))

                        //check if current user is in top list
                        if (myUID!!.compareTo(doc.id) == 0){
                            isMeOnTopList = true
                            myPerformance.rank = rank
                        }

                        rank++

                    }
                    completeListener.onSuccess()
                }
            })
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    completeListener.onFailure()
                }
            })
    }

    public fun getUsersCount(completeListener: MyCompleteListener){
        g_firestore.collection("USERS")
            .document("TOTAL_USERS")
            .get()
            .addOnSuccessListener(object : OnSuccessListener<DocumentSnapshot> {
                override fun onSuccess(documentSnapshot: DocumentSnapshot) {
                    g_usersCount = documentSnapshot.getLong("COUNT")!!.toInt()
                    completeListener.onSuccess()
                }
            })
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    completeListener.onFailure()
                }
            })
    }

    public fun loadbookmarkIds(completeListener: MyCompleteListener){
        g_bookmarkIdList.clear()
        g_firestore.collection("USERS")
            .document(FirebaseAuth.getInstance().uid!!)
            .collection("USER_DATA")
            .document("MY_BOOKMARKS")
            .get()
            .addOnSuccessListener(object : OnSuccessListener<DocumentSnapshot> {
                override fun onSuccess(documentSnapshot: DocumentSnapshot) {
                    var count : Int = myProfile.bookmarksCount
                    for (i in 0..count-1){
                        var bmId: String =
                            documentSnapshot.getString("BM" + (i+1).toString() +"_ID").toString()
                        g_bookmarkIdList.add(bmId)
                    }
                    completeListener.onSuccess()
                }
            })
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    completeListener.onFailure()
                }
            })
    }

    public fun loadbookmarkQuestions(completeListener: MyCompleteListener){
        g_bookmarkList.clear()

        if (g_bookmarkIdList.size == 0){
            completeListener.onSuccess()
            return
        }
        var tmp = 0
        for (i in 0..g_bookmarkIdList.size -1){
            var docId: String = g_bookmarkIdList.get(i)
            g_firestore.collection("Questions")
                .document(docId)
                .get()
                .addOnSuccessListener(object : OnSuccessListener<DocumentSnapshot> {
                    override fun onSuccess(documentSnapshot: DocumentSnapshot) {
                        if (documentSnapshot.exists()){
                            g_bookmarkList.add(
                                QuestionModel(
                                    documentSnapshot.id,
                                    documentSnapshot.getString("QUESTION"),
                                    documentSnapshot.getString("A"),
                                    documentSnapshot.getString("B"),
                                    documentSnapshot.getString("C"),
                                    documentSnapshot.getString("D"),
                                    documentSnapshot.getLong("ANSWER")!!.toInt(),
                                    0,
                                    -1,
                                    false
                                )
                            )
                        }
                        tmp++
                        if (tmp == g_bookmarkIdList.size){
                            completeListener.onSuccess()
                        }
                    }
                })
                .addOnFailureListener(object : OnFailureListener {
                    override fun onFailure(e: Exception) {
                        completeListener.onFailure()
                    }
                })


        }
    }
}