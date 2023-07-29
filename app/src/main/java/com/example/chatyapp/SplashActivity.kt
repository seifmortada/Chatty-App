package com.example.chatyapp
import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.chatyapp.databinding.ActivitySplashBinding
import com.example.chatyapp.loginRegisterActivites.LoginActivity

class SplashActivity : AppCompatActivity() {
private lateinit var binding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding=DataBindingUtil.setContentView(this,R.layout.activity_splash)


        val logoAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.zoom_out)
        binding. logoImageView.startAnimation(logoAnimation)

        logoAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                // Animation has ended, navigate to the next screen (e.g., MainActivity)
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
        })
    }
}
