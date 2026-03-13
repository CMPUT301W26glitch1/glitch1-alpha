package com.example.eventlotterysystemapp.data.models;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.eventlotterysystemapp.ui.UiUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for User object used to add users to firestore database
 */
public class UserController {
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private Context context;

    public UserController(Context context){
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        this.context = context;
    }

    /**
     * Adds a user to firestore database
     * @param user User object to be added
     */
    public void registerUser(User user){
        usersRef.document(user.getEmail())
                .set(user);
        UiUtils.showNotification(context, "Success", "User registered successfully;");
    }

    /**
     * Verifies whether the information for the user meets the requirements, before passing it along to be registered
     * @param user User object to be verified
     */
    public void checkUser(User user){
        if (user.getName().equals("") || user.getPassword().equals("") || user.getEmail().equals("")) {
            UiUtils.showNotification(context, "Error", "Do not leave any fields empty");
        } else {
            usersRef.whereEqualTo("email", user.getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!task.getResult().isEmpty()) {
                                    UiUtils.showNotification(context, "Error", "This email has already been registered");
                                } else {
                                    registerUser(user);
                                }
                            }
                        }
                    });
        }
    }

    public interface OnUsersLoadedListener {
        void onUsersLoaded(List<User> users);
    }

    public void getAllUsers(OnUsersLoadedListener listener) {
        usersRef.get().addOnCompleteListener(task -> {
            List<User> users = new ArrayList<>();

            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    users.add(user);
                }
            }

            listener.onUsersLoaded(users);
        });
    }
}