package com.example.pna.authencationsocial;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

/**
 * Created by PNA on 28/02/2018.
 */

public class FragmentTable extends Fragment{
    View view;
    TableLayout table;
    HorizontalScrollView horizal;
    ScrollView scrollView;
    int n=50;

    ScaleGestureDetector scaleGestureDetector;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_frag_table, container,false);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);
                return true;
            }
        });

        init();
        createTabale();

        return view;
    }
    private void createTabale(){
        for(int i=1;i<=n;i++){
            TableRow row = new TableRow(getActivity());
            for(int j=1;j<=n;j++){
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.item,null);
                row.addView(view);
            }
            table.addView(row);
        }
    }

    private void init() {
        table = view.findViewById(R.id.fragTable);
        horizal = view.findViewById(R.id.horizal);
        scrollView = view.findViewById(R.id.scroll);
        scaleGestureDetector = new ScaleGestureDetector(getActivity(),new MyScale(getActivity()));
    }
}
