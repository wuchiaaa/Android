package com.example.myalarmclock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ItemFragment extends Fragment {

    private TextView txtItem_time, txtItem_name, txtItem_date;
    private RecyclerView recycler_view;
    private LinearLayoutManager layout_manager;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        Bundle bundle = getArguments();
        String date = bundle.getString("date");
        String time = bundle.getString("time");
        String clockName = bundle.getString("clockname");

//===============未成功=======================
//        String vibrator_value = bundle.getString("vibrator_value");
//        Intent intent = new Intent(getActivity(), ClockActivity.class);//創建intent設置要啟動的組件
//        Bundle bundle2 = new Bundle();
//        bundle2.putString("vibrator_value", vibrator_value);
//        intent.putExtras(bundle2);
//        getActivity().startActivity(intent);
//================未成功======================

//        //準備資料，塞n個項目到ArrayList裡面
//        ArrayList<Post> mData = new ArrayList<>();
//        mData.add(new Post(date, time , clockName));
        txtItem_date = view.findViewById(R.id.txtItem_date);
        txtItem_time = view.findViewById(R.id.txtItem_time);
        txtItem_name = view.findViewById(R.id.txtItem_name);
        txtItem_date.setText(date);
        txtItem_time.setText(time);
        txtItem_name.setText(clockName);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        txtItem_date = view.findViewById(R.id.txtItem_date);
//        txtItem_time = view.findViewById(R.id.txtItem_time);
//        txtItem_name = view.findViewById(R.id.txtItem_name);

        MainActivity activity = (MainActivity) getActivity();
        FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();//為何final?
        MainFragment mainFragment = new MainFragment();
        transaction.add(R.id.fl_container_main, mainFragment,"main");
        transaction.commit();


    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        Bundle bundle = getArguments();
//        String date = bundle.getString("date");
//        String time = bundle.getString("time");
//        String clockName = bundle.getString("clockname");
//
////        //準備資料，塞n個項目到ArrayList裡面
////        ArrayList<Post> mData = new ArrayList<>();
////        mData.add(new Post(date, time , clockName));
//
//        txtItem_date.setText(date);
//        txtItem_time.setText(time);
//        txtItem_name.setText(clockName);
//
//
//
////        tv_name.setText("姓名:" + name);
////        tv_password.setText("密碼:" + password);
////
////
////        recycler_view = findViewById(R.id.recyclerView);
////        MyAdapter adapter = new MyAdapter(this, mData);//為數據添加配適器adapter
////
////        //layoutManager佈局管理器:設置RecyclerView為列表型態的呈現方式
////        layout_manager = new LinearLayoutManager(this);
////        recycler_view.setLayoutManager(layout_manager);
////
////        recycler_view.setAdapter(adapter);//設置adapter給recycler_view
////        recycler_view.setHasFixedSize(true);// do not change the layout size of the RecyclerView
////        recycler_view.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//設置格線
//
//    }
}
