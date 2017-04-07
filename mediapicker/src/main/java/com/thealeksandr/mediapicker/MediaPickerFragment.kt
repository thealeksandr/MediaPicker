package com.thealeksandr.mediapicker


import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import com.bumptech.glide.Glide
import com.thealeksandr.mediapicker.adapters.MediaCursorAdapter


/**
 * Created by Aleksandr Nikiforov on 2/14/17.
 */
class MediaPickerFragment : Fragment() {

    private var previewImageView: ImageView? = null
    private var previewVideoView: VideoView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString() +
                " OR " +
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
        val columns = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns._ID)
        val orderBy = MediaStore.Files.FileColumns._ID

        val mediaCursor = activity.contentResolver.query(
                MediaStore.Files.getContentUri("external"), columns, selection,
                null, orderBy)

        mediaCursor.moveToFirst()
        val view = inflater!!.inflate(R.layout.fragment_media_picker, container, false)
        val adapter = MediaCursorAdapter(activity, mediaCursor, false)
        adapter.onItemClickListener = onItemClickListener
        val recyclerView = view.findViewById(R.id.media_list_view) as RecyclerView
        recyclerView.adapter = adapter



        previewImageView = view.findViewById(R.id.preview_image_view) as ImageView
        previewVideoView = view.findViewById(R.id.preview_video_view) as VideoView

        var appBarLayout = view.findViewById(R.id.app_bar) as AppBarLayout

        val params = appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = AppBarLayout.Behavior()
        behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return false
            }
        })

        params.behavior = behavior

        return view

    }


    val onItemClickListener = object : MediaCursorAdapter.OnItemClickListener {
        override fun onClick(id: Long, uri: Uri, type: Int) {
            if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                showImagePreview(id, uri)
            } else {
                showVideoPreview(uri)
            }
        }
    }

    private fun showImagePreview(id: Long, uri: Uri) {
        previewImageView?.visibility = View.VISIBLE
        previewVideoView?.visibility = View.INVISIBLE
        Glide
                .with(this)
                .load(uri)
                .dontAnimate()
                .centerCrop()
                .into(previewImageView)
        //ThumbnailUtils.extractThumbnail()
        /*previewImageView?.setImage(ImageSource.bitmap(
                MediaStore.Images.Thumbnails.getThumbnail(context.contentResolver, id,
                MediaStore.Images.Thumbnails.FULL_SCREEN_KIND, null)))*/
        //previewImageView?.setImage(ImageSource.uri(uri), ImageViewState(2f, PointF(0.5f, 0.5f), 0))
    }

    private fun showVideoPreview(uri: Uri) {
        previewImageView?.visibility = View.INVISIBLE
        previewVideoView?.visibility = View.VISIBLE
        //val thumbnail = ThumbnailUtils.createVideoThumbnail(
        //        FileUtils.getPath(context, uri), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND)
        previewVideoView!!.setVideoURI(uri)
        previewVideoView!!.requestFocus()
        previewVideoView!!.start()


    }

}