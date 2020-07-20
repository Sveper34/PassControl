package com.example.PassControll.DB;

import android.os.AsyncTask;

import com.example.PassControll.MainActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.transform.Result;


public class ConnectionToPostgreSQL extends AsyncTask {
    public int port;
    private Connection con;
    private Statement stmtListPasses;
    private Statement stmtListPassesContent;
    private Statement stmtWatch;
    private ResultSet RsListPasses;
    private ResultSet RsListPassesContent;
    private ResultSet RsWatch;

    @Override
    protected ResultSet[] doInBackground(Object... objects) {
        ResultSet Result[] = new ResultSet[3];
        try {
            //Production//con = DriverManager.getConnection("jdbc:postgresql://192.168.42.68:5433/dev", "user_android", "user_android");//, "plan_emp3_test", "plan_emp3_test");// проброс портов
            //Test
            //con = DriverManager.getConnection("jdbc:postgresql://192.168.42.68:5432/dev", "postgres", "123456789");//, "plan_emp3_test", "plan_emp3_test");// проброс портов
            con = DriverManager.getConnection("jdbc:postgresql://192.168.42.68:5433/dev", "user_android", "user_android");//, "plan_emp3_test", "plan_emp3_test");// проброс портов
            stmtListPasses = con.createStatement();
            stmtListPassesContent = con.createStatement();
            stmtWatch = con.createStatement();
            RsListPasses = stmtListPasses.executeQuery("select * from amp.list_passes where  pass_accept=true ");
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

