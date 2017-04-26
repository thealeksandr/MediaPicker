package com.thealeksandr.mediapicker;

import android.database.Cursor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.thealeksandr.mediapicker.adapters.MediaCursorAdapter;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Aleksandr Nikiforov on 4/26/17.
 */

public class MediaPickerFragment extends Fragment implements TextureView.SurfaceTextureListener {

    private ImageView mPreviewImageView;
    private TextureView mPreviewVideoView;
    private MediaPlayer mMediaPlayer;
    private Uri mCurrentUri;
    private Surface mSurface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE +
                " OR " +
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        String[] columns = new String[] {
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns._ID
        };
        String orderBy = MediaStore.Files.FileColumns._ID;

        Cursor mediaCursor = getActivity().getContentResolver().query(
                MediaStore.Files.getContentUri("external"), columns, selection,
                null, orderBy);

        mediaCursor.moveToFirst();
        View view = inflater.inflate(R.layout.fragment_media_picker, container, false);
        MediaCursorAdapter adapter = new MediaCursorAdapter(getActivity(), mediaCursor, false);
        adapter.setOnItemClickListener(mOnItemClickListener);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.media_list_view);
        recyclerView.setAdapter(adapter);

        mPreviewImageView = (ImageView) view.findViewById(R.id.preview_image_view);
        mPreviewVideoView = (TextureView) view.findViewById(R.id.preview_video_view);
        mPreviewVideoView.setSurfaceTextureListener(this);

        AppBarLayout appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar);

        CoordinatorLayout.LayoutParams params
                = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(AppBarLayout appBarLayout) {
                return false;
            }
        });
        params.setBehavior(behavior);
        return view;
    }


    MediaCursorAdapter.OnItemClickListener mOnItemClickListener
            = new MediaCursorAdapter.OnItemClickListener() {
        @Override
        public void onClick(long id, @NotNull Uri uri, int type) {
            mCurrentUri = uri;
            if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                showImagePreview(uri);
            } else {
                showVideoPreview(uri);
            }
        }

    };

    private void showImagePreview(Uri uri) {
        mPreviewImageView.setVisibility(View.VISIBLE);
        mPreviewVideoView.setVisibility(View.INVISIBLE);
        Glide
                .with(this)
                .load(uri)
                .dontAnimate()
                .centerCrop()
                .into(mPreviewImageView);
    }

    private void showVideoPreview(Uri uri) {
        mPreviewImageView.setVisibility(View.INVISIBLE);
        mPreviewVideoView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        mSurface = new Surface(surfaceTexture);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
