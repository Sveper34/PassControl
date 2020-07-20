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
    protected ResultSet[] doInBackground(Object... objects) {
        ResultSet Result[] =null;
        try {
            //con = DriverManager.getConnection("jdbc:postgresql://192.168.42.68:5433/dev", "user_android", "user_android");//, "plan_emp3_test", "plan_emp3_test");// проброс портов
            stmtListPasses = con.createStatement();
            stmtListPassesContent = con.createStatement();
            RsListPasses = stmtListPasses.executeQuery("select * from amp.list_passes where  pass_accept=true ");
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Result[0] = RsListPasses;
        return Result;
    }
}

