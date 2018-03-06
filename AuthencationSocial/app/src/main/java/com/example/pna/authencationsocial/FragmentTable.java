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
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

/**
 * Created by PNA on 28/02/2018.
 */
class Holder{
    ImageButton img;
}
public class FragmentTable extends Fragment{
    View view;
    public TableLayout table;
    HorizontalScrollView horizal;
    ScrollView scrollView;
    public final int n=50;
    public int[][] mark;
    public Holder[][] holder;
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
        init2();
        createTabale();
        moveView();

        return view;
    }
    public void createTabale(){
        for(int i=0;i<n;i++){
            TableRow row = new TableRow(getActivity());
            for(int j=0;j<n;j++){
                final int ii=i,jj=j;
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.item,null);
                holder[i][j].img = view.findViewById(R.id.item_img);

                row.addView(view);
            }
            table.addView(row);
        }
    }

    public void init() {
        table = view.findViewById(R.id.fragTable);
        horizal = view.findViewById(R.id.horizal);
        scrollView = view.findViewById(R.id.scroll);
        scaleGestureDetector = new ScaleGestureDetector(getActivity(),new MyScale(getActivity()));
        mark=new int[n][n];
        holder=new Holder[n][n];
    }
    public void init2(){
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                mark[i][j]=0;
                holder[i][j]=new Holder();
            }
        }
    }

    private void moveView(){
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0,scrollView.getChildAt(0).getBottom()/2);
                horizal.scrollTo(horizal.getChildAt(0).getRight()/2,0);
            }
        });
    }
    public void reset(){
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                mark[i][j]=0;
                holder[i][j].img.setImageResource(0);
            }
        }
    }

    public void setEnable(boolean val){
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                holder[i][j].img.setEnabled(val);
            }
        }
    }
    public boolean test(int x, int y, int k) {
        int col = y, row = x;
        int doc, ngang, cc, cp;
        doc = ngang = cc = cp = 0;

        for (int i = -4; i <= 4; i++) {
            if (col + i < n && col + i >= 0) {
                if (mark[row][col + i] == k) ngang++;
                else if (ngang < 5) ngang = 0;
            }
            if (row + i < n && row + i >= 0) {
                if (mark[row + i][col] == k) doc++;
                else if (doc < 5) doc = 0;
            }
            if (row + i < n && row + i >= 0 && col + i >= 0 && col + i < n) {
                if (mark[row + i][col + i] == k) cc++;
                else if (cc < 5) cc = 0;
            }
            if (row + i < n && row + i >= 0 && col - i < n && col - i >= 0) {
                if (mark[row + i][col - i] == k) cp++;
                else if (cp < 5) cp = 0;
            }
        }
        if (ngang >= 5 || doc >= 5 || cc >= 5 || cp >= 5) return true;
        return false;
    }

}

/**  Note: If you are passing argument in fragment then don't use below code always replace fragment instance where we had set bundle as argument as we had done above else it will give exception  **/
//   fragmentManager.beginTransaction().replace(R.id.fragmentContainer, new ArgumentFragment()).commit();