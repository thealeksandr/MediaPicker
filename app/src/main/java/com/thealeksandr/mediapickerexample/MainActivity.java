package com.thealeksandr.mediapickerexample;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import com.thealeksandr.mediapicker.MediaPickerFragment;

/**
 * Created by Aleksandr Nikiforov on 5/11/17.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showFragment(new MediaPickerFragment(), R.id.activity_main);
        //showFragment(new Camera2BasicFragment(), R.id.activity_main);
        //showFragment(new CameraBasicFragment(), R.id.activity_main);
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
