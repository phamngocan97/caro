package com.example.pna.authencationsocial;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
    Button btn_ready;
    ImageButton btn_signout, btn_prev;
    FrameLayout frame;
    FragmentTable fragmentTable;
    Socket mSocket;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthState;

    public static int id, idTemp = -1;
    int k = 0;
    boolean isReady = false;
    boolean isPlaying = false;
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
                isReady = !isReady;
                if (isReady) btn_ready.setBackgroundResource(R.drawable.ready_press);
                if (!isReady) btn_ready.setBackgroundResource(R.drawable.ready_unpress);
                mSocket.emit("clientSend_ready", MainActivity.cur_room, id, isReady);
            }
        });
        if (id == 2) {
            mSocket.emit("clientSend_enough", MainActivity.cur_room);
        }
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("out_room", MainActivity.cur_room);
                MainActivity.cur_room = "-1";
                startActivity(new Intent(TableActivity.this, ListRoomActivity.class));
            }
        });
    }


    private void init() {
        txtv_test = findViewById(R.id.table_txtvTest);
        frame = findViewById(R.id.table_frame);
        btn_signout = findViewById(R.id.btn_out);
        btn_prev = findViewById(R.id.btn_prev);
        btn_ready = findViewById(R.id.table_btnReady);

        mSocket = MainActivity.mSocket;
        mAuth = MainActivity.mAuth;

        fragmentTable = (FragmentTable) getFragmentManager().findFragmentById(R.id.table_frag);
    }

    private void init2() {
        txtv_test.setText("Phong: " + MainActivity.cur_room);

        mAuthState = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    mSocket.emit("out_room", MainActivity.cur_room);
                    MainActivity.cur_room = "-1";
                  //  Toast.makeText(TableActivity.this, MainActivity.cur_room, Toast.LENGTH_SHORT).show();
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
                        if (fragmentTable.mark[ii][jj] == 0 && idTemp == id) {
                            fragmentTable.mark[ii][jj] = id;
                            if (id == 1) {
                                fragmentTable.holder[ii][jj].img.setImageResource(R.drawable.x);
                            } else {
                                fragmentTable.holder[ii][jj].img.setImageResource(R.drawable.o);
                            }
                            //Toast.makeText(TableActivity.this,"k: " + k,Toast.LENGTH_SHORT).show();
                            //k++;
                            //fragmentTable.setEnable(false);
                            idTemp = -1;
                            if (fragmentTable.test(ii, jj, id)) {
                                mSocket.emit("client_winner", MainActivity.cur_room, id);
                            } else {
                                int val_send;
                                val_send = id == 1 ? 2 : 1;
                                mSocket.emit("send_turn", val_send, MainActivity.cur_room, ii, jj);
                            }

                        }
                    }
                });
            }
        }
    }

    private void onSocket() {
        mSocket.on("other_user_out", other_out);
        mSocket.on("serverSend_state", getState);
        mSocket.on("sever_send_enough", getEnough);
        mSocket.on("severSend_playing", getPlaying);
        mSocket.on("sever_send_turn", getTurn);
        mSocket.on("serverSend_winner", getWinner);
    }

    Emitter.Listener getWinner = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject ob = (JSONObject) args[0];
                    try {
                        int t = ob.getInt("id");
                        AlertDialog.Builder alert = new AlertDialog.Builder(TableActivity.this);
                        alert.setCancelable(false);
                        if (t == id) {
                            alert.setMessage("Win");
                        } else {
                            alert.setMessage("Lose");
                        }
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fragmentTable.reset();
                            }
                        });
                        btn_ready.setVisibility(View.VISIBLE);
                        alert.show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    Emitter.Listener getTurn = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject ob = (JSONObject) args[0];
                    try {

                        idTemp = ob.getInt("val");
           //             Toast.makeText(TableActivity.this, "id temp: " + idTemp, Toast.LENGTH_SHORT).show();

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
                        if (val && isReady && isEnough == 1) {
                            mSocket.emit("send_turn", 1, MainActivity.cur_room, -1, -1);
                            mSocket.emit("clientSend_playing", MainActivity.cur_room);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    Emitter.Listener getPlaying = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_ready.setBackgroundResource(R.drawable.ready_unpress);
                    btn_ready.setVisibility(View.GONE);
                    isPlaying = true;
                    isReady = false;
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
                    if (isPlaying) {
                        Toast.makeText(TableActivity.this, "Doi thu thoat, ban thang", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder alert = new AlertDialog.Builder(TableActivity.this);
                        alert.setCancelable(false);
                        alert.setMessage("Doi thu thoat, ban thang");
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fragmentTable.reset();
                            }
                        });
                        alert.show();
                        idTemp = -1;
                        fragmentTable.setEnable(false);
                    }
                    btn_ready.setVisibility(View.VISIBLE);
                    isEnough = -1;
                    id = 1;
                }
            });
        }
    };

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


    }

}
