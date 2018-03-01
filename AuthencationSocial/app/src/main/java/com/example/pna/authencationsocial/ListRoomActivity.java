package com.example.pna.authencationsocial;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ListRoomActivity extends AppCompatActivity {

    String uri = "http://192.168.1.103:3000";

    ListView list_room;
    ArrayList<RowInfor> arr;
    ListRoomAdapter room_adapter;
    ImageButton btn_addRoom;
    Socket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_room);
        init();
        actionSocket();
        action();
        onSocket();
    }

    private void action(){
        btn_addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("create_room");
                startActivity(new Intent(ListRoomActivity.this,TableActivity.class));
            }
        });
        list_room.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSocket.emit("come_room",arr.get(position).getName());
            }
        });
    }
    private void actionSocket(){
        try {
            mSocket = IO.socket(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.connect();
    }
    private void onSocket(){
        mSocket.on("serverSend_list_room",recieveListRoom);
        mSocket.on("come_room_ans",comeRoom);
    }

    Emitter.Listener comeRoom = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject ob = (JSONObject) args[0];
                    try {
                        boolean is_success = ob.getBoolean("val");
                        if(is_success){
                            startActivity(new Intent(ListRoomActivity.this,TableActivity.class));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    Emitter.Listener recieveListRoom = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject ob = (JSONObject) args[0];
                    try {
                        JSONArray jsonArray = ob.getJSONArray("list");

                        arr.clear();
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject o = jsonArray.getJSONObject(i);
                            arr.add(new RowInfor(o.getInt("size"),o.getString("name")));
                        }
                        room_adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void init(){
        btn_addRoom = findViewById(R.id.btnAddRoom);
        list_room = findViewById(R.id.listRoom);
        arr = new ArrayList<>();
        room_adapter = new ListRoomAdapter(arr,getApplicationContext(),R.layout.row_room);
        list_room.setAdapter(room_adapter);
    }
}
