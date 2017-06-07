package com.example.chichi.thanqu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chichi.thanqu.model.CommentListAdapter;
import com.example.chichi.thanqu.model.CommentModel;
import com.example.chichi.thanqu.model.QuestionDetailModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.chichi.thanqu.AppProperties.ADDLIKE;
import static com.example.chichi.thanqu.AppProperties.COMMENTLIST;
import static com.example.chichi.thanqu.AppProperties.MAKECOMMENT;
import static com.example.chichi.thanqu.AppProperties.QUESTIONDETAIL;
import static com.example.chichi.thanqu.AppProperties.REMOVELIKE;
import static com.example.chichi.thanqu.AppProperties.ROOT;

/**
 * Created by pacilo on 2017. 6. 6..
 */

public class QuestionDetailActivity extends AppCompatActivity {

    RequestQueue q;
    Integer questionID;

    TextView content;
    TextView time;
    TextView commentCnt;
    TextView likeCnt;

    ListView commentListView;
    ArrayList<CommentModel> commentlist;
    CommentListAdapter adapter;

    EditText editText;
    Button sendBtn;

    StringRequest stringRequest;
    StringRequest commentRequest;
    Boolean didLike;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_question);

        questionID = getIntent().getIntExtra("questionID", -1);
        if (questionID < 0) {
            Toast.makeText(this, "질문정보 조회실패", Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else {
            AppProperties.questionID = questionID;
        }

        content = (TextView) findViewById(R.id.content);
        time = (TextView) findViewById(R.id.time);
        commentCnt = (TextView) findViewById(R.id.commentCnt);
        likeCnt = (TextView) findViewById(R.id.likeCnt);
        likeCnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (didLike) { // DELETE
                    StringRequest disLikeReq = new StringRequest(Request.Method.PUT, ROOT + REMOVELIKE, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                if (obj.getBoolean("success") == false) {
                                    return;
                                } else {
                                    q.add(stringRequest);
//                                    q.add(commentRequest);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("DISLIKE_REQ", error.toString());
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("questionID", AppProperties.questionID.toString());
                            params.put("userID", AppProperties.userID.toString());
                            return params;
                        }
                    };
                    q.add(disLikeReq);
                } else { // PUT
                    StringRequest addLikeReq = new StringRequest(Request.Method.PUT, ROOT + ADDLIKE, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                if (obj.getBoolean("success") == false) {
                                    return;
                                } else {
                                    q.add(stringRequest);
//                                    q.add(commentRequest);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("LIKE_REQ", error.toString());
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("questionID", AppProperties.questionID.toString());
                            params.put("userID", AppProperties.userID.toString());
                            return params;
                        }
                    };
                    q.add(addLikeReq);
                }
            }
        });
        commentListView = (ListView) findViewById(R.id.commentList);

        commentlist = new ArrayList<>();
        adapter = new CommentListAdapter(this, commentlist);
        commentListView.setAdapter(adapter);

        editText = (EditText) findViewById(R.id.input);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest addCommentRequest = new StringRequest(Request.Method.POST, ROOT + MAKECOMMENT, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("success") == false) {
                                Toast.makeText(QuestionDetailActivity.this, "댓글등록 실패", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                editText.setText("");
                                q.add(stringRequest);
//                                q.add(commentRequest);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(QuestionDetailActivity.this, "댓글등록 성공", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ADDCOMMENT_REQ", error.toString());
                        Toast.makeText(QuestionDetailActivity.this, "댓글등록 실패", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("questionID", AppProperties.questionID.toString());
                        params.put("userID", AppProperties.userID.toString());
                        params.put("content", editText.getText().toString());
                        return params;
                    }
                };
                q.add(addCommentRequest);
            }
        });

        q = Volley.newRequestQueue(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (AppProperties.questionID == null || AppProperties.questionID.equals("")) {
            finish();
            return;
        }

        didLike = false;

        final QuestionDetailModel model = new QuestionDetailModel();
        stringRequest = new StringRequest(Request.Method.GET, ROOT + QUESTIONDETAIL + "/" + questionID + "/" + AppProperties.userID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject rootObj = new JSONObject(response);
                    if (rootObj.getBoolean("success") == false) {
                        Toast.makeText(QuestionDetailActivity.this, "질문목록 조회실패", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONArray array = rootObj.getJSONArray("info");
                    JSONObject obj = (JSONObject) array.get(0);
                    model.id = obj.getInt("id");
                    model.classID = obj.getInt("classID");
                    model.userID = obj.getInt("userID");
                    model.content = obj.getString("content");
                    model.time = obj.getString("time");
                    model.commentCnt = obj.getInt("commentCnt");
                    model.likeCnt = obj.getInt("likeCnt");
                    model.didLike = rootObj.getBoolean("didLike");
                    if (!isFinishing()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                didLike = model.didLike;
                                content.setText(model.content);
                                time.setText(model.time);
                                commentCnt.setText("덧글 " + model.commentCnt.toString());
                                likeCnt.setText("공감 " + model.likeCnt.toString());
                            }
                        });
                    }
                    q.add(commentRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("QUESTIONDETAIL_REQ", error.toString());
            }
        });

        commentRequest = new StringRequest(Request.Method.GET, ROOT + COMMENTLIST + "/" + questionID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject rootObj = new JSONObject(response);
                    if (rootObj.getBoolean("success") == false) {
                        Toast.makeText(QuestionDetailActivity.this, "댓글목록 조회실패", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    adapter.clear();
                    JSONArray array = rootObj.getJSONArray("infos");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = (JSONObject) array.get(i);
                        CommentModel m = new CommentModel();
                        m.id = obj.getInt("id");
                        m.questionID = obj.getInt("questionID");
                        m.userID = obj.getInt("userID");
                        m.content = obj.getString("content");
                        m.time = obj.getString("time");
                        adapter.add(m);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("COMMENTLIST_REQ", error.toString());
            }
        });

        q.add(stringRequest);
//        q.add(commentRequest);
    }
}
