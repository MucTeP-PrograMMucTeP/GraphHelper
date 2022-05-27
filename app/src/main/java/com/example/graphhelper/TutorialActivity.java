package com.example.graphhelper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TutorialActivity extends AppCompatActivity {

    ImageButton backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);

        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
            intent.putExtra("className", "tutorial");
            startActivity(intent);
        });
    }
}
