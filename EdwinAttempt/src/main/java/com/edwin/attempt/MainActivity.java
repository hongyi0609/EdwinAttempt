package com.edwin.attempt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * @author hongy_000
 */
public class MainActivity extends AppCompatActivity {

    private Button buttonAidl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonAidl = findViewById(R.id.aidl_btn);
        buttonAidl.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, BookManagerActivity.class);
            startActivity(i);
        }
    };
}
