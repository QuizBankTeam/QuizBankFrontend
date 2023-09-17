package com.example.quizbanktest.activity.account

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.IntroActivity
import com.example.quizbanktest.activity.MainActivity
import com.example.quizbanktest.utils.ConstantsAccountServiceFunction
import com.google.android.material.snackbar.Snackbar

class SignUpActivity : AppCompatActivity() {
    private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val backBtn : ImageButton = findViewById(R.id.back_btn)
        backBtn.setOnClickListener {
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
            finish() }
        val signUpBtn : android.widget.Button = findViewById(R.id.signUpbutton)
        val email : EditText = findViewById(R.id.email)
        val userName : EditText = findViewById(R.id.account)
        val password : EditText = findViewById(R.id.password)
        signUpBtn.setOnClickListener {
            buttonClick(userName.text.toString(),email.text.toString(),password.text.toString())
        }
    }
    private fun buttonClick(userName : String,email: String, password: String){
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+(\\.[A-Za-z]+){1,6}$")
        val passwordRegex = Regex("[A-Za-z0-9._%+-]+")
        if(email.isEmpty()||password.isEmpty()){
//            Toast.makeText(this, "Login type is wrong", Toast.LENGTH_LONG).show()
        } else{
            showProgressDialog("登入中請稍等")
            startRegister(userName,email, password)
        }
//        if(email.matches(emailRegex) && password.matches(passwordRegex)){
//            startLogin(email, password)
//        } else{
//            if(!email.matches(emailRegex)){
//                Toast.makeText(this, "email type is wrong", Toast.LENGTH_LONG).show()
//            }else{
//                Toast.makeText(this, "password type is wrong", Toast.LENGTH_LONG).show()
//            }
//        }
    }

    private fun startRegister(userName: String,email: String, password: String){
//        showProgressDialog("登入中請稍等")
        ConstantsAccountServiceFunction.getCsrfToken(this,
            onSuccess = { it1 ->
                Log.d("get csrf success", it1)
                ConstantsAccountServiceFunction.register(this, userName,email, password,
                    onSuccess = {   message->
                        val intent = Intent()
                        intent.setClass(this, LoginActivity::class.java)
                        hideProgressDialog()
                        startActivity(intent)
                        finish()
                    },
                    onFailure = { message->
                        hideProgressDialog()
                        showErrorSnackBar("註冊失敗")
                    })
            },
            onFailure = { it1 ->
                hideProgressDialog()
                showErrorSnackBar("伺服器回應失敗")
            })
    }
    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.dialog_progress)

        mProgressDialog.findViewById<TextView>(R.id.tv_progressbar_text).text=text

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    fun showErrorSnackBar(message: String) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                this@SignUpActivity,
                R.color.red
            )
        )
        snackBar.show()
    }
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}