package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventlotterysystemapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import com.example.eventlotterysystemapp.ui.AccessibilityUtils;

public class AdminManageImagesActivity extends AppCompatActivity {

    private RecyclerView recyclerImages;
    private FirebaseFirestore db;
    private List<QueryDocumentSnapshot> imageList = new ArrayList<>();
    private ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_images);
        AccessibilityUtils.applyAccessibilityMode(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Manage Images");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        recyclerImages = findViewById(R.id.recyclerImages);
        recyclerImages.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ImageAdapter();
        recyclerImages.setAdapter(adapter);

        loadImages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImages();
    }

    private void loadImages() {
        db.collection("images")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    imageList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        imageList.add(doc);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading images: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteImage(String documentId) {
        db.collection("images")
                .document(documentId)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Image removed successfully", Toast.LENGTH_SHORT).show();
                    loadImages();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error removing image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admin_image, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {
            QueryDocumentSnapshot doc = imageList.get(position);

            String uploader = doc.getString("uploader");
            String url = doc.getString("url");

            holder.tvUploader.setText(uploader != null ? uploader : "Unknown uploader");
            holder.tvUrl.setText(url != null ? url : "No URL");

            Glide.with(holder.itemView.getContext()).clear(holder.imagePreview);
            if (url != null && !url.isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(url)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(holder.imagePreview);
            } else {
                holder.imagePreview.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            holder.btnDelete.setOnClickListener(v -> deleteImage(doc.getId()));
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }

        class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imagePreview;
            TextView tvUploader, tvUrl;
            ImageButton btnDelete;

            ImageViewHolder(View itemView) {
                super(itemView);
                imagePreview = itemView.findViewById(R.id.imagePreview);
                tvUploader = itemView.findViewById(R.id.tvUploader);
                tvUrl = itemView.findViewById(R.id.tvUrl);
                btnDelete = itemView.findViewById(R.id.btnDeleteImage);
            }
        }
    }
}