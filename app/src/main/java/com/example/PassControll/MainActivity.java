package com.example.PassControll;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import com.example.PassControll.DB.ConnectionToOracle;
import com.example.PassControll.DB.DBHelper;
import com.google.android.material.navigation.NavigationView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    private AppBarConfiguration mAppBarConfiguration;
    private BroadcastReceiver br;
    public String type, barcode;
    public final static String BROADCAST_ACTION = "com.xcheng.scanner.action.BARCODE_DECODING_BROADCAST";
    DBHelper dbHelper = new DBHelper(this);
    public SQLiteDatabase Database;
    public NavController navController;
    public ConnectionToOracle synchronizationOracle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Database = dbHelper.getWritableDatabase();
        synchronizationOracle = new ConnectionToOracle();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_Content
        )
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                type = intent.getStringExtra("EXTRA_BARCODE_DECODING_SYMBOLE");
                barcode = intent.getStringExtra("EXTRA_BARCODE_DECODING_DATA");
                TextView tvNumberDate = (TextView) findViewById(R.id.tvNumberDate);
                TextView tvfromTo = (TextView) findViewById(R.id.tvFromTo);
                TextView tvAttendant = (TextView) findViewById(R.id.tvAttendant);
                TextView tvcar = (TextView) findViewById(R.id.tvCar);
                TextView tvIdpass = (TextView) findViewById(R.id.tvIdPass);
                // Button btContent = (Button) findViewById(R.id.bOpenContent);
                Cursor cursor = Database.rawQuery("select * from amp_pass where ampp_index='" + barcode.trim() + "'", null);
                if (cursor.moveToNext()) {
                    tvNumberDate.setText(cursor.getString(cursor.getColumnIndex("ampp_INDEX")) + " / " + cursor.getString(cursor.getColumnIndex("ampp_AGREED_DATE")));
                    tvfromTo.setText(cursor.getString(cursor.getColumnIndex("ampp_PLACE_FROM")) + " / " + cursor.getString(cursor.getColumnIndex("ampp_PLACE_TO")));
                    tvAttendant.setText(cursor.getString(cursor.getColumnIndex("ampp_ATTENDANT_FIO")));
                    tvcar.setText(cursor.getString(cursor.getColumnIndex("ampp_TRANSPORT_INFO")));
                    tvIdpass.setText(cursor.getString(cursor.getColumnIndex("ampp_id")));
                } else {
                    tvNumberDate.setText("");
                    tvfromTo.setText("");
                    tvAttendant.setText("");
                    tvcar.setText("");
                    Toast.makeText(MainActivity.this, "Пропуск не найден", Toast.LENGTH_SHORT).show();
                }
            }


        };
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(br, intFilt);
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                //Insert code for SQL Sync here
                System.out.println("ХАЙ ХАЙ ХАЙ ХАЙ ХАЙ ЙХА ЙХА ЙХАЙХ А ТАЙМЕР");
                synchronizationOracle.execute();//Класс для заполнения Внутренний бд
                System.out.println("ХАЙ ХАЙ ХАЙ ХАЙ ХАЙ ЙХА ЙХА ЙХАЙХ А ТАЙМЕР");

            }
        };
        Timer tm = new Timer();
        tm.schedule(tt, 600000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override // для выключения приложения
    protected void onStop() {
        MainActivity.super.onStop();
        unregisterReceiver(br);
    }

    public void bOpenContentOnClick(View view) {
        navController.navigate(R.id.action_nav_home_to_nav_Content);
    }
}