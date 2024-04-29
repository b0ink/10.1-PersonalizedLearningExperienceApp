package com.example.personalizedlearningexperience;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.Collections;

public class InterestsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_interests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        ArrayList<String> interests = new ArrayList<>();
        interests.add("Web Development");
        interests.add("JavaScript");
        interests.add("Mobile App Development");
        interests.add("PHP");

        // Additional interests
        interests.add("Python");
        interests.add("Data Science");
        interests.add("Artificial Intelligence");
        interests.add("Machine Learning");
        interests.add("Cyber Security");
        interests.add("Network Administration");
        interests.add("C++");
        interests.add("Cloud Computing");
        interests.add("Database Management");
        interests.add("DevOps");
        interests.add("Linux");
        interests.add("Web Design");
        interests.add("C#");
        interests.add("Software Engineering");
        interests.add("Computer Graphics");
        interests.add("Game Development");
        interests.add("UI/UX Design");
        interests.add("Big Data Analytics");
        interests.add("Blockchain Technology");
        interests.add("Augmented Reality (AR)");
        interests.add("Virtual Reality (VR)");
        interests.add("Cryptography");
        interests.add("Data Mining");
        interests.add("Robotics");
        interests.add("Embedded Systems");
        interests.add("Information Retrieval");
        interests.add("Computer Vision");
        interests.add("Quantum Computing");
        interests.add("Bioinformatics");
        interests.add("Wireless Communication");
        interests.add("Parallel Computing");

        Collections.shuffle(interests);
        RecyclerView recycler = findViewById(R.id.recyclerView);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
//        layoutManager.setFlexDirection(FlexDirection.COLUMN);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        recycler.setLayoutManager(layoutManager);

//        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        InterestsAdapter adapter = new InterestsAdapter(this, interests);
        recycler.setAdapter(adapter);
    }
}