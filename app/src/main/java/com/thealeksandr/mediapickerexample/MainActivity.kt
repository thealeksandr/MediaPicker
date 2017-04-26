package com.thealeksandr.mediapickerexample

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.thealeksandr.mediapicker.MediaOldPickerFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showFragment(MediaOldPickerFragment(), R.id.activity_main)
        //showFragment(Camera2BasicFragment(), R.id.activity_main)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //showFragment(CameraBasicFragment(), R.id.activity_main)
    }

    fun AppCompatActivity.showFragment(fragment: Fragment, containerId: Int) {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount == 0 && fragmentManager.fragments == null) {
            var transaction = supportFragmentManager.beginTransaction().replace(containerId,
                    fragment)
            transaction.commit()
        } else {
            var transaction = supportFragmentManager.beginTransaction()
            transaction.replace(containerId, fragment).addToBackStack(null)
            transaction.commit()
        }
    }
}
