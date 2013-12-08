package com.example.shenyubao.hotfixdemo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import dalvik.system.DexClassLoader;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
    private static final String SECONDARY_DEX_NAME = "vipv2.jar";
    private static final String SECONDARY_DEX_URL = "http://2.vipmobile.sinaapp.com/vipv2.jar";
    
    private static final int BUF_SIZE = 8 * 1024;
    
    private Button mToastButton = null;
    private Button mUpdateButton = null;
    private TextView mTextResult = null;
    private ProgressDialog mProgressDialog = null;
    public LibraryInterface lib = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToastButton = (Button) findViewById(R.id.toast_button);
        mUpdateButton = (Button) findViewById(R.id.update_button);
        mTextResult = (TextView) findViewById(R.id.txvResult);
        
        lib = new LocalLibraryProvider();
        
        final File dexInternalStoragePath = new File(getDir("dex", Context.MODE_PRIVATE),SECONDARY_DEX_NAME);
  
        mToastButton.setEnabled(true);
        mToastButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				lib.setText(mTextResult);
			}
		});
        
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
			public void onClick(View view) {
                final File optimizedDexOutputPath = getDir("outdex", Context.MODE_PRIVATE);
                
                //boolean is_download = !dexInternalStoragePath.exists()
                boolean is_download = true;
                if (is_download) {
                    mProgressDialog = ProgressDialog.show(view.getContext(),
                            getResources().getString(R.string.diag_title), 
                            getResources().getString(R.string.diag_message), true, false);
                    (new PrepareDexTask()).execute(dexInternalStoragePath);
                } else {
                    mToastButton.setEnabled(true);
                }
                
                DexClassLoader cl = new DexClassLoader(dexInternalStoragePath.getAbsolutePath(),
                        optimizedDexOutputPath.getAbsolutePath(),
                        null,
                        getClassLoader());
                Class libProviderClazz = null;
                
                try {
                    libProviderClazz = cl.loadClass("com.example.shenyubao.hotfixdemo.lib.LibraryProvider");
                    lib = (LibraryInterface) libProviderClazz.newInstance();           
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }
    
    /**
     * 从网络下载自定义类的dex文件
     * @return
     */
    private InputStream downloadFile() {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpHost targetHost = new HttpHost("2.vipmobile.sinaapp.com", 80, "http");  
            HttpGet httpGet = new HttpGet(SECONDARY_DEX_URL);  
            HttpResponse response = client.execute(targetHost, httpGet);  
            int statusCode = response.getStatusLine().getStatusCode();  
            if(statusCode == HttpStatus.SC_OK) {
                InputStream is = response.getEntity().getContent();
                return is;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean prepareDex(File dexInternalStoragePath) {
        InputStream bis = null;
        OutputStream dexWriter = null;

        try {
            bis = downloadFile();
            if(bis == null) {
                return false;
            }
//            bis = new BufferedInputStream(getAssets().open(SECONDARY_DEX_NAME));
            dexWriter = new BufferedOutputStream(new FileOutputStream(dexInternalStoragePath));
            byte[] buf = new byte[BUF_SIZE];
            int len;
            while((len = bis.read(buf, 0, BUF_SIZE)) > 0) {
                dexWriter.write(buf, 0, len);
            }
            dexWriter.close();
            bis.close();
            return true;
        } catch (IOException e) {
            if (dexWriter != null) {
                try {
                    dexWriter.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            return false;
        }
    }
    
    private class PrepareDexTask extends AsyncTask<File, Void, Boolean> {

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (mProgressDialog != null) mProgressDialog.cancel();
        }

        @SuppressLint("NewApi")
		@Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (mProgressDialog != null) mProgressDialog.cancel();
        }

        @Override
        protected Boolean doInBackground(File... dexInternalStoragePaths) {
            prepareDex(dexInternalStoragePaths[0]);
            return null;
        }
    }
}
