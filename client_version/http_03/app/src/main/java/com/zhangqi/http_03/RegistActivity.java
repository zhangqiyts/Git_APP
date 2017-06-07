package com.zhangqi.http_03;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by zhangqi on 2016/10/3.
 */
public class RegistActivity extends Activity {
    private EditText name;
    private EditText password;
    private Button login;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist);
        name = (EditText)findViewById(R.id.et_name);
        password = (EditText)findViewById(R.id.et_pass);
        login = (Button)findViewById((R.id.btn_login));
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://222.28.84.108:8080/web/MyServlet"; //本地ip不能写localhost或者127.0.0.1，需要写本机的真实ip
                new HttpThreadLogin(url, name.getText().toString(), password.getText().toString()).start();
            }
        });
    }
}
