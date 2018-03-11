package com.example.pna.authencationsocial;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class TableActivity extends AppCompatActivity {
    final int img1 = R.drawable.x;
    final int img2 = R.drawable.o;

    TextView txtv_test;
    Button btn_signout,btn_ready;
    FrameLayout frame;
    FragmentTable fragmentTable;
    Socket mSocket;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthState;

    public static int id, idTemp;
    boolean isReady = false;
    int isEnough = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        id = bundle.getInt("id");

        init();
        init2();
        actionTable();
        onSocket();
        action();

    }

    private void action() {
        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });
        btn_ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isReady=!isReady;
                mSocket.emit("clientSend_ready",MainActivity.cur_room,id,isReady);
            }
        });
        if(id == 2){
            mSocket.emit("clientSend_enough",MainActivity.cur_room);
        }
    }


    private void init() {
        txtv_test = findViewById(R.id.table_txtvTest);
        frame = findViewById(R.id.table_frame);
        btn_signout = findViewById(R.id.btn_out);
        btn_ready = findViewById(R.id.table_btnReady);

        mSocket = MainActivity.mSocket;
        mAuth = MainActivity.mAuth;

        fragmentTable = (FragmentTable) getFragmentManager().findFragmentById(R.id.table_frag);
    }

    private void init2() {
        txtv_test.setText(""+id);

        mAuthState = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    mSocket.emit("out_room", MainActivity.cur_room);
                    Toast.makeText(TableActivity.this, MainActivity.cur_room, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(TableActivity.this, MainActivity.class));
                }
            }
        };
        mAuth.addAuthStateListener(mAuthState);
    }

    private void actionTable() {
        fragmentTable.setEnable(false);
        for (int i = 0; i < fragmentTable.n; i++) {
            for (int j = 0; j < fragmentTable.n; j++) {
                final int ii = i, jj = j;
                fragmentTable.holder[ii][jj].img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (fragmentTable.mark[ii][jj] == 0) {
                            fragmentTable.mark[ii][jj] = id;
                            if (id == 1) {
                                fragmentTable.holder[ii][jj].img.setImageResource(R.drawable.x);
                            } else {
                                fragmentTable.holder[ii][jj].img.setImageResource(R.drawable.o);
                            }


                            fragmentTable.setEnable(false);
                            mSocket.emit("send_turn", idTemp, MainActivity.cur_room, ii, jj);
                        }
                    }
                });
            }
        }
    }

    private void onSocket() {
        mSocket.on("other_user_out", other_out);
        mSocket.on("serverSend_state",getState);
        mSocket.on("sever_send_enough",getEnough);
        mSocket.on("sever_send_turn", getTurn);
    }

    Emitter.Listener getTurn = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject ob = (JSONObject) args[0];
                    try {

                        idTemp = ob.getInt("val");

//                        txtv_test.setText(idTemp);

                        int x, y, idPrev;
                        x = ob.getInt("x");
                        y = ob.getInt("y");
                        idPrev = idTemp == 1 ? 2 : 1;
                        if (x != -1 && y != -1) {
                            fragmentTable.mark[x][y] = idPrev;
                            if (idPrev == 1) fragmentTable.holder[x][y].img.setImageResource(img1);
                            else fragmentTable.holder[x][y].img.setImageResource(img2);
                        }

                        if (idTemp != id) {
                            fragmentTable.setEnable(false);
                        } else {
                            fragmentTable.setEnable(true);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    Emitter.Listener getEnough = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject ob = (JSONObject) args[0];
                    try {
                        isEnough = ob.getInt("valo");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        }
    };
    Emitter.Listener getState = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject ob = (JSONObject) args[0];
                    try {
                        boolean val = ob.getBoolean("val");
                        txtv_test.setText(isReady+" "+val +" " +isEnough);
                        if(val && isReady && isEnough == 1){
                            mSocket.emit("send_turn",2,MainActivity.cur_room,-1,-1);
                            Toast.makeText(TableActivity.this,"send ok",Toast.LENGTH_SHORT).show();
                        }
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
                    Toast.makeText(TableActivity.this, "Doi thu thoat, ban thang", Toast.LENGTH_SHORT).show();
                    id = 1;
                }
            });
        }
    };


}
