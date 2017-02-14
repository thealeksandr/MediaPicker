package com.thealeksandr.mediapicker.adapters

import android.content.Context
import android.provider.MediaStore
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.thealeksandr.mediapicker.R

/**
 * Created by Aleksandr Nikiforov on 2/14/17.
 */
class MediaAdapter: RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {

    private val context: Context
    private var thumbnails: List<Int>? = null
    var onItemClickListener: OnItemClickListener<Int>? = null

    constructor(context: Context, thumbnails: List<Int>?) : super() {
        this.thumbnails = thumbnails ?: listOf()
        this.context = context
    }

    override fun getItemCount(): Int {
        return thumbnails?.size ?: 0
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        var bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.contentResolver,
                thumbnails!![position].toLong(),
                MediaStore.Images.Thumbnails.MINI_KIND, null)
        if (bitmap == null) {
            bitmap = MediaStore.Video.Thumbnails.getThumbnail(context.contentResolver,
                    thumbnails!![position].toLong(),
                    MediaStore.Video.Thumbnails.MINI_KIND, null)
        }
        holder.imageView.setImageBitmap(bitmap)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.view_media_preview,
                parent, false)
        val holder = MediaViewHolder(view)
        return holder
    }

    /**
     * @see android.support.v7.widget.RecyclerView.ViewHolder.ViewHolder
     */
    class MediaViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val rootView: View
        val imageView: ImageView

        init {
            rootView = view
            imageView = view.findViewById(R.id.thumb_image_view) as ImageView
        }
    }


}