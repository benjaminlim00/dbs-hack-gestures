package com.example.benjamin.dbsgestures;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class RegisterGesture2 extends AppCompatActivity {
    private CanvasView canvasView;
    private Button confirmButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_gesture_2);

        canvasView = findViewById(R.id.canvas);
        confirmButton= findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearCanvas(canvasView);    //maybe dont need
                Intent i = new Intent(RegisterGesture2.this, DonePage.class);
                startActivity(i);

            }
        });


    }

    public void clearCanvas(View v) {
        canvasView.clearCanvas();
    }
}
