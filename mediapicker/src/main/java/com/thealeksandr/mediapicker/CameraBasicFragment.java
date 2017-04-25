package com.thealeksandr.mediapicker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.thealeksandr.mediapicker.views.CameraPreview;

/**
 * Created by Aleksandr Nikiforov on 4/25/17.
 */

public class CameraBasicFragment extends Fragment {

    private CameraPreview mPreview;
    private RelativeLayout mLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //mLayout = new RelativeLayout(getContext());
        //mLayout.setBackgroundColor(getResources().getColor(R.color.control_background));
        View view = inflater.inflate(R.layout.fragment_camera_basic, container, false);
        mLayout = (RelativeLayout) view.findViewById(R.id.preview_layout);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mPreview.stop();
        mLayout.removeView(mPreview); // This is necessary.
        mPreview = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set the second argument by your choice.
        // Usually, 0 for back-facing camera, 1 for front-facing camera.
        // If the OS is pre-gingerbreak, this does not have any effect.
        mPreview = new CameraPreview(getActivity(), 0, CameraPreview.LayoutMode.FitToParent);
        RelativeLayout.LayoutParams previewLayoutParams
                = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        // Un-comment below lines to specify the size.
        //previewLayoutParams.height = 500;
        //previewLayoutParams.width = 500;

        // Un-comment below line to specify the position.
        //mPreview.setCenterPosition(270, 130);

        mLayout.addView(mPreview, 0, previewLayoutParams);
    }
}
