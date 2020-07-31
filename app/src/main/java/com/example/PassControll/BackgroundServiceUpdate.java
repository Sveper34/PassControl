package com.example.PassControll;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import com.example.PassControll.DB.ConnectionToPostgreSQL;
import com.example.PassControll.DB.DBHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

class BackgroundServiceUpdate extends Service {
    private ConnectionToPostgreSQL synchronizationPostgresql;

    private DBHelper dbHelper = new DBHelper(this);
//    final String LOG_TAG = "myLogs";


    public void onCreate() {
        super.onCreate();

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        someTask();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    void someTask() {
//    Thread threadBackground=new Thread(new Runnable() {
//        @Override
//        public void run() {
        TimerTask ts = new TimerTask() {
            @Override
            public void run() {
//                    Log.d(LOG_TAG,"привет я тут в "+System.currentTimeMillis());
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
                Cursor cursor = MainActivity.Database.rawQuery("select amp_pass.* from amp_pass ", null);
                Object[] parameters = new Object[1];
                parameters[0] = cursor;
                synchronizationPostgresql.execute(cursor);
                try {
                    //  Toast.makeText(MainActivity.this, synchronizationPostgresql.get().toString(), Toast.LENGTH_SHORT).show();
                    ResultSet[] RsListPasses = (ResultSet[]) synchronizationPostgresql.get();
                    if (RsListPasses[0] != null) {
                        dbHelper.ExecComandInDB(MainActivity.Database, "delete from amp_pass;");
                        while (RsListPasses[0].next()) {// пропуска
                            dbHelper.ExecComandInDB(MainActivity.Database, "insert into amp_pass(ampp_id,ampp_INDEX,ampp_CREATE_USER_FIO,ampp_AGREED_DATE,ampp_PLACE_FROM,ampp_PLACE_TO,ampp_ATTENDANT_FIO,ampp_TRANSPORT_INFO,ampp_type_pass,ampp_PASSED_IN_DATE,ampp_PASSED_out_DATE)" +
                                    "values(" + RsListPasses[0].getString("id") + ",'" + RsListPasses[0].getString("pass_number") + "'," +
                                    "'" + RsListPasses[0].getString("pass_create_user") + "','" + RsListPasses[0].getDate("pass_date") + "'," +
                                    "'" + RsListPasses[0].getString("pass_from") + "','" + RsListPasses[0].getString("pass_to").trim() + "'," +
                                    "'" + RsListPasses[0].getString("fio_convoy") + "','" + RsListPasses[0].getString("car") + "',"
                                    + RsListPasses[0].getInt("pass_type") + ",'" + RsListPasses[0].getDate("pass_in_date") +
                                    "','" + RsListPasses[0].getDate("pass_out_date") + "') ; ");
                        }
                    }
                    if (RsListPasses[1] != null) {
                        dbHelper.ExecComandInDB(MainActivity.Database, "delete from amp_pass_content;");
                        while (RsListPasses[1].next()) {//составы пропусков
                            dbHelper.ExecComandInDB(MainActivity.Database, "insert into amp_pass_content(amppc_id ,amppc_SYNC_DATETIME ,amppc_PASS_ID , amppc_INDEX ,amppc_TITLE  ,amppc_INVENTORY_NUMBER , amppc_AMOUNT ,amppc_UNIT)" +
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
                        dbHelper.ExecComandInDB(MainActivity.Database, "delete from amp_watch;");
                        while (RsListPasses[2].next()) {
                            dbHelper.ExecComandInDB(MainActivity.Database, "insert into amp_watch(ampw_ID,ampw_SHORT_TITLE,ampw_FULL_TITLE) values (" + RsListPasses[2].getInt("id") + ",'" +
                                    RsListPasses[2].getString("short_title") + "','" + RsListPasses[2].getString("full_title") + "');");
                        }
                    }
                    //  Toast.makeText(MainActivity.this, synchronizationPostgresql.get().toString(), Toast.LENGTH_SHORT).show();
                } catch (ExecutionException e) {
                    //Button bt=findViewById(R.id.button);
//                    e.printStackTrace();
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                } catch (SQLException e) {
//                    e.printStackTrace();
                }
            }
        };

        Timer tm = new Timer();
        tm.scheduleAtFixedRate(ts, new Date(System.currentTimeMillis()), 15000);
    }
//    });
//        threadBackground.setName("ThreadBackGraund");
//        threadBackground.start();
//    }
}
