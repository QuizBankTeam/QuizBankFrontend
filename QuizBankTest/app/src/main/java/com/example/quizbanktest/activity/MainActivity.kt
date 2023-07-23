package com.example.quizbanktest.activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.RecentViewAdapter
import com.example.quizbanktest.adapters.RecommendViewAdapter
import com.example.quizbanktest.adapters.WrongViewAdapter
import com.example.quizbanktest.models.QuestionBankModel
import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.utils.*
import com.google.android.material.navigation.NavigationView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ConstantsAccountServiceFunction.getCsrfToken(this,
            onSuccess = { it1 ->
                ConstantsAccountServiceFunction.login(this, " ", " ",
                    onSuccess = {   message->
                        Log.d("login success", message)

                        ConstantsQuestionBankFunction.getAllUserQuestionBanks(this,
                            onSuccess = { questionBanks ->
                                setupRecentRecyclerView(questionBanks)
                                setupRecommendRecyclerView(ConstantsRecommend.getQuestions())
                                setupWrongListRecyclerView(ConstantsWrong.getQuestions())
                            },
                            onFailure = { errorMessage ->
                                Toast.makeText(this,"get banklist error",Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onFailure = { message->
                        Log.d("login fail", message)
                    })
            },
            onFailure = { it1 ->
                Log.d("get csrf fail", it1)
            })

        setupActionBar()
        val nav_view : com.google.android.material.navigation.NavigationView = findViewById(R.id.nav_view)
        nav_view.setNavigationItemSelectedListener(this)

        val quiz : ImageButton = findViewById(R.id.test)
        quiz.setOnClickListener {
            gotoQuizActivity()
        }
        val bank : ImageButton = findViewById(R.id.bank)
        bank.setOnClickListener{
            gotoBankActivity()
        }

        val homeButton : ImageButton  = findViewById(R.id.home)
        homeButton.setOnClickListener{
            gotoHomeActivity()
        }

        val camera : ImageButton = findViewById(R.id.camera)
        camera.setOnClickListener {
            cameraPick()
        }

        val settingButton : ImageButton = findViewById(R.id.setting)
        settingButton.setOnClickListener{

        }
    }



    private fun setupRecentRecyclerView(quizBankList: ArrayList<QuestionBankModel>) {
        val recentQuizBankList : androidx.recyclerview.widget.RecyclerView = findViewById(R.id.recent_quiz_bank_list)
        recentQuizBankList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        recentQuizBankList.setHasFixedSize(true)

        val placesAdapter = RecentViewAdapter(this, quizBankList)
        recentQuizBankList.adapter = placesAdapter


    }

    private fun setupWrongListRecyclerView(wrongList: ArrayList<QuestionModel>) {
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
                Toast.makeText(this@MainActivity, "My Profile", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_sign_out -> {
               ConstantsAccountServiceFunction.logout(this@MainActivity)
            }
            R.id.imageEditor ->{
                val intent = Intent(this,PaintActivity::class.java)
                startActivity(intent)
            }
            R.id.ocrWorkSpace ->{
                val intent = Intent(this,ScannerTextWorkSpaceActivity::class.java)
                startActivity(intent)
            }
        }
        val drawer_layout :DrawerLayout = findViewById(R.id.drawer_layout)
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun setupActionBar() {
        val toolbar_main_activity : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_home_detail)
        setSupportActionBar(toolbar_main_activity)
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