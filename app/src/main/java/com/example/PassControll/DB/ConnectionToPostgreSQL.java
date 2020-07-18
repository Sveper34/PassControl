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
    private ResultSet RsListPasses;
//    private ResultSet RsListPassesContent;

    @Override
    protected ResultSet doInBackground(Object... objects) {
        ResultSet Result =null;

        String result = "delete from amp_pass;  ";
        try {
            con = DriverManager.getConnection("jdbc:postgresql://192.168.42.235:5432/dev", "user_amp", "user_amp");//, "plan_emp3_test", "plan_emp3_test");// проброс портов
            stmtListPasses = con.createStatement();
            stmtListPassesContent = con.createStatement();
            RsListPasses = stmtListPasses.executeQuery("select * from amp.list_passes where  pass_accept=true  order by pass_number limit 1");
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Result = RsListPasses;
        return Result;
    }
}

