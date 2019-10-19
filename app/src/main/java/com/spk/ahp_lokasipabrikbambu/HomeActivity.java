package com.spk.ahp_lokasipabrikbambu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.spk.ahp_lokasipabrikbambu.input.KriteriaActivity;

public class HomeActivity extends AppCompatActivity {

    public static final String user = "names";

    Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String user = getIntent().getStringExtra("names");

        initStartButton();
    }

    private void initStartButton() {
        startBtn = findViewById(R.id.start_btn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchKriteriaScreen();
            }
        });
    }

    private void launchKriteriaScreen() {
        Intent intent = new Intent(this, KriteriaActivity.class);
        startActivity(intent);
    }
}
