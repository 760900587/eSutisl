package com.example.esutisl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;
import java.util.logging.Level;

public class MainActivity extends AppCompatActivity {

    private EditText password;
    private EditText lev;
    private Button buton;
    private Button up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = password.getText().toString();
                String level = lev.getText().toString();
                Map<String, Object> loagin = PasswordUtils.Loagin(Integer.parseInt(level), pass, MainActivity.this);
                Log.i("liuhongliang map", loagin.toString());
                int fist = (int) loagin.get("FIST");
                int timer = (int) loagin.get("TIMER");
//                long failure_number = (long) loagin.get("FAILURE_number");
                Log.i("liuhongliang", fist + "");
                Log.i("liuhongliang次数", timer + "");
//                Log.i("liuhongliang次数", failure_number + "");

                if (fist == 9) {
                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "首次", Toast.LENGTH_SHORT).show();
                } else if (fist == 3) {
                    Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
                }


            }
        });
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);

            }
        });
    }

    private void initView() {
        password = findViewById(R.id.pass);
        lev = findViewById(R.id.level);
        buton = findViewById(R.id.button);
        up = findViewById(R.id.up);

    }
}
