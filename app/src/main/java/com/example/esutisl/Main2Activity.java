package com.example.esutisl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

public class Main2Activity extends AppCompatActivity {

    private EditText pass;
    private EditText lev;
    private Button butt;
    private EditText old;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
        initData();
    }

    private void initData() {
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = pass.getText().toString();
                String level = lev.getText().toString();
                String oldpass = old.getText().toString();
                Map<String, Object> map = PasswordUtils.set_Password(Integer.parseInt(level), password, oldpass, Main2Activity.this);
                int fist = (int) map.get("FIST");
                int timer = (int) map.get("TIMER");
                Log.i("liuhongliang",timer+"失败");
                Log.i("liuhongliang", fist + "");
                if (fist == 9) {
                    Toast.makeText(Main2Activity.this, "首次修改", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (fist == 3) {
                    Toast.makeText(Main2Activity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Main2Activity.this, "修改失败", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void initView() {
        pass = findViewById(R.id.pass);
        lev = findViewById(R.id.level);
        butt = findViewById(R.id.button);
        old = findViewById(R.id.oldpass);
    }
}
