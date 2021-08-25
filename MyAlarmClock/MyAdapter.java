package com.example.myalarmclock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> { // implements  View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener

    private Context context;
    private List<Post> clockList;//實體類資料ArrayList

    public MyAdapter(Context context, List<Post> clockList) {
        this.context = context;
        this.clockList = clockList;
    }

    //建立MyViewHolder，定義每個item的介面與邏輯
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txtItem_time;
        public TextView txtItem_name;
        public TextView txtItem_date;
        private Switch switch_openNClose;//被動的顯示鬧鐘啟用或停用

        public MyViewHolder(@NonNull View v) {
            super(v);
            txtItem_time = (TextView) v.findViewById(R.id.txtItem_time);
            txtItem_name = (TextView) v.findViewById(R.id.txtItem_name);
            txtItem_date = (TextView) v.findViewById(R.id.txtItem_date);
            switch_openNClose = (Switch) v.findViewById(R.id.openNClose);

            //將創建的View註冊點擊事件-->Item和Item內的控件點擊處理-->理解錯誤
//            itemView.setOnClickListener(MyAdapter.this);
//            switch_openNClose.setOnClickListener(MyAdapter.this);
//            itemView.setOnLongClickListener(MyAdapter.this);

        }
    }

    //調用R.layout建立View，以此建立一個新的ViewHolder
    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //自定義佈局，連接項目佈局檔fragment_item.xml
        View view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false);
        MyViewHolder myviewHolder = new MyViewHolder(view);
        return myviewHolder;
    }

    // 將資料與介面進行繫結的操作
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyAdapter.MyViewHolder holder, final int position) {
        // 因為 ViewHolder 會重複使用，要在這個 function 依據 position 把正確的資料跟 ViewHolder 綁定在一起。

        //設定Tag-->Item和Item內的控件點擊處理-->理解錯誤
//        MyViewHolder myViewHolder = (MyViewHolder) holder;
//        myViewHolder.itemView.setTag(position);
//        myViewHolder.switch_openNClose.setTag(position);

        final Post post = clockList.get(position);

        String clockHour, clockMinute;
        if (post.getClockHour() < 10) clockHour = "0" + post.getClockHour();
        else clockHour = "" + post.getClockHour();
        if (post.getClockMinute() < 10) clockMinute = "0" + post.getClockMinute();
        else clockMinute = "" + post.getClockMinute();
        holder.txtItem_time.setText(clockHour + ":" + clockMinute);//獲取實體類中的ClockTime欄位並設定
        holder.txtItem_name.setText(post.getClockName());

        //12.29 鬧鐘設置重複
        int repeat = post.getRepeat();
        if(repeat == 0){
            int month = post.getClockMonth();//值1-12
            holder.txtItem_date.setText(post.getClockYear() + "年" + month + "月" + post.getClockDay() + "日");
        }else if(repeat == 1){
            ArrayList<String> weeks = new ArrayList<String>();
            int sun = post.getRepeatSun();
            int mon = post.getRepeatMon();
            int tue = post.getRepeatTue();
            int wed = post.getRepeatWed();
            int thu = post.getRepeatThu();
            int fri = post.getRepeatFri();
            int sat = post.getRepeatSat();
            if(sun == 1) weeks.add("日");
            if(mon == 1) weeks.add("一");
            if(tue == 1) weeks.add("二");
            if(wed == 1) weeks.add("三");
            if(thu == 1) weeks.add("四");
            if(fri == 1) weeks.add("五");
            if(sat == 1) weeks.add("六");
            StringBuilder weekList = new StringBuilder("每週  ");
            for(int i=0; i<weeks.size(); i++){
                weekList.append(weeks.get(i));
            }
            if(weeks.size() == 7) holder.txtItem_date.setText("每天");
            else holder.txtItem_date.setText(weekList.toString());
            Log.d("MyAdapter", "weeks:"+weeks);
        }


        //每次MainFragment被onViewCreated的時候重新抓取資料庫狀態
        int clockEnable = post.getClockEnable();
        if(clockEnable == 1) {
            holder.switch_openNClose.setChecked(true);//預設打開鬧鐘
            //需註冊?
        }else if(clockEnable == 0){
            holder.switch_openNClose.setChecked(false);//關閉鬧鐘
            holder.txtItem_time.setTextColor(0xFF858585);
            holder.txtItem_date.setTextColor(0xFF858585);
            holder.txtItem_name.setTextColor(0xFF858585);
            //取消註冊? clockId
        }


        /*
         * item點擊事件
         * */
        if(onItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //此處回傳點選監聽事件
                    onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
                    Log.d("MyAdapterPosition", "鬧鐘的position位置:" + holder.getAdapterPosition());//0、1、2等
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getAdapterPosition();
                    onItemClickListener.OnItemLongClick(v, position);
                    Log.d("MyAdapterPosition", "長按鬧鐘的position位置:" + position);
                    return false;
                }
            });
        }

        holder.switch_openNClose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = (int) holder.getAdapterPosition();
                onItemClickListener.onCheckedChanged(buttonView, position, isChecked);
                Log.d("MyAdapterPosition", "鬧鐘switch的position位置:" + position);
            }
        });

    }

    // 回傳整個 Adapter 包含幾筆資料。
    @Override
    public int getItemCount() {
        return clockList != null ? clockList.size() : 0;
    }


////////////////////////////以下為item點擊處理-->Item和Item內的控件點擊處理-->理解錯誤 ///////////////////////////////

//    private OnRecyclerViewItemClickListener onItemClickListener = null;
//
//    public void setOnItemClickListener(OnRecyclerViewItemClickListener onItemClickListener) {
//        this.onItemClickListener = onItemClickListener;
//    }

    private OnItemClickListener onItemClickListener;

    //需要外部訪問，所以需要設定set方法，方便呼叫
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /** item裏面有多個控件可以點擊-->Item和Item內的控件點擊處理-->理解錯誤 */
    public enum ViewName {
        ITEM,
        OPEN
    }

    /**
     * 設定item的監聽事件的介面
     */
//    public interface OnRecyclerViewItemClickListener {
//        void onClick(View view, ViewName viewName, int position);
//        void onItemLongClick(View view, int position);
//
//        void onCheckedChanged(View view, int position);
//    }
    public interface OnItemClickListener {
        /**
         * 介面中的點選每一項的實現方法，引數自己定義
         *
         * @param view 點選的item的檢視
//         * @param data 點選的item的資料
         * @param position 點選的item的位置
         */
//        void OnItemClick(View view, Post data);
        void OnItemClick(View view, int position);
        void OnItemLongClick(View view, int position);
        void onCheckedChanged(CompoundButton buttonView, int position, boolean isChecked);
    }


    //-->Item和Item內的控件點擊處理-->理解錯誤
//    @Override
//    public void onClick(View v) { //此處回傳點選監聽事件
//        //使用getTag方法獲取數據
//        int position = (int) v.getTag();
//        if(onItemClickListener != null){
//            switch(v.getId()){
//                case R.id.openNClose:
//                    onItemClickListener.onClick(v, ViewName.OPEN, position);
//                    Log.d("MyAdapterPosition", "@@鬧鐘switch的position位置:" + position);
//                    break;
//                default:
//                    onItemClickListener.onClick(v, ViewName.ITEM, position);
//                    Log.d("MyAdapterPosition", "@@鬧鐘的position位置:" + position);
//                    break;
//            }
//        }
//    }


//    @Override
//    public boolean onLongClick(View v) { //此處回傳點選監聽事件
//        int position = (int) v.getTag();
//        onItemClickListener.onItemLongClick(v, position);
//        Log.d("MyAdapterPosition", "@@長按鬧鐘的position位置:" + position);
//        return false;
//    }

}
