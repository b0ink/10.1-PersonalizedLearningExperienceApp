package com.example.personalizedlearningexperience;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;

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

    private Button btnContinue;

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

        btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(view -> {
            Intent intent = new Intent(InterestsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        ArrayList<String> interests = new ArrayList<>();
        interests.add("Web Development");
        interests.add("Javascript");
        interests.add("PHP");
        interests.add("Linear Algebra");
        interests.add("Discrete Math");

        // Additional interests
        interests.add("Python");
        interests.add("Data Science");
        interests.add("AI");
        interests.add("Machine Learning");
        interests.add("Cyber Security");
        interests.add("Networks");
        interests.add("C++");
        interests.add("Cloud Computing");
        interests.add("Database Management");
        interests.add("DevOps");
        interests.add("Linux");
        interests.add("Web Design");
        interests.add("C#");
        interests.add("Software Engineering");
        interests.add("Game Dev");
        interests.add("UI/UX Design");
        interests.add("Blockchain");
        interests.add("Augmented Reality");
        interests.add("Virtual Reality");
        interests.add("Cryptography");
        interests.add("Data Mining");
        interests.add("Robotics");
        interests.add("Quantum Computing");
        interests.add("Bioinformatics");
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