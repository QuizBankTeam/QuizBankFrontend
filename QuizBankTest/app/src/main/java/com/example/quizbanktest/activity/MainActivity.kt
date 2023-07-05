package com.example.quizbanktest.activity
import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.RecentViewAdapter
import com.example.quizbanktest.adapters.RecommendViewAdapter
import com.example.quizbanktest.adapters.WrongViewAdapter
import com.example.quizbanktest.models.QuestionBankModel
import com.example.quizbanktest.models.QuestionModel

import com.example.quizbanktest.utils.*
import com.google.android.material.navigation.NavigationView

import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

import com.squareup.okhttp.ResponseBody
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit.Callback
import retrofit.GsonConverterFactory
import retrofit.Response
import retrofit.Retrofit
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecentRecyclerView(ConstantsQuestionBank.getQuestions())
        setupRecommendRecyclerView(ConstantsRecommend.getQuestions())
        setupWrongListRecyclerView(ConstantsWrong.getQuestions())
        setupActionBar()

        var nav_view : com.google.android.material.navigation.NavigationView = findViewById(R.id.nav_view)
        nav_view.setNavigationItemSelectedListener(this)

        var bank : ImageButton = findViewById(R.id.bank)
        bank.setOnClickListener{
            gotoBankActivity()
        }

        var homeButton : ImageButton  = findViewById(R.id.home)
        homeButton.setOnClickListener{
            gotoHomeActivity()
        }

        var camera : ImageButton = findViewById(R.id.camera)
        camera?.setOnClickListener {
//
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems =
                arrayOf("Select photo from gallery", "Capture photo from camera")
            pictureDialog.setItems(
                pictureDialogItems
            ) { dialog, which ->
                when (which) {
                    // Here we have create the methods for image selection from GALLERY
                    0 -> choosePhotoFromGallery()
                    1 -> takePhotoFromCamera()
                }
            }
            pictureDialog.show()
        }

        var settingButton : ImageButton = findViewById(R.id.setting)
        settingButton.setOnClickListener{
            ConstantsServiceFunction.login(this@MainActivity)

        }
        ConstantsServiceFunction.getCsrfToken(this@MainActivity)
        ConstantsServiceFunction.login(this@MainActivity)
    }



    private fun setupRecentRecyclerView(quizBankList: ArrayList<QuestionBankModel>) {
        var recentQuizBankList : androidx.recyclerview.widget.RecyclerView = findViewById(R.id.recent_quiz_bank_list)
        recentQuizBankList?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        recentQuizBankList?.setHasFixedSize(true)

        val placesAdapter = RecentViewAdapter(this, quizBankList)
        recentQuizBankList?.adapter = placesAdapter


    }

    private fun setupWrongListRecyclerView(wrongList: ArrayList<QuestionModel>) {
        var recentWrongList : androidx.recyclerview.widget.RecyclerView = findViewById(R.id.recent_wrong_list)
        recentWrongList?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        recentWrongList?.setHasFixedSize(true)

        val placesAdapter = WrongViewAdapter(this, wrongList)
        recentWrongList?.adapter = placesAdapter
    }

    private fun setupRecommendRecyclerView(recommendList: ArrayList<QuestionModel>) {
        var recentRecommendList : androidx.recyclerview.widget.RecyclerView = findViewById(R.id.recent_recommend_list)
        recentRecommendList?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        recentRecommendList?.setHasFixedSize(true)

        val placesAdapter = RecommendViewAdapter(this, recommendList)
        recentRecommendList?.adapter = placesAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    private fun toggleDrawer() {
        var drawer_layout :DrawerLayout = findViewById(R.id.drawer_layout)
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
               ConstantsServiceFunction.logout(this@MainActivity)
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
        var drawer_layout :DrawerLayout = findViewById(R.id.drawer_layout)
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun setupActionBar() {
        var toolbar_main_activity : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_home_detail)
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