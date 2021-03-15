package com.robustastudio.weather.splash

import android.content.Intent
import androidx.constraintlayout.motion.widget.MotionLayout
import com.robustastudio.weather.common.base.BaseActivity
import com.robustastudio.weather.databinding.ActivitySplashBinding
import com.robustastudio.weather.main.MainActivity

class SplashActivity : BaseActivity<ActivitySplashBinding>(), MotionLayout.TransitionListener {


    override fun getViewBinding(): ActivitySplashBinding =
        ActivitySplashBinding.inflate(layoutInflater)

    override fun initOnCreate() {

        binding.root.setTransitionListener(this)

    }

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

    }

    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
    }

    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

    }


}