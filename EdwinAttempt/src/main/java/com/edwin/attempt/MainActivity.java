package com.edwin.attempt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.edwin.attempt.ui.ProxyActivity;

/**
 * @author hongy_000
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonAidl = findViewById(R.id.aidl_btn);
        buttonAidl.setOnClickListener(listener);
        Button buttonProxy = findViewById(R.id.proxy_btn);
        buttonProxy.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.aidl_btn:
                    intent = new Intent(MainActivity.this, BookManagerActivity.class);
                    break;
                default:
                    intent = new Intent(MainActivity.this, ProxyActivity.class);
                    break;
            }
            startActivity(intent);
        }
    };
}
