package com.thealeksandr.mediapickerexample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.thealeksandr.mediapicker.CameraBasicFragment;

/**
 * Created by Aleksandr Nikiforov on 5/11/17.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //showFragment(new MediaPickerFragment(), R.id.activity_main);
        //showFragment(new Camera2BasicFragment(), R.id.activity_main);
        showFragment(new CameraBasicFragment(), R.id.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    private void showFragment(Fragment fragment, int containerId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() == 0
                && fragmentManager.getFragments() == null) {
            FragmentTransaction transaction
                    = fragmentManager.beginTransaction().replace(containerId, fragment);
            transaction.commit();
        } else {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(containerId, fragment).addToBackStack(null);
            transaction.commit();
        }
    }

}
