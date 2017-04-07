package com.thealeksandr.mediapicker.adapters

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.AsyncTask
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
    var onItemClickListener: OnItemClickListener? = null
    var first: Boolean = true

    constructor(context: Context, cursor: Cursor, autoRequery: Boolean)
            : super(context, cursor, autoRequery) {
        this.context = context
        idColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
        typeColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)
    }

    override fun onBindViewHolder(viewHolder: MediaCursorAdapter.MediaViewHolder?,
                                  cursor: Cursor?) {
        val type = cursor!!.getInt(typeColumnIndex)
        val id = cursor.getLong(idColumnIndex)
        val uri: Uri
        if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            uri = Uri.
                    withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
        } else {
            uri = Uri.
                    withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id.toString())
        }

        viewHolder!!.itemView.tag = id

        if (first) {
            onItemClickListener?.onClick(id, uri, type)
            first = false
        }
        viewHolder.imageView.setImageBitmap(null)
        GetFileInfoTask(type, uri, viewHolder).execute(id)

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

        val imageView: ImageView = view.findViewById(R.id.thumb_image_view) as ImageView
        val textView: TextView = view.findViewById(R.id.duration_text_view) as TextView

    }

    interface OnItemClickListener {
        fun onClick(id: Long, uri: Uri, type: Int)
    }


    private inner class GetFileInfoTask(var type: Int, var uri: Uri,
                                        var viewHolder: MediaViewHolder) :
            AsyncTask<Long, Int, Bitmap>() {

        private var time: String? = null
        private var id: Long? = null

        override fun doInBackground(vararg ids: Long?): Bitmap? {
            id = ids[0]
            val bitmap: Bitmap
            if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.contentResolver,
                        id!!,
                        MediaStore.Images.Thumbnails.MINI_KIND, null)
            } else {
                bitmap = MediaStore.Video.Thumbnails.getThumbnail(context.contentResolver,
                        id!!,
                        MediaStore.Video.Thumbnails.MINI_KIND, null)
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, uri)
                time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            }
            return bitmap
        }


        override fun onPostExecute(result: Bitmap?) {
            if (viewHolder.itemView.tag == id) {
                viewHolder.imageView.setImageBitmap(result)
                viewHolder.textView.text = time
                viewHolder.itemView.setOnClickListener({
                    onItemClickListener?.onClick(id!!, uri, type)
                })
            }

        }
    }

}