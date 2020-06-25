package com.example.appupdatemanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.ref.SoftReference;

public class MainActivity extends AppCompatActivity {

    TextView currentversion,latestversion;
    String str_currentversion,str_latestversion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentversion = findViewById(R.id.currentversion);
        latestversion = findViewById(R.id.latestversion);

        //get latest version from play store
        new getLatestversion().execute();
    }

    private class getLatestversion extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                str_latestversion = Jsoup.connect("https://play.google.com/store/apps/details?id=com.scm.spotter")
                        .timeout(30000)
                        .get()
                        .select("div.hAyfc:nth-child(4)>"+
                                "span:nth-child(2) > div:nth-child(1)"+
                                "> span:nth-child(1)")
                        .first()
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str_latestversion;
        }

        @Override
        protected void onPostExecute(String s) {
            // get current version

            str_currentversion = BuildConfig.VERSION_NAME;

                currentversion.setText(str_currentversion);

                // set latest version on textview
                latestversion.setText(str_latestversion);

                if (latestversion != null){

                    float cversion = Float.parseFloat(str_currentversion);
                    float lversion = Float.parseFloat(str_latestversion);

                    // check condition (latest version is greater than current version)

                    if (lversion > cversion){

                        UpdateAlertDialogbox();
                    }
                }
        }
    }

    private void UpdateAlertDialogbox() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name));
        builder.setMessage("Update Available");
        builder.setCancelable(false);

        builder.setPositiveButton("update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.scm.spotter")));
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                dialog.cancel();

            }
        });

        builder.show();
    }
}
