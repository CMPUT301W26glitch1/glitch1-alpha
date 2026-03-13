package com.example.eventlotterysystemapp.data.models;

import static com.example.eventlotterysystemapp.ui.UiUtils.showNotification;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.eventlotterysystemapp.ui.UiUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserController {
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private UiUtils uiUtils;
    private Context context;

    public UserController(Context context){
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        this.context= context;
    }
    public interface UserCallback {
        void onSuccess();
        void onError(String message);

    }


    public void registerUser(User user){
        usersRef.document()
                .set(user);
       // UiUtils.showNotification(context, "Success", "User registered successfully;");
    }

    // ✅ Updated checkUser() with callback
    public void checkUser(User user, UserCallback callback){
        if (user.getName().equals("") || user.getPassword().equals("") || user.getEmail().equals("")) {
            callback.onError("Do not leave any fields empty");
        } else {
            usersRef.whereEqualTo("email", user.getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!task.getResult().isEmpty()) {
                                    callback.onError("This email has already been registered");
                                } else {
                                    registerUser(user);
                                    callback.onSuccess();
                                }
                            } else {
                                callback.onError("Error accessing database");
                            }
                        }
                    });
        }
    }
}