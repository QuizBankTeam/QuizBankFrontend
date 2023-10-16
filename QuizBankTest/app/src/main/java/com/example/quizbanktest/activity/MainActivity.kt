package com.example.quizbanktest.activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.os.BuildCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.account.MyProfileActivity
import com.example.quizbanktest.activity.paint.PaintActivity
import com.example.quizbanktest.activity.quiz.MPStartQuiz
import com.example.quizbanktest.activity.quiz.SPSingleRecord
import com.example.quizbanktest.activity.quiz.SingleQuestion
import com.example.quizbanktest.activity.scan.MathActivity
import com.example.quizbanktest.activity.scan.ScannerTextWorkSpaceActivity
import com.example.quizbanktest.adapters.main.RecentViewAdapter
import com.example.quizbanktest.adapters.main.RecommendViewAdapter
import com.example.quizbanktest.adapters.main.WrongViewAdapter
import com.example.quizbanktest.adapters.quiz.SPRecordAdapter
import com.example.quizbanktest.models.QuestionBankModel
import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.models.QuestionRecord
import com.example.quizbanktest.models.QuizRecord
import com.example.quizbanktest.utils.*
import com.google.android.material.navigation.NavigationView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val showEmptyBankImage : ImageView = findViewById(R.id.show_empty_bank_list)
        val showEmptyWrongQuizRecordImage : ImageView = findViewById(R.id.show_empty_wrong_list)
        showProgressDialog("處理資料中請稍等")
        ConstantsQuestionBankFunction.getAllUserQuestionBanks(this,
            onSuccess = { questionBanks ->
                setupRecentRecyclerView(questionBanks)
                setupRecommendRecyclerView(ConstantsRecommend.getQuestions())
                hideProgressDialog()
                showEmptyBankImage.visibility = View.GONE
            },
            onFailure = { errorMessage ->
                hideProgressDialog()
                setupRecommendRecyclerView(ConstantsRecommend.getQuestions())
                if(errorMessage == "Request failed with status code "){
                    Log.e("empty","eeeeeeeeeeeeeefjalsjfla;sjfl;asjf")
                    showEmptyBankImage.visibility = View.VISIBLE
                }else{
                    showErrorSnackBar("題庫資料取得錯誤")
                }

            }
        )

        val quizRecordType = Constants.quizTypeSingle
        ConstantsQuizRecord.getAllQuizRecords(this, quizRecordType, onSuccess = { returnRecordList->
            ConstantsQuizRecord.getAllQuizRecords(this, Constants.quizTypeCasual, onSuccess = { returnRecordList2->
                if(returnRecordList2.size != 0){
                    returnRecordList.addAll(returnRecordList2)
                }
                if(returnRecordList.size == 0){
                    showEmptyWrongQuizRecordImage.visibility = View.VISIBLE
                }else{
                    showEmptyWrongQuizRecordImage.visibility = View.GONE
                    setupWrongListRecyclerView(returnRecordList)
                }
            }, onFailure = {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                if(returnRecordList.size == 0){
                    showEmptyWrongQuizRecordImage.visibility = View.VISIBLE
                }else{
                    showEmptyWrongQuizRecordImage.visibility = View.GONE
                    setupWrongListRecyclerView(returnRecordList)
                }
            })
        }, onFailure = {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })


        setupActionBar()
        val nav_view : com.google.android.material.navigation.NavigationView = findViewById(R.id.nav_view)
        nav_view.setNavigationItemSelectedListener(this)

        setupNavigationView()

        doubleCheckExit()
        val joinText: androidx.appcompat.widget.AppCompatEditText =  findViewById(R.id.join_code_text)
        val joinBtn: android.widget.Button = findViewById(R.id.btn_join)
        joinBtn.setOnClickListener {
            val jText = joinText.text.toString()
            val intent = Intent()
            intent.setClass(this, MPStartQuiz::class.java)
            intent.putExtra("Key_id", jText)
            startActivityForResult(intent, 2000)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 2000){ //前往考試紀錄
            val intent = Intent()
            intent.setClass(this, SPSingleRecord::class.java)
            val questionRecordList = data?.getParcelableArrayListExtra<QuestionRecord>("Key_questionRecord")
            val quizRecord = data?.getParcelableExtra<QuizRecord>("Key_quizRecord")
            val activityMain = "activityMain"

            intent.putExtra("previousActivity", activityMain)
            intent.putParcelableArrayListExtra("Key_questionRecord", questionRecordList)
            intent.putExtra("Key_quizRecord", quizRecord)
            startActivity(intent)
        }
    }


    private fun setupRecentRecyclerView(quizBankList: ArrayList<QuestionBankModel>) {
        val recentQuizBankList : androidx.recyclerview.widget.RecyclerView = findViewById(R.id.recent_quiz_bank_list)
        recentQuizBankList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        recentQuizBankList.setHasFixedSize(true)

        val placesAdapter = RecentViewAdapter(this, quizBankList)
        recentQuizBankList.adapter = placesAdapter


    }

    private fun setupWrongListRecyclerView(wrongList: ArrayList<QuizRecord>) {
        val recentWrongList : androidx.recyclerview.widget.RecyclerView = findViewById(R.id.recent_wrong_list)
        recentWrongList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        recentWrongList.setHasFixedSize(true)

        val placesAdapter = WrongViewAdapter(this, wrongList)
        recentWrongList.adapter = placesAdapter
    }

    private fun setupRecommendRecyclerView(recommendList: ArrayList<QuestionModel>) {
        val recentRecommendList : androidx.recyclerview.widget.RecyclerView = findViewById(R.id.recent_recommend_list)
        recentRecommendList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        recentRecommendList.setHasFixedSize(true)

        val placesAdapter = RecommendViewAdapter(this, recommendList)
        recentRecommendList.adapter = placesAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun toggleDrawer() {
        val drawer_layout :DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_my_profile -> {
                val intent = Intent(this, MyProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.mathWorkSpace -> {
                if (Constants.isNetworkAvailable(this)) {
                    val intent  = Intent(this,MathActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this@MainActivity,"此功能需要網路請先開啟網路",Toast.LENGTH_SHORT).show()
                }

            }
            R.id.nav_sign_out -> {
               ConstantsAccountServiceFunction.logout(this@MainActivity)
            }
            R.id.imageEditor ->{
                val intent = Intent(this, PaintActivity::class.java)
                startActivity(intent)
            }
            R.id.ocrWorkSpace ->{
                val intent = Intent(this, ScannerTextWorkSpaceActivity::class.java)
                startActivity(intent)
            }
            R.id.publicArea ->{
                Toast.makeText(this@MainActivity,"公共區開發中",Toast.LENGTH_SHORT).show()
            }
        }
        val drawer_layout :DrawerLayout = findViewById(R.id.drawer_layout)
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun setupActionBar() {
        val toolbar_main_activity : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_home_detail)
        setSupportActionBar(toolbar_main_activity)
        supportActionBar?.title = ""
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }


    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2

        private const val IMAGE_DIRECTORY = "QuizTest"

    }

}