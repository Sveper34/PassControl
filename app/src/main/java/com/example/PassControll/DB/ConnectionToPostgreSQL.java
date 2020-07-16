package com.example.PassControll.DB;

import android.os.AsyncTask;

import com.example.PassControll.MainActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class ConnectionToPostgreSQL extends AsyncTask {
    public int port;
    private Connection con;
    private Statement stmtListPasses;
    private Statement stmtListPassesContent;
    private ResultSet RsListPasses;
//    private ResultSet RsListPassesContent;

    @Override
    protected String doInBackground(Object... objects) {
        String result = "delete * from amp_pass;";
        try {
            con = DriverManager.getConnection("jdbc:postgresql://192.168.42.235:5432/dev", "postgres", "123456789");//, "plan_emp3_test", "plan_emp3_test");// проброс портов
            stmtListPasses = con.createStatement();
            stmtListPassesContent = con.createStatement();
            RsListPasses = stmtListPasses.executeQuery("select * from amp.list_passes where  pass_accept=true");
            while (RsListPasses.next()) {
                result+="insert into amp_pass(ampp_id,ampp_INDEX,ampp_CREATE_USER_FIO,ampp_AGREED_DATE,ampp_PLACE_FROM,ampp_PLACE_TO,ampp_ATTENDANT_FIO,ampp_TRANSPORT_INFO)" +
                        "values('"+RsListPasses.getString("id")+"','"+RsListPasses.getString("pass_number")+"','','','','',)";
                //RsListPassesContent = stmtListPassesContent.executeQuery("select * from amp.list_passes_content");
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
