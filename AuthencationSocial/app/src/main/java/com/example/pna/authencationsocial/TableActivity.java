package com.example.pna.authencationsocial;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class TableActivity extends AppCompatActivity {

    Button btn_signout;
    FrameLayout frame;
    FragmentTable fragmentTable;
    Socket mSocket;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthState;

    public static int id,idTemp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        init();
        action();
        init2();
        actionTable();
        onSocket();

    }
    private void action(){
        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });

    }
    private void onSocket(){
        mSocket.on("other_user_out",other_out);
        mSocket.on("sever_send_id",getId);
        mSocket.on("sever_send_enough",enough);
        mSocket.on("sever_send_turn",getTurn);
    }

    Emitter.Listener getTurn = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject ob = (JSONObject) args[0];
                    try {
                        idTemp =  ob.getInt("val");
                        if(idTemp!=id){
                            fragmentTable.table.setEnabled(false);
                        }else{
                            fragmentTable.table.setEnabled(true);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    Emitter.Listener enough = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSocket.emit("send_turn",2,MainActivity.cur_room);
                }
            });
        }
    };
    Emitter.Listener getId = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject ob = (JSONObject) args[0];
                    try {
                        id = ob.getInt("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    Emitter.Listener other_out = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(TableActivity.this,"Doi thu thoat, ban thang",Toast.LENGTH_SHORT).show();
                }
            });
        }
    };


    private void init(){
        frame = findViewById(R.id.table_frame);
        btn_signout = findViewById(R.id.btn_out);
        mSocket = MainActivity.mSocket;
        mAuth = MainActivity.mAuth;

        fragmentTable = (FragmentTable) getFragmentManager().findFragmentById(R.id.table_frag);
    }
    private void init2(){


        mAuthState = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user==null){
                    mSocket.emit("out_room",MainActivity.cur_room);
                    Toast.makeText(TableActivity.this,MainActivity.cur_room,Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(TableActivity.this,MainActivity.class));
                }
            }
        };
        mAuth.addAuthStateListener(mAuthState);
    }
    private void actionTable(){
        for(int i =0;i<fragmentTable.n;i++){
            for(int j=0;j<fragmentTable.n;j++){
                final int ii=i,jj=j;
                fragmentTable.holder[ii][jj].img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(fragmentTable.mark[ii][jj]==0){
                            fragmentTable.mark[ii][jj]=id;
                            if(id==1){
                                fragmentTable.holder[ii][jj].img.setImageResource(R.drawable.x);
                            }else{
                                fragmentTable.holder[ii][jj].img.setImageResource(R.drawable.o);
                            }
                        }
                    }
                });
            }
        }
    }
}
