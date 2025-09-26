package com.example.photoaivideo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LibraryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        Button btnImages = findViewById(R.id.btnImages);
        Button btnVideos = findViewById(R.id.btnVideos);

        btnImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LibraryActivity.this, ImagesLibraryActivity.class));
            }
        });

        btnVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LibraryActivity.this, VideosLibraryActivity.class));
            }
        });
    }
}
