package com.example.herve.toolbarview.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.herve.toolbarview.R;

public class EmptyActivity extends AppCompatActivity {
    private Button btn_goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        btn_goBack = (Button) findViewById(R.id.btn_goBack);

        btn_goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EmptyActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
