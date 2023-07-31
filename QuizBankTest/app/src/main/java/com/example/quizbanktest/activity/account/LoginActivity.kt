package com.example.quizbanktest.activity.account
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.quizbanktest.activity.MainActivity
import com.example.quizbanktest.databinding.ActivityLoginBinding
import com.example.quizbanktest.utils.ConstantsAccountServiceFunction
import java.io.File
import java.io.FileOutputStream

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtn.setOnClickListener { finish() }

        binding.loginbutton.setOnClickListener{ buttonClick() }

    }

    private fun buttonClick(){
        startLogin(" ", " ")
//        val email : String = binding.account.text.toString()
//        val password : String = binding.password.text.toString()
//        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+(\\.[A-Za-z]+){1,6}$")
//        val passwordRegex = Regex("[A-Za-z0-9._%+-]+")
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
        ConstantsAccountServiceFunction.getCsrfToken(this,
            onSuccess = { it1 ->
                Log.d("get csrf success", it1)
                ConstantsAccountServiceFunction.login(this, email, password,

                onSuccess = {   message->
                    Log.d("login success", message)
                    writeToFile("loginSuccess.txt", message)

                    val intent = Intent()
                    intent.setClass(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                onFailure = { message->
                    Log.d("login fail", message)
                })
            },

            onFailure = { it1 ->
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
}