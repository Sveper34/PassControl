package com.example.PassControll;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.PassControll.DB.ConnectionToPostgreSQL;
import com.example.PassControll.DB.DBHelper;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import static android.os.BatteryManager.BATTERY_PLUGGED_USB;

public class MainActivity extends AppCompatActivity {

    //Приватные переменные
    private AppBarConfiguration mAppBarConfiguration;
    private BroadcastReceiver brbarCode;
    private BroadcastReceiver brCharge;
    public String type, barcode;
    //    public final static String BROADCAST_ACTION = "com.xcheng.scanner.action.BARCODE_DECODING_BROADCAST";
    public final static String BROADCAST_ACTION = "android.intent.ACTION_DECODE_DATA";
    DBHelper dbHelper = new DBHelper(this);
    public static SQLiteDatabase Database;
    public NavController navController;
    public ConnectionToPostgreSQL synchronizationPostgresql;
    public static Integer Idpass;//id пропуска
    public boolean isCharging = false;// проверка на зарядку

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Database = dbHelper.getWritableDatabase();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_Content, R.id.nav_settingsActivity, R.id.nav_allPassesContentFragment
        )
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Сканирование Штрих кодов
        brbarCode = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                type = intent.getStringExtra("EXTRA_BARCODE_DECODING_SYMBOLE");
//                barcode = intent.getStringExtra("EXTRA_BARCODE_DECODING_DATA");
//                type = intent.getStringExtra("barcode_string");
                barcode = intent.getStringExtra("barcode_string");
//                System.out.println(barcode);
                TextView tvNumberDate = (TextView) findViewById(R.id.tvNumberDate);
                TextView tvfromTo = (TextView) findViewById(R.id.tvFromTo);
                TextView tvAttendant = (TextView) findViewById(R.id.tvAttendant);
                TextView tvcar = (TextView) findViewById(R.id.tvCar);


                // Button btContent = (Button) findViewById(R.id.bOpenContent);
                Cursor cursor = Database.rawQuery("select * from amp_pass where ampp_id='" + barcode.trim() + "'", null);
                if (cursor.moveToNext()) {
                    tvNumberDate.setText("Пропуск №" + cursor.getString(cursor.getColumnIndex("ampp_INDEX")) + " от " + cursor.getString(cursor.getColumnIndex("ampp_AGREED_DATE")));
                    tvfromTo.setText(cursor.getString(cursor.getColumnIndex("ampp_PLACE_FROM")) + " / " + cursor.getString(cursor.getColumnIndex("ampp_PLACE_TO")));
                    tvAttendant.setText(cursor.getString(cursor.getColumnIndex("ampp_ATTENDANT_FIO")));
                    tvcar.setText(cursor.getString(cursor.getColumnIndex("ampp_TRANSPORT_INFO")));
                    Idpass = cursor.getInt(cursor.getColumnIndex("ampp_id"));
                } else {
                    tvNumberDate.setText("");
                    tvfromTo.setText("");
                    tvAttendant.setText("");
                    tvcar.setText("");
                    Toast.makeText(MainActivity.this, "Пропуск не найден", Toast.LENGTH_SHORT).show();
                }
            }
        };
        brCharge = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL;

                int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                boolean usbCharge = chargePlug == BATTERY_PLUGGED_USB;

                if (usbCharge & isCharging) {
                    synchronizationPostgresql = new ConnectionToPostgreSQL();
                    synchronizationPostgresql.execute();
                    try {
                        ResultSet[] RsListPasses = (ResultSet[]) synchronizationPostgresql.get();
                        if (RsListPasses[0] != null) {
                            dbHelper.ExecComandInDB(Database, "delete from amp_pass;");
                            while (RsListPasses[0].next()) {// пропуска
                                dbHelper.ExecComandInDB(Database, "insert into amp_pass(ampp_id,ampp_INDEX,ampp_CREATE_USER_FIO,ampp_AGREED_DATE,ampp_PLACE_FROM,ampp_PLACE_TO,ampp_ATTENDANT_FIO,ampp_TRANSPORT_INFO)" +
                                        "values(" + RsListPasses[0].getString("id") + ",'" + RsListPasses[0].getString("pass_number") + "'," +
                                        "'" + RsListPasses[0].getString("pass_create_user") + "','" + RsListPasses[0].getString("pass_date") + "'," +
                                        "'" + RsListPasses[0].getString("pass_from") + "','" + RsListPasses[0].getString("pass_to").trim() + "'," +
                                        "'" + RsListPasses[0].getString("pass_convoy_fio") + "','" + RsListPasses[0].getString("manual_car_id") + "') ; ");
                            }
                        }
                        if (RsListPasses[1] != null) {
                            dbHelper.ExecComandInDB(Database, "delete from amp_pass_content;");
                            while (RsListPasses[1].next()) {//составы пропусков
                                dbHelper.ExecComandInDB(Database, "insert into amp_pass_content(amppc_id ,amppc_SYNC_DATETIME ,amppc_PASS_ID , amppc_INDEX ,amppc_TITLE  ,amppc_INVENTORY_NUMBER , amppc_AMOUNT ,amppc_UNIT)" +
                                        "values (" + RsListPasses[1].getInt("id") + ",'"//amppc_id
                                        + RsListPasses[1].getString("id") + "','"//amppc_SYNC_DATETIME
                                        + RsListPasses[1].getString("list_passes_id") + "','"//amppc_PASS_ID
                                        + RsListPasses[1].getString("pass_content_index") + "','"//amppc_INDEX
                                        + RsListPasses[1].getString("pass_content_title") + "','"//amppc_TITLE
                                        + RsListPasses[1].getString("pass_content_inv_number") + "','"//amppc_INVENTORY_NUMBER
                                        + RsListPasses[1].getString("pass_count") + "','"//amppc_AMOUNT
                                        + RsListPasses[1].getString("amppc_UNIT") + "');");//amppc_UNIT
                            }
                        }
                        if (RsListPasses[2] != null) {//вахты
                            dbHelper.ExecComandInDB(Database, "delete from amp_watch;");
                            while (RsListPasses[2].next()) {
                                dbHelper.ExecComandInDB(Database, "insert into amp_watch(ampw_ID,ampw_SHORT_TITLE,ampw_FULL_TITLE) values (" + RsListPasses[2].getInt("id") + ",'" +
                                        RsListPasses[2].getString("short_title") + "','" + RsListPasses[2].getString("full_title") + "');");
                            }
                        }
                        //  Toast.makeText(MainActivity.this, synchronizationPostgresql.get().toString(), Toast.LENGTH_SHORT).show();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        //Установка на зарядку ы
        IntentFilter ifBattaryChanged = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(brCharge, ifBattaryChanged);
        //Штрих Код
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(brbarCode, intFilt);
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
        unregisterReceiver(brbarCode);
        unregisterReceiver(brCharge);
    }

    public void bOpenContentOnClick(View view) {
        navController.navigate(R.id.action_nav_home_to_nav_Content);
    }

    public void ButtonImportOnClick(View view) {
        Button bt = (Button) view;
        bt = findViewById(R.id.btImport);
        if (bt.getText().toString().equals("Ввоз ТМЦ"))
            dbHelper.UpdateAmpPassImport(Database, false);
        else dbHelper.UpdateAmpPassImport(Database, true);
        navController.navigateUp();
    }

    public void ButtonExportOnClick(View view) {
        Button bt = (Button) view;
        bt = findViewById(R.id.btExport);
        if (bt.getText().toString().equals("Вывоз ТМЦ"))
            dbHelper.UpdateAmpPassExport(Database, false);
        else dbHelper.UpdateAmpPassExport(Database, true);
        navController.navigateUp();
    }

    public void ButtonAllPassesOnClick(View view) {
//        Intent inttent = new Intent(MainActivity.this, SettingsActivity.class);
//        startActivity(inttent );
        navController.navigate(R.id.action_nav_home_to_allPassesContentFragment);
    }
}