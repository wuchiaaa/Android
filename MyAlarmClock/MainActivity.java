package com.example.myalarmclock;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.stetho.Stetho;


public class MainActivity extends AppCompatActivity{

    public MainFragment mainFragment = new MainFragment();//實例化mainFragment
    public static SQLiteHelper mySQLiteHelper;
//    public SQLiteDatabase db;
//    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();//取得所有資料
//    ArrayList<HashMap<String, String>> getNowArray = new ArrayList<>();//取得被選中的項目資料

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this);//初始化Stetho
//        mySQLiteHelper.chickTable();//確認是否存在資料表，沒有則新增
//        arrayList = mySQLiteHelper.showAll();//撈取資料表內所有資料

        FragmentManager FManager = getSupportFragmentManager();
        FragmentTransaction FTransaction = FManager.beginTransaction();

        //把mainFragment添加到Activity中(指定位置)
        FTransaction.add(R.id.fl_container_main, mainFragment);
        FTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("MainActivity, onStart...");
//        //初始化資料庫
//        SQLiteHelper mySQLiteHelper = new SQLiteHelper(this);
//        System.out.println("MainActivity, onCreate...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("MainActivity, onResume...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("MainActivity, onDestroy...");
        mySQLiteHelper.close();
    }


}
