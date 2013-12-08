package com.example.shenyubao.hotfixdemo.lib;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shenyubao.hotfixdemo.LibraryInterface;

public class LibraryProvider implements LibraryInterface {
    public void setText(TextView textview) {
        if (textview == null) {
            return;
        }
        textview.setText("Now is vip V2.0");
        textview.setVisibility(View.VISIBLE);
    }
}
