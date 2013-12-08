package com.example.shenyubao.hotfixdemo;

import android.content.Context;
import android.widget.Toast;

public class LocalLibraryProvider implements LibraryInterface{
    public void showAwesomeToast(Context context) {
        if (context == null) {
            return;
        }
        Toast.makeText(context,"Now is old vip",Toast.LENGTH_LONG).show();

    }
}
