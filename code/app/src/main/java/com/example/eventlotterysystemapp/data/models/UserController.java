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
/**
 * UserController handles all the Firestore operations related to Users.
 * This includes registering a new user and checking if a user already exists.
 * Basically: talk to Firebase so we don't have duplicate emails and users get saved.
 */
public class UserController {
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private UiUtils uiUtils;
    private Context context;
    /**
     * Constructor for UserController
     * @param context the app context, used for Toasts or notifications
     */
    public UserController(Context context){
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        this.context= context;
    }
    /**
     * Callback interface to notify caller of success or failure of user operations.
     */
    public interface UserCallback {
        void onSuccess();
        void onError(String message);

    }

    /**
     * Register a new user in Firestore.
     * @param user the User object to save
     * Note: this method does not do any validation — assumes the caller already checked that.
     */

    public void registerUser(User user){
        usersRef.document()
                .set(user);
       // UiUtils.showNotification(context, "Success", "User registered successfully;");
    }

    /**
     * Check if a user can be registered (all fields filled and email not already taken).
     * If everything is good, calls registerUser.
     * @param user the User object to check and possibly register
     * @param callback callback interface to handle success or errors
     */
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