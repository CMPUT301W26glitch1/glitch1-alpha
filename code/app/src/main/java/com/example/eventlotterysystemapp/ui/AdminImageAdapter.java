package com.example.eventlotterysystemapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlotterysystemapp.R;

import java.util.List;

public class AdminImageAdapter extends RecyclerView.Adapter<AdminImageAdapter.ImageViewHolder> {

    public interface OnRemoveClickListener {
        void onRemove(ImageItem image);
    }

    public static class ImageItem {
        public String id;
        public String url;
        public String uploader;

        public ImageItem(String id, String url, String uploader) {
            this.id = id;
            this.url = url;
            this.uploader = uploader;
        }
    }

    private List<ImageItem> imageList;
    private OnRemoveClickListener removeClickListener;

    public AdminImageAdapter(List<ImageItem> imageList, OnRemoveClickListener listener) {
        this.imageList = imageList;
        this.removeClickListener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        ImageItem image = imageList.get(position);

        holder.textUploader.setText("Uploaded by: " + image.uploader);

        holder.btnRemoveImage.setOnClickListener(v -> {
            if (removeClickListener != null) {
                removeClickListener.onRemove(image);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textUploader;
        Button btnRemoveImage;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textUploader = itemView.findViewById(R.id.textUploader);
            btnRemoveImage = itemView.findViewById(R.id.btnRemoveImage);
        }
    }
}