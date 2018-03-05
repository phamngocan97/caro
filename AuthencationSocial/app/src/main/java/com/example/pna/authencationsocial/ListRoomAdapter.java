package com.example.pna.authencationsocial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by PNA on 27/02/2018.
 */

public class ListRoomAdapter extends BaseAdapter {
    ArrayList<RowInfor> arr;
    Context context;
    int layout;

    public ListRoomAdapter(ArrayList<RowInfor> arr, Context context, int layout) {
        this.arr = arr;
        this.context = context;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class Holder{
        TextView txtv_name,txtv_cnt;
        ImageButton img_btn_go;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_room,null);

            holder = new Holder();
            holder.txtv_name = convertView.findViewById(R.id.txtv_name);
            holder.txtv_cnt = convertView.findViewById(R.id.txtv_cnt);
            holder.img_btn_go = convertView.findViewById(R.id.imgBtnGo);


            holder.img_btn_go.setFocusable(false);
            convertView.setTag(holder);

        }else{
            holder = (Holder) convertView.getTag();
        }

        holder.txtv_name.setText("Phòng: "+ arr.get(position).getName());
        holder.txtv_cnt.setText("Số lượng" + arr.get(position).getCnt()+"");

        return convertView;
    }
}
