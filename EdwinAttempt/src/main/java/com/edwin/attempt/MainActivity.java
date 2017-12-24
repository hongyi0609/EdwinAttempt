package com.edwin.attempt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.edwin.attempt.ui.ContentProviderActivity;
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
        findViewById(R.id.btn_provider).setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.aidl_btn:
                    startTargetActivity(BookManagerActivity.class);
                    break;
                case R.id.proxy_btn:
                    startTargetActivity(ProxyActivity.class);
                    break;
                case R.id.btn_provider:
                    startTargetActivity(ContentProviderActivity.class);
                    break;
                default:
                    break;
            }
        }
    };

    private void startTargetActivity(Class<?> cls) {
        Intent intent;
        intent = new Intent(MainActivity.this, cls);
        startActivity(intent);
    }
}
