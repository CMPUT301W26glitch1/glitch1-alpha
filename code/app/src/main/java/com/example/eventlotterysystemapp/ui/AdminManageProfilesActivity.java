package com.example.eventlotterysystemapp.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlotterysystemapp.R;
import com.example.eventlotterysystemapp.data.models.User;
import com.example.eventlotterysystemapp.data.models.UserController;

import java.util.ArrayList;
import java.util.List;

public class AdminManageProfilesActivity extends AppCompatActivity {

    private RecyclerView rvProfiles;
    private ProfileAdapter profileAdapter;
    private List<User> userList;
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_profiles);

        rvProfiles = findViewById(R.id.rvProfiles);
        rvProfiles.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        profileAdapter = new ProfileAdapter(userList);
        rvProfiles.setAdapter(profileAdapter);

        userController = new UserController(this);

        userController.getAllUsers(users -> {
            userList.clear();
            userList.addAll(users);
            profileAdapter.notifyDataSetChanged();
        });
    }
}