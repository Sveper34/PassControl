package com.example.PassControll;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.BatteryManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.PassControll.DB.ConnectionToPostgreSQL;
import com.example.PassControll.DB.DBHelper;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import static android.os.BatteryManager.BATTERY_PLUGGED_USB;

public class MainActivity extends AppCompatActivity {

    //Приватные переменные
    private AppBarConfiguration mAppBarConfiguration;
    private BroadcastReceiver brbarCode;//Прием широковешательных сообщений от сканирование штрихкода
    private BroadcastReceiver brCharge;//Прием широковешательных сообщений зарядки устройства
    public String barcode; //Штрих код
    private BackgroundServiceUpdate backgroundService;//Сервис фонового обновления данных
    //    public final static String BROADCAST_ACTION = "com.xcheng.scanner.action.BARCODE_DECODING_BROADCAST";
    public final static String BROADCAST_ACTION = "android.intent.ACTION_DECODE_DATA";//Широковешательное сообщение для сканера Urovo dt40
    public DBHelper dbHelper = new DBHelper(this); // Класс для работы с базой данных
    public static SQLiteDatabase Database; //База данных
    public NavController navController;// Боковое меню приложения
    public ConnectionToPostgreSQL synchronizationPostgresql; //Класс для синхронизации данных с postgresql
    public static Integer Idpass;//id пропуска
    public boolean isCharging = false;// проверка на зарядку
    public int FlagOpen;
    public ArrayAdapter<String> adapterWatch;

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
                R.id.nav_home, R.id.nav_Content, R.id.nav_allPassesContentFragment, R.id.nav_settingsFragment
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
//                if(!isForeground(context)){
//                    Intent ActivityMaintoStart = new Intent("android.intent.action.MAIN");
//                    context.startActivity(ActivityMaintoStart);
//                }
//                type = intent.getStringExtra("EXTRA_BARCODE_DECODING_SYMBOLE");
//                barcode = intent.getStringExtra("EXTRA_BARCODE_DECODING_DATA");
//                type = intent.getStringExtra("barcode_string");
                barcode = intent.getStringExtra("barcode_string");
                TextView tvNumberDate = (TextView) findViewById(R.id.tvNumberDate);
                TextView tvfromTo = (TextView) findViewById(R.id.tvFromTo);
                TextView tvAttendant = (TextView) findViewById(R.id.tvAttendant);
                TextView tvcar = (TextView) findViewById(R.id.tvCar);
                Button btContent = (Button) findViewById(R.id.bOpenContent);
                if (tvNumberDate != null) {
                    Cursor cursor = Database.rawQuery("select * from amp_pass where ampp_id='" + barcode.trim() + "'", null);
                    if (cursor.moveToNext()) {

                        String StrDate = cursor.getString(cursor.getColumnIndex("ampp_AGREED_DATE"));
                        StrDate = StrDate.substring(8, 10) + "." + StrDate.substring(5, 7) + "." + StrDate.substring(0, 4);//Преобразование даты путем обрезания строки

                        //Создание костомизированного TextView текста
                        SpannableString NewUnderLineString = new SpannableString("Пропуск №" + cursor.getString(cursor.getColumnIndex("ampp_INDEX")) + " от " + StrDate);
                        NewUnderLineString.setSpan(new UnderlineSpan(), 9, 9 + cursor.getString(cursor.getColumnIndex("ampp_INDEX")).length(), 0);
                        NewUnderLineString.setSpan(new UnderlineSpan(), NewUnderLineString.length() - StrDate.length(), NewUnderLineString.length(), 0);
                        //Полностью сформированную строку закидываем на отображение
                        //Строка с номером пропуска и датой пропуска
                        tvNumberDate.setText(NewUnderLineString);//"Пропуск №" + cursor.getString(cursor.getColumnIndex("ampp_INDEX")) + " от " + StrDate);
                        //формирование строки Откуда Куда)
                        NewUnderLineString = new SpannableString("Откуда: " + cursor.getString(cursor.getColumnIndex("ampp_PLACE_FROM")) + ". Куда: " + cursor.getString(cursor.getColumnIndex("ampp_PLACE_TO")));
                        NewUnderLineString.setSpan(new UnderlineSpan(), 8, 8 + cursor.getString(cursor.getColumnIndex("ampp_PLACE_FROM")).length(), 0);
                        NewUnderLineString.setSpan(new UnderlineSpan(), NewUnderLineString.length() - cursor.getString(cursor.getColumnIndex("ampp_PLACE_TO")).length(), NewUnderLineString.length(), 0);
                        tvfromTo.setText(NewUnderLineString);
                        //Формирование сопровождающего
                        NewUnderLineString = new SpannableString("Сопровождающий: " + cursor.getString(cursor.getColumnIndex("ampp_ATTENDANT_FIO")));
                        NewUnderLineString.setSpan(new UnderlineSpan(), NewUnderLineString.length() - cursor.getString(cursor.getColumnIndex("ampp_ATTENDANT_FIO")).length(), NewUnderLineString.length(), 0);
                        tvAttendant.setText(NewUnderLineString);
                        //формирование автомобиль
                        if (!cursor.getString(cursor.getColumnIndex("ampp_TRANSPORT_INFO")).equals("null")) {
                            NewUnderLineString = new SpannableString("Автомобиль: " + cursor.getString(cursor.getColumnIndex("ampp_TRANSPORT_INFO")));
                            NewUnderLineString.setSpan(new UnderlineSpan(), NewUnderLineString.length() - cursor.getString(cursor.getColumnIndex("ampp_TRANSPORT_INFO")).length(), NewUnderLineString.length(), 0);
                            tvcar.setText(NewUnderLineString);
                        }
                        else tvcar.setText("");

                        btContent.setVisibility(View.VISIBLE);
                        Idpass = cursor.getInt(cursor.getColumnIndex("ampp_id"));
                    } else {
                        tvNumberDate.setText("");
                        tvfromTo.setText("");
                        tvAttendant.setText("");
                        tvcar.setText("");
                        btContent.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, "Пропуск не найден", Toast.LENGTH_SHORT).show();
                    }
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
                Button bt = findViewById(R.id.bOpenContent);
                if (usbCharge & isCharging) {
                    synchronizationPostgresql = new ConnectionToPostgreSQL();
                    //
                    String ipAddrtrash, ipAddr = "";
                    Process processGetIppaddr;
                    try {
                        processGetIppaddr = Runtime.getRuntime().exec("ip n");
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(processGetIppaddr.getInputStream()));
                        while ((ipAddrtrash = br.readLine()) != null) {
                            if (ipAddrtrash.indexOf("FAILED") == -1)
                                ipAddr = ipAddrtrash.substring(0, ipAddrtrash.indexOf("dev") - 1);
                        }
                        processGetIppaddr.waitFor();
                        processGetIppaddr.destroy();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    synchronizationPostgresql.IpAdrressConection = ipAddr;
                    Cursor cursor = Database.rawQuery("select amp_pass.* from amp_pass ", null);
                    Object[] parameters = new Object[1];
                    parameters[0] = cursor;
                    synchronizationPostgresql.execute(cursor);
                    try {
                        //  Toast.makeText(MainActivity.this, synchronizationPostgresql.get().toString(), Toast.LENGTH_SHORT).show();
                        ResultSet[] RsListPasses = (ResultSet[]) synchronizationPostgresql.get();
                        if (RsListPasses[0] != null) {
                            dbHelper.ExecComandInDB(Database, "delete from amp_pass;");
                            while (RsListPasses[0].next()) {// пропуска
                                dbHelper.ExecComandInDB(Database, "insert into amp_pass(ampp_id,ampp_INDEX,ampp_CREATE_USER_FIO,ampp_AGREED_DATE,ampp_PLACE_FROM,ampp_PLACE_TO,ampp_ATTENDANT_FIO,ampp_TRANSPORT_INFO,ampp_type_pass,ampp_PASSED_IN_DATE,ampp_PASSED_out_DATE ,ampp_PASSED_IN_CONTROL_POINT_ID,ampp_PASSED_OUT_CONTROL_POINT_ID)" +
                                        "values(" + RsListPasses[0].getString("id") + ",'" + RsListPasses[0].getString("pass_number") + "'," +
                                        "'" + RsListPasses[0].getString("pass_create_user") + "','" + RsListPasses[0].getDate("pass_date") + "'," +
                                        "'" + RsListPasses[0].getString("pass_from") + "','" + RsListPasses[0].getString("pass_to").trim() + "'," +
                                        "'" + RsListPasses[0].getString("fio_convoy") + "','" + RsListPasses[0].getString("car") + "',"
                                        + RsListPasses[0].getInt("pass_type") + ",'" + RsListPasses[0].getDate("pass_in_date") +
                                        "','" + RsListPasses[0].getDate("pass_out_date") + "'," + RsListPasses[0].getInt("pass_watch_in") + "," + RsListPasses[0].getInt("pass_watch_out") + ") ; ");
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
                        //Button bt=findViewById(R.id.button);
//                        e.printStackTrace();
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                    } catch (SQLException e) {
//                        e.printStackTrace();
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

        backgroundService = new BackgroundServiceUpdate();
        backgroundService.onCreate();
        backgroundService.someTask();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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
        unregisterReceiver(brbarCode);
        unregisterReceiver(brCharge);
        MainActivity.super.onStop();
    }

    public void bOpenContentOnClick(View view) {
        FlagOpen = 2;
        navController.navigate(R.id.action_nav_home_to_nav_Content);
    }

    public void ButtonImportOnClick(View view) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Button bt = (Button) view;
        bt = findViewById(R.id.btImport);
        if (bt.getText().toString().toLowerCase().equals("ввоз тмц") || bt.getText().toString().toLowerCase().equals("внос тмц"))
            dbHelper.UpdateAmpPassImport(Database, false, settings.getInt("ANDROID_SYNC_WATCH", 0));
        //else dbHelper.UpdateAmpPassImport(Database, true,settings.getInt("ANDROID_SYNC_WATCH", 0));
        navController.navigateUp();
    }

    public void ButtonExportOnClick(View view) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Button bt = (Button) view;
        bt = findViewById(R.id.btExport);
        if (bt.getText().toString().toLowerCase().equals("вывоз тмц") || bt.getText().toString().toLowerCase().equals("вынос тмц"))
            dbHelper.UpdateAmpPassExport(Database, false, settings.getInt("ANDROID_SYNC_WATCH", 0));
        // else dbHelper.UpdateAmpPassExport(Database, true,settings.getInt("ANDROID_SYNC_WATCH", 0));
        navController.navigateUp();
    }

}