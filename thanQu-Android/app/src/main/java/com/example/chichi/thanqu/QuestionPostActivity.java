package com.example.chichi.thanqu;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

import static com.example.chichi.thanqu.AppProperties.MAKEQUESTION;
import static com.example.chichi.thanqu.AppProperties.ROOT;
import static com.example.chichi.thanqu.AppProperties.classID;
import static com.example.chichi.thanqu.AppProperties.userID;

/**
 * Created by pacilo on 2017. 6. 7..
 */
public class QuestionPostActivity extends AppCompatActivity {

    EditText edit;
    Button btn;

    RequestQueue q;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postquestion);

        q = Volley.newRequestQueue(this);

        edit = (EditText) findViewById(R.id.edit);
        btn = (Button) findViewById(R.id.confirm);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edit.getText().toString().length() < 6 || edit.getText().toString().length() > 32) {
                    Toast.makeText(QuestionPostActivity.this, "질문은 6글자 이상 32자 이내로 해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                StringRequest stringRequest = new StringRequest(Request.Method.POST, ROOT + MAKEQUESTION, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Utility.dismissProgressDlg();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("success") == false) {
                                Toast.makeText(QuestionPostActivity.this, "질문등록 실패", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(QuestionPostActivity.this, "질문등록 성공", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Utility.dismissProgressDlg();
                        Log.d("ADD_QUESTION_REQ", error.toString());
                        Toast.makeText(QuestionPostActivity.this, "질문등록 실패", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("classID", AppProperties.classID.toString());
                        params.put("userID", AppProperties.userID.toString());
                        params.put("content", edit.getText().toString());
                        return params;
                    }
                };
//                Utility.showProgressDlg(QuestionPostActivity.this);
                q.add(stringRequest);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (classID == null || classID.equals("") || userID == null || userID.equals("")) {
            Toast.makeText(this, "세션이 만료되었습니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }
}
