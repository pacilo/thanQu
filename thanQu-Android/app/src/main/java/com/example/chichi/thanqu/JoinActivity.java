package com.example.chichi.thanqu;

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

import static com.example.chichi.thanqu.AppProperties.JOIN;
import static com.example.chichi.thanqu.AppProperties.ROOT;

/**
 * Created by pacilo on 2017. 6. 7..
 */

public class JoinActivity extends AppCompatActivity {

    EditText idInput;
    EditText pwInput;
    EditText emailInput;
    Button joinBtn;

    RequestQueue q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        q = Volley.newRequestQueue(this);

        idInput = (EditText) findViewById(R.id.id);
        pwInput = (EditText) findViewById(R.id.pw);
        emailInput = (EditText) findViewById(R.id.email);
        joinBtn = (Button) findViewById(R.id.confirm);
        joinBtn.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (idInput.getText().length() < 6) {
                Toast.makeText(JoinActivity.this, "아이디는 6글자 이상 20자 이내로 해주세요", Toast.LENGTH_SHORT).show();
                return;
            } else if (pwInput.getText().length() < 8) {
                Toast.makeText(JoinActivity.this, "비밀번호는 8글자 이상 32자 이내로 해주세요", Toast.LENGTH_SHORT).show();
                return;
            } else if (emailInput.getText().length() <= 0) {
                Toast.makeText(JoinActivity.this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            StringRequest stringRequest = new StringRequest(Request.Method.POST, ROOT + JOIN, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
//                    Utility.dismissProgressDlg();
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success") == false) {
                            Toast.makeText(JoinActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(JoinActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    Utility.dismissProgressDlg();
                    Log.d("JOIN_REQ", error.toString());
                    Toast.makeText(JoinActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                    return;
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id", idInput.getText().toString());
                    params.put("pw", pwInput.getText().toString());
                    params.put("email", emailInput.getText().toString());
                    return params;
                }
            };
//            Utility.showProgressDlg(JoinActivity.this);
            q.add(stringRequest);
        }
    };
}
