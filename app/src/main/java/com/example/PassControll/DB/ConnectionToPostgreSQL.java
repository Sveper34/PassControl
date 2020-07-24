package com.example.PassControll.DB;


import android.database.Cursor;
import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class ConnectionToPostgreSQL extends AsyncTask {
    private Connection con;
    private Statement stmtListPasses;
    private Statement stmtListPassesContent;
    private Statement stmtWatch;
    private ResultSet RsListPasses;
    private ResultSet RsListPassesContent;
    private ResultSet RsWatch;
    private PreparedStatement psUpdaetInPostgreSQL;
    public String IpAdrressConection;

    @Override
    protected ResultSet[] doInBackground(Object... objects) {
        ResultSet Result[] = new ResultSet[3];
        try {         //Синхронизация информации
            //Переменная для возврата полученных данных синхронизации
            Cursor cursor = (Cursor) objects[0];
            //Production//con = DriverManager.getConnection("jdbc:postgresql://192.168.42.68:5433/dev", "user_android", "user_android");//, "plan_emp3_test", "plan_emp3_test");// проброс портов
            //Test
            //con = DriverManager.getConnection("jdbc:postgresql://"+IpAdrressConection+"/dev", "postgres", "123456789");//, "plan_emp3_test", "plan_emp3_test");// проброс портов
            con = DriverManager.getConnection("jdbc:postgresql://" + IpAdrressConection + ":5433/dev", "user_android", "user_android");//, "plan_emp3_test", "plan_emp3_test");// проброс портов
            while (cursor.moveToNext()) {
                //Внесение информации об обратной синхронизации
                psUpdaetInPostgreSQL = con.prepareStatement("update amp.list_passes set amp.list_passes.pass_in_date=?,amp.list_passes.pass_out_date=? where amp.list_passes.id=" + cursor.getString(cursor.getColumnIndex("ampp_id")));
                psUpdaetInPostgreSQL.setDate(1, Date.valueOf(cursor.getString(cursor.getColumnIndex("ampp_PASSED_IN_DATE"))));//ampp_PASSED_IN_DATE
                psUpdaetInPostgreSQL.setDate(2, Date.valueOf(cursor.getString(cursor.getColumnIndex("ampp_PASSED_OUT_DATE"))));//ampp_PASSED_OUT_DATE
                psUpdaetInPostgreSQL.execute();
            }
            // DriverManager.setLoginTimeout(1);

            //Получение информации
            stmtListPasses = con.createStatement();
            stmtListPassesContent = con.createStatement();
            stmtWatch = con.createStatement();
            RsListPasses = stmtListPasses.executeQuery("select amp.list_passes.*,amp.v_user_list.name_last||' '||amp.v_user_list.name_first ||' '||amp.v_user_list.name_middle as fio_convoy,\n" +
                    "amp.manual_car.mark||','||amp.manual_car.color||','||amp.manual_car.state_number as car \n" +
                    "from amp.list_passes \n" +
                    "left join amp.v_user_list on amp.v_user_list.id=amp.list_passes.pass_convoy \n" +
                    "left join amp.manual_car on amp.manual_car.id=amp.list_passes.manual_car_id where  amp.list_passes.pass_accept=true ");
            RsListPassesContent = stmtListPassesContent.executeQuery("select amp.list_passes_content.*,amp.system_unit.naimei as amppc_UNIT from amp.list_passes_content \n" +
                    "left join amp.system_unit on amp.system_unit.id_kodei=amp.list_passes_content.pass_kodei \n" +
                    "where  list_passes_id in (select id from amp.list_passes where  pass_accept=true);");
            RsWatch = stmtWatch.executeQuery("select * from amp.manual_watch where is_deleted=0");
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Result[0] = RsListPasses;
        Result[1] = RsListPassesContent;
        Result[2] = RsWatch;
        return Result;
    }
}

