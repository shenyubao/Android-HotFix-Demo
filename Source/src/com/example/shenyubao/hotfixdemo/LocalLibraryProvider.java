package com.example.shenyubao.hotfixdemo;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class LocalLibraryProvider implements LibraryInterface{
    public void setText(TextView textview) {
        if (textview == null) {
            return;
        }
        textview.setText("Now is vip V1.0");
        textview.setVisibility(View.VISIBLE);
    }
}
