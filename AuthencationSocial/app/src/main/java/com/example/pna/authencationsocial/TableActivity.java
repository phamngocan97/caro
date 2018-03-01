package com.example.pna.authencationsocial;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

public class TableActivity extends AppCompatActivity {

    FrameLayout frame;
    FragmentTable fragmentTable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        init();
        Log.d("tableact","1");
        init2();
        Log.d("tableact","2");
    }

    private void init(){
        frame = findViewById(R.id.table_frame);
        fragmentTable = new FragmentTable();

    }
    private void init2(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.table_frame,fragmentTable);
        fragmentTransaction.commit();
    }
}
