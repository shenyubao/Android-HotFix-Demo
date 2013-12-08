package com.example.shenyubao.hotfixdemo.lib;

import android.content.Context;
import android.widget.Toast;

import com.example.shenyubao.hotfixdemo.LibraryInterface;

public class LibraryProvider implements LibraryInterface {
    public void showAwesomeToast(Context context) {
        if (context == null) {
            return;
        }
        Toast.makeText(context,"Now is new vip",Toast.LENGTH_LONG).show();
    }
}
