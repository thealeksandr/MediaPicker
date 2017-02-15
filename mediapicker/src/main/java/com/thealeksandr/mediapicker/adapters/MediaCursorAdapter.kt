package com.thealeksandr.mediapicker.adapters

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.thealeksandr.mediapicker.R


/**
 * Created by Aleksandr Nikiforov on 2/15/17.
 */
class MediaCursorAdapter: CursorRecyclerAdapter<MediaCursorAdapter.MediaViewHolder> {

    private val context: Context
    private val idColumnIndex: Int
    private val typeColumnIndex: Int

    constructor(context: Context, cursor: Cursor, autoRequery: Boolean)
            : super(context, cursor, autoRequery) {
        this.context = context
        idColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
        typeColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)
    }

    override fun onBindViewHolder(viewHolder: MediaCursorAdapter.MediaViewHolder?,
                                  cursor: Cursor?) {
        var bitmap: Bitmap
        val type = cursor!!.getInt(typeColumnIndex)
        val id = cursor.getLong(idColumnIndex)
        if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.contentResolver,
                    id,
                    MediaStore.Images.Thumbnails.MINI_KIND, null)
        } else {
            bitmap = MediaStore.Video.Thumbnails.getThumbnail(context.contentResolver,
                    id,
                    MediaStore.Video.Thumbnails.MINI_KIND, null)

            val uri = Uri.
                    withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id.toString())


            val retriever = MediaMetadataRetriever()

            retriever.setDataSource(context, uri)
            val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            //val timeInMillisec = java.lang.Long.parseLong(time)

            viewHolder!!.textView.text = time
        }

        viewHolder!!.imageView.setImageBitmap(bitmap)

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int)
            : MediaCursorAdapter.MediaViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.view_media_preview,
                parent, false)
        val holder = MediaCursorAdapter.MediaViewHolder(view)
        return holder
    }

    /**
     * @see android.support.v7.widget.RecyclerView.ViewHolder.ViewHolder
     */
    class MediaViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val rootView: View = view
        val imageView: ImageView = view.findViewById(R.id.thumb_image_view) as ImageView
        val textView: TextView = view.findViewById(R.id.duration_text_view) as TextView

    }

}