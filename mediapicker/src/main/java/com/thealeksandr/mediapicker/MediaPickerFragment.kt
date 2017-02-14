package com.thealeksandr.mediapicker


import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thealeksandr.mediapicker.adapters.MediaAdapter



/**
 * Created by Aleksandr Nikiforov on 2/14/17.
 */
class MediaPickerFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString() +
                " OR " +
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Files.FileColumns._ID)
        val orderBy = MediaStore.Files.FileColumns._ID

        val mediaCursor = activity.contentResolver.query(
                MediaStore.Files.getContentUri("external"), columns, selection,
                null, orderBy)
        val columnIndex = mediaCursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
        mediaCursor.moveToFirst()
        val ids = mutableListOf<Int>()
        while (!mediaCursor.isAfterLast) {
            val id = mediaCursor.getInt(columnIndex)
            ids.add(id)
            mediaCursor.moveToNext()
        }
        mediaCursor.close()

        val view = inflater!!.inflate(R.layout.fragment_media_picker, container, false);

        val adapter = MediaAdapter(activity, ids)

        val recyclerView = view.findViewById(R.id.media_list_view) as RecyclerView
        recyclerView.adapter = adapter


        return view

    }



}