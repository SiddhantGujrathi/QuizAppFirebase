package com.siddhant.quizapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.siddhant.quizappnew.R
import com.siddhant.quizappnew.databinding.ActivityLoginIntroBinding
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception

class LoginIntro : AppCompatActivity() {

    private lateinit var binding: ActivityLoginIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* To play LOTTIE animation */
        val animationView: LottieAnimationView = findViewById(R.id.imageView5)
        animationView.setAnimation(R.raw.wave)
        animationView.playAnimation()

        /* To get firebase authentication instance */
        val auth = FirebaseAuth.getInstance()

        /* If user is already logged in redirecting to Main Activity */
        if(auth.currentUser != null){
            Toast.makeText(this, "User is already logged in!", Toast.LENGTH_SHORT).show()
            redirect("MAIN")
        }

        /* auth.currentUser allows you to quickly check the current user's
        authentication status without directly querying the Firebase
        Authentication database each time.*/

        /* If user is not logged in redirecting to Login Activity */
        binding.btnGetStarted.setOnClickListener {
            redirect("LOGIN")
        }
    }


    /* Function to redirect to MAIN or LOGIN activity*/
    private fun redirect(name:String){
        val intent = when(name){
            "LOGIN" -> Intent(this, LoginActivity::class.java)
            "MAIN" -> Intent(this, MainActivity::class.java)
            else -> throw Exception("no path exists")
        }
        startActivity(intent)
        finish()
    }

}