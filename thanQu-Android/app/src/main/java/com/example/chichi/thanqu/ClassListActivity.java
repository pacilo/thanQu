package com.example.chichi.thanqu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chichi.thanqu.model.ClassListAdapter;
import com.example.chichi.thanqu.model.ClassModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.chichi.thanqu.AppProperties.CLASSLIST;
import static com.example.chichi.thanqu.AppProperties.ROOT;

/**
 * Created by 치치 on 2017-05-07.
 */

public class ClassListActivity extends AppCompatActivity {

    ArrayList<ClassModel> classList;
    ClassListAdapter adapter;
    RequestQueue q;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classlist);

        q = Volley.newRequestQueue(this);
        classList = new ArrayList<>();
        adapter = new ClassListAdapter(this, classList);

        ListView listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ClassListActivity.this, QuestionListActivity.class);
                intent.putExtra("classID", classList.get(position).id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ROOT + CLASSLIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject rootObj = new JSONObject(response);
                    if (rootObj.getBoolean("success") == false) {
                        Toast.makeText(ClassListActivity.this, "강의목록 조회실패", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONArray array = rootObj.getJSONArray("classinfos");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = (JSONObject) array.get(i);
                        ClassModel model = new ClassModel();
                        model.id = obj.getInt("id");
                        model.classCode = obj.getInt("classCode");
                        model.className = obj.getString("className");
                        model.threshold = obj.getInt("likeThreshold");
                        adapter.add(model);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("CLASSLIST_REQ", error.toString());
            }
        });

        q.add(stringRequest);
    }
}
