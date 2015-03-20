package com.ugrus.gps.testclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.List;


public class MainActivity extends Activity {
    static final String CUSTOM_INTENT="org.umktrack.intent.GET_LAST_POSITION";

    static final String TRACKER_PACKAGE="org.umktrack.client";
    static final String YA_NAV_POINT_INTENT="ru.yandex.yandexnavi.action.SHOW_POINT_ON_MAP";

    private Bundle data= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void resetMessageText(View view) {
        final TextView txt = (TextView) findViewById(R.id.lbl_text);
        txt.setText("- - - - - -");
    }

    public void sendCustomIntent(View view) {
//        System.out.println("HIT OUTGOING");
        Log.d("send", "SEND OUT INTENT");
        Intent i = new Intent();
        i.setAction(CUSTOM_INTENT);
        i.setPackage(TRACKER_PACKAGE);
//        i.setClassName(TRACKER_PACKAGE,TRACKER_PACKAGE+".AutostartReceiver");
        final TextView txt = (TextView) findViewById(R.id.lbl_text);
        final Button btn_onmap = (Button) findViewById(R.id.btn_onmap);
        txt.setText("Querying data...");
        //this.sendBroadcast(i);
        //i.putExtra("guid","test");
        this.sendOrderedBroadcast(i, null, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("send", "Receive result");
                data=getResultExtras(false);
                String result;
                if( data == null){
                    result = "No any data\nact="+intent.getAction()+"\ndata="+ intent.getComponent();
                }else {
                    Date d = new Date(data.getLong("time"));
                    result = "lat=" + data.getFloat("lat") + "\n" +
                            "lng="  + data.getFloat("lng") + "\n" +
                            "time=" + data.getLong("time") + "\n" +
                            "time is " + d.toString() + "\n" +
                            "guid=" + data.getString("guid");
                    btn_onmap.setEnabled(true);
                }
                //MainActivity.this.showPointInYAN( data.getFloat("lat"), data.getFloat("lng"));
                txt.setText(result);
            }
        }, null, Activity.RESULT_OK, null, null);

    }

    public void showInYAN(View view) {
        if(data == null){
            return;
        }
        this.showPointInYAN( data.getFloat("lat"), data.getFloat("lng"));
    }

    void showPointInYAN(float lat, float lng){
        Intent intent = new Intent(YA_NAV_POINT_INTENT);
        intent.setPackage("ru.yandex.yandexnavi");
//        PackageManager pm = getPackageManager();
//        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
//
//        // Проверяем, установлен ли Яндекс.Навигатор
//        if (infos == null || infos.size() == 0) {}
        intent.putExtra("lat",lat);
        intent.putExtra("lon",lng);
        intent.putExtra("zoom", 18);
        intent.putExtra("desc","Последняя известная точка здесь");

        startActivity(intent);
    }
}
