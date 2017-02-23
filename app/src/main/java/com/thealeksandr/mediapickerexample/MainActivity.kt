package com.thealeksandr.mediapickerexample

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.thealeksandr.mediapicker.MediaPickerFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showFragment(MediaPickerFragment(), R.id.activity_main)
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
