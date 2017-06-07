package com.example.chichi.thanqu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import static com.example.chichi.thanqu.AppProperties.LOGOUT;
import static com.example.chichi.thanqu.AppProperties.ROOT;

public class MainActivity extends AppCompatActivity {
    RequestQueue q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        q = Volley.newRequestQueue(this);

        Button button = (Button) findViewById(R.id.classbutton);
        Button button1 = (Button) findViewById(R.id.logout);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ClassListActivity.class));
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, ROOT + LOGOUT, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppProperties.userID = null;
                        Toast.makeText(MainActivity.this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("LOGOUT_REQ", error.toString());
                    }
                });

                q.add(stringRequest);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppProperties.userID == null || AppProperties.userID.equals("")) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
