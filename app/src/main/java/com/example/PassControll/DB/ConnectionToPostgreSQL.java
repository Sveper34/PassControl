package com.example.PassControll.DB;


import android.database.Cursor;
import android.os.AsyncTask;

import java.sql.Connection;
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
        Cursor cursor = (Cursor) objects[0];
        try {         //Синхронизация информации
            //Переменная для возврата полученных данных синхронизации

            //Production//con = DriverManager.getConnection("jdbc:postgresql://192.168.42.68:5433/dev", "user_android", "user_android");//, "plan_emp3_test", "plan_emp3_test");// проброс портов
            //Test
            //con = DriverManager.getConnection("jdbc:postgresql://"+IpAdrressConection+"/dev", "postgres", "123456789");//, "plan_emp3_test", "plan_emp3_test");// проброс портов
            //con = DriverManager.getConnection("jdbc:postgresql://srv-postgres-4.arktika.spo:5432/dev", "user_android", "user_android");//, "plan_emp3_test", "plan_emp3_test");// проброс портов
            con = DriverManager.getConnection("jdbc:postgresql://" + IpAdrressConection + ":15432/dev", "user_android", "user_android");//, "plan_emp3_test", "plan_emp3_test");// проброс портов
            //con=DriverManager.getConnection("jdbc:postgresql://192.168.42.154:15432/dev", "postgres", "123456789");
            while (cursor.moveToNext()) {
                //Внесение информации об обратной синхронизации
                psUpdaetInPostgreSQL = con.prepareStatement("update amp.list_passes set" +
                        " pass_in_date=" + GetstrWithForging(cursor.getString(cursor.getColumnIndex("ampp_PASSED_IN_DATE"))) +" ,"+
                        " pass_out_date=" + GetstrWithForging(cursor.getString(cursor.getColumnIndex("ampp_PASSED_OUT_DATE"))) +", "+
                        " pass_watch_in=" +cursor.getInt(cursor.getColumnIndex("ampp_PASSED_IN_CONTROL_POINT_ID"))+", "+
                        " pass_watch_out="+cursor.getInt(cursor.getColumnIndex("ampp_PASSED_OUT_CONTROL_POINT_ID"))+"  "+
                        " where amp.list_passes.id=" + cursor.getString(cursor.getColumnIndex("ampp_id")));
                System.out.println(psUpdaetInPostgreSQL.toString());
                psUpdaetInPostgreSQL.execute();
            }
            //Получение информации
            stmtListPasses = con.createStatement();
            stmtListPassesContent = con.createStatement();
            stmtWatch = con.createStatement();
            RsListPasses = stmtListPasses.executeQuery("select amp.list_passes.*,amp.v_user_list.name_last||' '||amp.v_user_list.name_first ||' '||amp.v_user_list.name_middle as fio_convoy\n" +
                    "                , amp.manual_car.mark||','||amp.manual_car.color||','||amp.manual_car.state_number as car \n" +
                    "                    from amp.list_passes " +
                    "                    left join amp.v_user_list on amp.v_user_list.id=amp.list_passes.pass_convoy \n" +
                    "                    left join amp.manual_car on amp.manual_car.id=amp.list_passes.manual_car_id where  amp.list_passes.pass_accept=true \n" +
                    "                    and ( " +
                    "                    case when amp.list_passes.pass_type=1 and  amp.list_passes.pass_in_date is null then 1 \n" +
                    "                    when amp.list_passes.pass_type=0 and  amp.list_passes.pass_out_date is null  then 1\n" +
                    "                    when amp.list_passes.pass_type=2 and (amp.list_passes.pass_in_date is null or amp.list_passes.pass_out_date is null) then 1 \n" +
                    "                    else 0 " +
                    "                 end )=1;");
            RsListPassesContent = stmtListPassesContent.executeQuery("select amp.list_passes_content.*,amp.system_unit.naimei as amppc_UNIT from amp.list_passes_content \n" +
                    "left join amp.system_unit on amp.system_unit.id_kodei=amp.list_passes_content.pass_kodei \n" +
                    "where  list_passes_id in (select id from amp.list_passes where  pass_accept=true);");
            RsWatch = stmtWatch.executeQuery("select * from amp.manual_watch where is_deleted=0");
            Result[0] = RsListPasses;
            Result[1] = RsListPassesContent;
            Result[2] = RsWatch;
            con.close();
        } catch (SQLException e) {
//            e.printStackTrace();
        }


        return Result;
    }

    private String GetstrWithForging(String str) {
        if (!str.equals("null")) str = "'" + str + "'";
        return str;
    }
//    private String GetDateRussiaFormat(String str) {
//        System.out.println(str);
//        String tmp=str;
//        if (!str.equals("null"))
//            tmp = str.substring(8, 10) + "." + str.substring(5, 7) + "." + str.substring(0, 4);
//        System.out.println(tmp);
//        return tmp;
//    }
}

