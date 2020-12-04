/* 此作廢~
* 未能成功:Fragment + AlarmManager 的使用
* 目前原因:AlarmManager 需要用 Activity 才能實現
* */
package com.example.myalarmclock;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MyAlertDialogFragment extends DialogFragment {

    private MediaPlayer mediaPlayer;

    //在該實例中傳入顯示的內容(message)
    public static MyAlertDialogFragment newInstance(String message){
        MyAlertDialogFragment frag = new MyAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.music);
        mediaPlayer.start();
        String message = getArguments().getString("message");//獲取參數

//        AlertDialog.Builder timeDialog = new AlertDialog.Builder(getActivity());//使用AlertBuilder創建新的對話框
//        // 配置對話框UI
//        timeDialog.setTitle(title);
//        //返回配置完成的對話框
//        return timeDialog.create();

        final MyAlertDialogFragment myAlertDialogFragment = new MyAlertDialogFragment();

        MainActivity activity = (MainActivity) getActivity();
        FragmentManager supportFragmentManager = activity.getSupportFragmentManager();//傳值跳轉
        final FragmentTransaction transaction = supportFragmentManager.beginTransaction();//開啟事務
        transaction.add(R.id.fl_container_main, myAlertDialogFragment, "add");


        return new AlertDialog.Builder(getActivity()).setTitle("鬧鐘").setMessage(message).setPositiveButton("關閉鬧鈴", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//按鈕點選的監聽程式
                mediaPlayer.stop();
//                ((FragmentAlertDialog)getActivity()).doPositiveClick();
                transaction.hide(myAlertDialogFragment);
//                getActivity().finish();
            }
        }).create();

    }
}
