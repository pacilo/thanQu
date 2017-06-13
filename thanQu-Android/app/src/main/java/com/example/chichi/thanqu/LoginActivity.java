package com.example.chichi.thanqu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.chichi.thanqu.AppProperties.LOGIN;
import static com.example.chichi.thanqu.AppProperties.ROOT;

/**
 * Created by pacilo on 2017. 6. 7..
 */
public class LoginActivity extends AppCompatActivity {

    EditText idInput;
    EditText pwInput;
    Button joinBtn;
    Button loginBtn;

    RequestQueue q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        q = Volley.newRequestQueue(this);

        idInput = (EditText) findViewById(R.id.id);
        pwInput = (EditText) findViewById(R.id.pw);
        joinBtn = (Button) findViewById(R.id.join);
        loginBtn = (Button) findViewById(R.id.login);
        joinBtn.setOnClickListener(listener);
        loginBtn.setOnClickListener(listener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.join:
                    startActivity(new Intent(LoginActivity.this, JoinActivity.class));
                    break;
                case R.id.login:
                    if (idInput.getText().length() <= 0) {
                        Toast.makeText(LoginActivity.this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (pwInput.getText().length() <= 0) {
                        Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, ROOT + LOGIN, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            Utility.dismissProgressDlg();
                            try {
                                JSONObject obj = new JSONObject(response);
                                if (obj.getBoolean("success") == false) {
                                    Toast.makeText(LoginActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    AppProperties.userID = obj.getInt("userID");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            Utility.dismissProgressDlg();
                            Log.d("LOGIN_REQ", error.toString());
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("id", idInput.getText().toString());
                            params.put("pw", pwInput.getText().toString());
                            return params;
                        }
                    };

//                    Utility.showProgressDlg(LoginActivity.this);
                    q.add(stringRequest);
                    break;
            }
        }
    };
}
