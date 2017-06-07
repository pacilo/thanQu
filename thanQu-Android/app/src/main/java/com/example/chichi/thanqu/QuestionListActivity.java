package com.example.chichi.thanqu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chichi.thanqu.model.QuestionListAdapater;
import com.example.chichi.thanqu.model.QuestionModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.chichi.thanqu.AppProperties.QUESTIONLIST;
import static com.example.chichi.thanqu.AppProperties.ROOT;

/**
 * Created by 치치 on 2017-05-09.
 */

public class QuestionListActivity extends AppCompatActivity {

    RecyclerView rv;
    private LinearLayoutManager mLinearLayoutManager;

    RequestQueue q;
    ArrayList<QuestionModel> list;
    QuestionListAdapater adapter;

    Integer classID;

    FloatingActionButton fab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionlist);

        classID = getIntent().getIntExtra("classID", -1);
        if (classID < 0) {
            Toast.makeText(this, "강의정보 조회실패", Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else {
            AppProperties.classID = classID;
        }

        q = Volley.newRequestQueue(this);
        list = new ArrayList<>();
        adapter = new QuestionListAdapater(this, list);

        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        fab = (FloatingActionButton) findViewById(R.id.addBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QuestionListActivity.this, QuestionPostActivity.class));
            }
        });

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rv.setLayoutManager(mLinearLayoutManager);
        rv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (AppProperties.classID == null || AppProperties.classID.equals("")) {
            Toast.makeText(this, "세션이 만료되었습니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        adapter.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ROOT + QUESTIONLIST + "/" + classID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject rootObj = new JSONObject(response);
                    if (rootObj.getBoolean("success") == false) {
                        Toast.makeText(QuestionListActivity.this, "질문목록 조회실패", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONArray array = rootObj.getJSONArray("questionInfos");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = (JSONObject) array.get(i);
                        QuestionModel model = new QuestionModel();
                        model.id = obj.getInt("id");
                        model.classID = obj.getInt("classID");
                        model.userID = obj.getInt("userID");
                        model.content = obj.getString("content");
                        model.time = obj.getString("time");
                        model.commentCnt = obj.getInt("commentCnt");
                        model.likeCnt = obj.getInt("likeCnt");
                        adapter.add(model);
                    }
                    adapter.reverse();
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("QUESTIONLIST_REQ", error.toString());
            }
        });

        q.add(stringRequest);
    }
}
