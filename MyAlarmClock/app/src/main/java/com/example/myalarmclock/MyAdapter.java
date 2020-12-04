package com.example.myalarmclock;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

//    private ArrayList<Post> mData;
//    private String[] mData;
    public Switch openNClose;


    private List<String> mData;
    MyAdapter(List<String> data) {//傳入資料清單
        mData = data;
    }
//    public static void Post(String[] args){
//        String[] number = {"A", "B", "C"};
//        String[] name = {"Amy", "Bessy", "Cathy"};
//        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
//        for (int i = 0; i < number.length; i++) {
//            Map<String, Object> showitem = new HashMap<String, Object>();
//            showitem.put("number", number[i]);
//            showitem.put("name", name[i]);
//            listitem.add(showitem);
//        }
//    }


    //建立MyViewHolder，定義每個item的介面與邏輯
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView txtItem_time;
        public TextView txtItem_name;
        public TextView txtItem_date;

        public MyViewHolder(@NonNull TextView v) {
            super(v);
            txtItem_time = (TextView) v.findViewById(R.id.txtItem_time);;
        }

    }

    //傳入資料清單
    // Provide a suitable constructor (depends on the kind of dataset)
//    public MyAdapter(String[] myDataset){
//        this.mData = myDataset;
//    }





    //調用R.layout建立View，以此建立一個新的ViewHolder
    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //連接項目佈局檔fragment_item.xml
        TextView view = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false);
        MyViewHolder myviewHolder = new MyViewHolder(view);
//        myviewHolder.txtItem_date = (TextView) view.findViewById(R.id.txtItem_date);
//        myviewHolder.txtItem_time = (TextView) view.findViewById(R.id.txtItem_time);
//        myviewHolder.txtItem_name = (TextView) view.findViewById(R.id.txtItem_name);
        return myviewHolder;
    }

    // 透過position找到data，以此設置MyViewHolder裡的View
    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
//        Post post = mData.get(position);
//        holder.txtItem_date.setText(post.clockDate);
//        holder.txtItem_time.setText(post.clockTime);
//        holder.txtItem_name.setText(post.clockName);
//        holder.setData(mData[0]);
        holder.txtItem_time.setText(mData.get(position));
    }

    // 返回item個數
    @Override
    public int getItemCount() {
//        return 0;
        return mData.size();
    }


}
