package com.thealeksandr.mediapicker;

import android.database.Cursor;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.thealeksandr.mediapicker.adapters.MediaCursorAdapter;
import com.thealeksandr.mediapicker.views.OnDragTouchListener;

import java.io.IOException;


/**
 * Created by Aleksandr Nikiforov on 4/26/17.
 */

public class MediaPickerFragment extends Fragment implements TextureView.SurfaceTextureListener {

    private PhotoView mPreviewImageView;
    private TextureView mPreviewVideoView;
    private View mPreviewVideoLayout;
    private MediaPlayer mMediaPlayer;
    private Uri mCurrentUri;
    private Surface mSurface;
    private int mWidth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_media_picker, container, false);

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE +
                " OR " +
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        String[] columns = new String[] {
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME

        };
        String orderBy = MediaStore.Files.FileColumns._ID;

        Cursor mediaCursor = getActivity().getContentResolver().query(
                MediaStore.Files.getContentUri("external"), columns, selection,
                null, orderBy);

        if (mediaCursor == null) {
            return view;
        }
        mediaCursor.moveToFirst();

        final MediaCursorAdapter adapter = new MediaCursorAdapter(getActivity(), mediaCursor, false);
        adapter.setOnItemClickListener(mOnItemClickListener);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.media_list_view);
        recyclerView.setAdapter(adapter);

        mPreviewImageView = (PhotoView) view.findViewById(R.id.preview_image_view);
        mPreviewVideoView = (TextureView) view.findViewById(R.id.preview_video_view);
        mPreviewVideoLayout = view.findViewById(R.id.preview_video_layout);
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
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                mWidth = view.getWidth();
            }
        });

        return view;
    }


    MediaCursorAdapter.OnItemClickListener mOnItemClickListener
            = new MediaCursorAdapter.OnItemClickListener() {
        @Override
        public void onClick(long id, final Uri uri, int type) {
            mCurrentUri = uri;
            if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                showImagePreview(uri);
            } else {
                showVideoPreview(uri);

            }
        }

    };

    private void showImagePreview(Uri uri) {
        resetMediaPlayer();
        mPreviewImageView.setVisibility(View.VISIBLE);
        mPreviewVideoLayout.setVisibility(View.INVISIBLE);
        mPreviewImageView.setImageURI(uri);
        /*Glide
                .with(this)
                .load(uri)
                .dontAnimate()
                .into(mPreviewImageView);*/

    }

    private void resetMediaPlayer() {
        if (mMediaPlayer != null) {
            try {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                mMediaPlayer.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mMediaPlayer = null;
        }
    }

    private void showVideoPreview(final Uri uri) {
        try {
            mPreviewImageView.setVisibility(View.INVISIBLE);
            mPreviewVideoLayout.setVisibility(View.VISIBLE);
            resetMediaPlayer();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(getActivity(), uri);
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setOnVideoSizeChangedListener(
                    new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
                    MediaMetadataRetriever m = new MediaMetadataRetriever();
                    m.setDataSource(getActivity(), uri);
                    m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                    int newHeight;
                    int newWidth;
                    if (width > height) {
                        newHeight = getResources().getDimensionPixelSize(R.dimen.media_picker_header_size);
                        newWidth = width * newHeight / height;
                    } else {
                        newWidth = mWidth;
                        newHeight = newWidth * height / width;
                    }
                    mPreviewVideoView.animate().x(0).y(0).setDuration(0).start();
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(newWidth, newHeight);
                    layoutParams.gravity = Gravity.CENTER;
                    mPreviewVideoView.setLayoutParams(layoutParams);
                    mPreviewVideoView.setOnTouchListener(new OnDragTouchListener(mPreviewVideoView, mPreviewVideoLayout));
                }
            });
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });

            mMediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get selected object uri.
     */
    public Uri getCurrentMediaUri() {
        return mCurrentUri;
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        mSurface = new Surface(surfaceTexture);
        if (mMediaPlayer != null) {
            mMediaPlayer.setSurface(mSurface);
        }
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
