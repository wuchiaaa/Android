package com.example.myalarmclock;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager FManager = getSupportFragmentManager();
        FragmentTransaction FTransaction = FManager.beginTransaction();

        //實例化mainFragment
        mainFragment = new MainFragment();
        //把mainFragment添加到Activity中(指定位置)
        FTransaction.add(R.id.fl_container_main, mainFragment);
        FTransaction.commit();

    }
}
