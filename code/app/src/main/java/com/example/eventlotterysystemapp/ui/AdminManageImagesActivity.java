package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlotterysystemapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminManageImagesActivity extends AppCompatActivity {

    private RecyclerView recyclerImages;
    private AdminImageAdapter adapter;
    private List<AdminImageAdapter.ImageItem> imageList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_images);

        recyclerImages = findViewById(R.id.recyclerImages);
        recyclerImages.setLayoutManager(new LinearLayoutManager(this));

        imageList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        adapter = new AdminImageAdapter(imageList, image -> removeImage(image));
        recyclerImages.setAdapter(adapter);

        loadImages();
    }

    private void loadImages() {
        db.collection("images")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    imageList.clear();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String id = doc.getId();
                        String url = doc.getString("url");
                        String uploader = doc.getString("uploader");

                        imageList.add(new AdminImageAdapter.ImageItem(
                                id,
                                url != null ? url : "",
                                uploader != null ? uploader : "Unknown uploader"
                        ));
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load images", Toast.LENGTH_SHORT).show());
    }

    private void removeImage(AdminImageAdapter.ImageItem image) {
        db.collection("images")
                .document(image.id)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Image removed", Toast.LENGTH_SHORT).show();
                    loadImages();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to remove image", Toast.LENGTH_SHORT).show());
    }
}