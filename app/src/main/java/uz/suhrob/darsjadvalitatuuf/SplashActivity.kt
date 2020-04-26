package uz.suhrob.darsjadvalitatuuf

import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash.*
import uz.suhrob.darsjadvalitatuuf.storage.SharedPreferencesHelper

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (SharedPreferencesHelper(applicationContext).darkThemeEnabled()) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme((R.style.AppTheme))
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        if (SharedPreferencesHelper(applicationContext).darkThemeEnabled()) {
            suhrob_logo_view.setImageResource(R.drawable.ic_suhrob_logo_dark)
        }

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 4000)

        val logoUpAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.logo_up)
        suhrob_logo_view.visibility = View.INVISIBLE
        Handler().postDelayed({
            suhrob_logo_view.visibility = View.VISIBLE
            val anim = suhrob_logo_view.drawable as AnimatedVectorDrawable
            anim.start()
            splash_icon_layout.visibility = View.VISIBLE
            splash_icon_layout.startAnimation(logoUpAnimation)
        }, 500)
    }
}
