package com.example.pna.authencationsocial;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class TableActivity extends AppCompatActivity {

    Button btn_signout;
    FrameLayout frame;
    FragmentTable fragmentTable;
    Socket mSocket;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        init();
        init2();
        action();
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
    }

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
        fragmentTable = new FragmentTable();
        mSocket = MainActivity.mSocket;
        mAuth = MainActivity.mAuth;
    }
    private void init2(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.table_frame,fragmentTable);
        fragmentTransaction.commit();

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


}
