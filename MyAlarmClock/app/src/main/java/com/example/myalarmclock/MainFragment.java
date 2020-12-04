package com.example.myalarmclock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MainFragment extends Fragment {

    private Button btn_add;


    private RecyclerView recyclerView;
//    private RecyclerView.Adapter myAdapter;
//    private RecyclerView.LayoutManager layoutManager;
    private MyAdapter myadapter;
    private ArrayList<String> mData = new ArrayList<>();


//    private String[] number = new String[]{"A", "B", "C"};
//    private String[] name = new String[]{"Amy", "Bessy", "Cathy"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_add = view.findViewById(R.id.btn_add);
        //傳值跳轉
        MainActivity activity = (MainActivity) getActivity();
        FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
        //開啟事務
        final FragmentTransaction transaction = supportFragmentManager.beginTransaction();//為何final?
        final SetClockFragment setClockFragment = new SetClockFragment();

        //NOT WORK
        if(view.findViewById(R.id.fl_container_main) != null){ //You haven't set any alarmclock.
            TextView textViewEmply = view.findViewById(R.id.textViewEmply);
            String tVempty = "You haven't set any alarmclock.";
            textViewEmply.setText(tVempty);
        }

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//跳頁
                transaction.replace(R.id.fl_container_main, setClockFragment,"set");
                transaction.commitAllowingStateLoss();//寬容錯誤
            }
        });



        // 準備資料，塞10個項目到ArrayList裡
        for(int i = 0; i < 10; i++) {
            mData.add("項目"+i);
        }
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));// 設置格線
        myadapter = new MyAdapter(mData);
        recyclerView.setAdapter(myadapter);



        //官網的
//        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_main);
//        recyclerView.setHasFixedSize(true);
//        // use a linear layout manager
//        layoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(layoutManager);
//        // specify an adapter (see also next example)
//        myAdapter = new MyAdapter(myDataset);
//        recyclerView.setAdapter(myAdapter);


        //測試數據 listview
//        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
//        for (int i = 0; i < number.length; i++) {
//            Map<String, Object> showitem = new HashMap<String, Object>();
//            showitem.put("number", number[i]);
//            showitem.put("name", name[i]);
//            listitem.add(showitem);
//        }
//        //创建一个simpleAdapter
//        SimpleAdapter myAdapter = new SimpleAdapter(getActivity(), listitem, R.layout.fragment_item, new String[]{"number", "name"}, new int[]{R.id.txtItem_time, R.id.txtItem_name});
//        ListView listView = (ListView) view.findViewById(R.id.recycle_view_main);
//        listView.setAdapter(myAdapter);
    }


}

