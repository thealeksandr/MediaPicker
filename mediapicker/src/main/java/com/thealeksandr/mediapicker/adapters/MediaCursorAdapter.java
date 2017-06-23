package com.thealeksandr.mediapicker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealeksandr.mediapicker.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aleksandr Nikiforov on 5/11/17.
 */
public class MediaCursorAdapter extends CursorRecyclerAdapter<MediaCursorAdapter.MediaViewHolder> {

    private Context context;
    private int idColumnIndex;
    private int nameColumnIndex;
    private int typeColumnIndex;
    private OnItemClickListener onItemClickListener;
    private boolean first;
    private LruCache<Long, Bitmap> mMemoryCache;
    private long maxMemory = (Runtime.getRuntime().maxMemory() / 1024);
    int cacheSize = (int) (maxMemory / 8);
    private Map<Long, String> dateCache = new HashMap<>();

    public MediaCursorAdapter(Context context, Cursor cursor, Boolean autoRequery) {
        super(context, cursor, autoRequery);
        this.context = context;
        idColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
        nameColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
        typeColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);
        mMemoryCache = new LruCache<Long, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Long key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }

    @Override
    public void onBindViewHolder(MediaViewHolder viewHolder, Cursor cursor) {
        final int type = cursor.getInt(typeColumnIndex);
        final long id = cursor.getLong(idColumnIndex);
        final String name = cursor.getString(nameColumnIndex);
        final Uri uri;
        if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            uri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
        } else {
            uri = Uri.withAppendedPath(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
        }

        viewHolder.itemView.setTag(id);

        if (first) {
            onItemClickListener.onClick(id, uri, type);
            first = false;
        }
        viewHolder.imageView.setImageBitmap(null);
        Bitmap bitmap = getBitmapFromMemCache(id);
        if (bitmap != null) {
            viewHolder.imageView.setImageBitmap(bitmap);
            viewHolder.textView.setText(dateCache.get(id));
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(id, uri, type);
                    }
                }
            });
        } else {
            new GetFileInfoTask(type, uri, name, viewHolder).execute(id);
        }

    }



    @Override
    public MediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_media_preview,
                parent, false);
        MediaViewHolder holder = new MediaViewHolder(view);
        return holder;
    }

    /**
     * @see android.support.v7.widget.RecyclerView.ViewHolder#ViewHolder(View view)
     */
    class MediaViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        MediaViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.thumb_image_view);
            textView = (TextView) itemView.findViewById(R.id.duration_text_view);
        }

    }

    public interface OnItemClickListener {
        void onClick(long id, Uri uri, int type);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private class GetFileInfoTask extends AsyncTask<Long, Integer, Bitmap> {

        private String time;
        private String name;
        private Long id;

        private int type;
        private Uri uri;
        private MediaViewHolder viewHolder;

        GetFileInfoTask(int type, Uri uri, String name, MediaViewHolder viewHolder) {
            this.type = type;
            this.uri = uri;
            this.viewHolder = viewHolder;
            this.name = name;
        }

        @Override
        public Bitmap doInBackground(Long... ids) {
            id = ids[0];
            Bitmap bitmap;
            if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
                        id, MediaStore.Images.Thumbnails.MINI_KIND, null);
            } else {
                bitmap = MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(),
                        id, MediaStore.Video.Thumbnails.MINI_KIND, null);
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                try {
                    retriever.setDataSource(context, uri);
                    time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                } catch (IllegalArgumentException e) {
                    Log.d("FAIL_DATA", name);
                }
            }
            return bitmap;
        }


        @Override
        public void onPostExecute(Bitmap result) {
            if (result != null) {
                addBitmapToMemoryCache(id, result);
                dateCache.put(id, time == null ? "" : time);
            }
            if (viewHolder.itemView.getTag().equals(id)) {
                viewHolder.imageView.setImageBitmap(result);
                viewHolder.textView.setText(time);
                viewHolder.itemView.setOnClickListener(
                        new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               if (onItemClickListener != null) {
                                   onItemClickListener.onClick(id, uri, type);
                               }
                           }
                       }
                );
            }

        }
    }

    private void addBitmapToMemoryCache(long key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(long key) {
        return mMemoryCache.get(key);
    }
}
