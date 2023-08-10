package com.example.quizbanktest.activity.account
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.IntroActivity
import com.example.quizbanktest.activity.MainActivity
import com.example.quizbanktest.databinding.ActivityLoginBinding
import com.example.quizbanktest.utils.ConstantsAccountServiceFunction
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtn.setOnClickListener {
            val intent = Intent(this,IntroActivity::class.java)
            startActivity(intent)
            finish() }

        binding.loginbutton.setOnClickListener{ buttonClick() }

    }

    private fun buttonClick(){
        startLogin(" ", " ")
        val email : String = binding.account.text.toString()
        val password : String = binding.password.text.toString()
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+(\\.[A-Za-z]+){1,6}$")
        val passwordRegex = Regex("[A-Za-z0-9._%+-]+")
        if(email.isEmpty()||password.isEmpty()){
//            Toast.makeText(this, "Login type is wrong", Toast.LENGTH_LONG).show()
        } else{
            startLogin(email, password)
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

    private fun startLogin(email: String, password: String){
        showProgressDialog("登入中請稍等")
        ConstantsAccountServiceFunction.getCsrfToken(this,
            onSuccess = { it1 ->
                Log.d("get csrf success", it1)
                ConstantsAccountServiceFunction.login(this, email, password,
                onSuccess = {   message->
                    hideProgressDialog()
                    Log.d("login success", message)
                    writeToFile("loginSuccess.txt", message)
                    val intent = Intent()
                    intent.setClass(this, MainActivity::class.java)
                    startActivity(intent)
                    hideProgressDialog()
                    finish()
                },
                onFailure = { message->
                    hideProgressDialog()
                    showErrorSnackBar("登入失敗")
                    Log.d("login fail", message)
                })
            },
            onFailure = { it1 ->
                hideProgressDialog()
                showErrorSnackBar("伺服器回應失敗")
                Log.d("get csrf fail", it1)
            })
    }

    fun writeToFile(filename: String, data: String) {
        val cacheDir = externalCacheDir?.absoluteFile.toString()
        val file = File(cacheDir, filename)

        try {
            val fos = FileOutputStream(file)
            fos.write(data.toByteArray())
            fos.close()
        } catch (e: Exception) {
            Log.e("Error:", "Cannot write file: " + e.toString())
        }
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
                this@LoginActivity,
                R.color.red
            )
        )
        snackBar.show()
    }
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}