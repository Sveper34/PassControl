package com.example.PassControll.DB;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionToOracle extends AsyncTask {
    @Override
    protected String doInBackground(Object[] objects) {
        String result = "";
//        try {
        //DriverManager.registerDriver ("new oracle.jdbc.OracleDriver()");
        //Class.forName("oracle.jdbc.OracleDriver");   //jdbc:oracle:thin:@192.168.42.240:1522
//            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@192.168.42.20:1522:test", "plan_emp3_test", "plan_emp3_test"); // проброс портов
//            Statement stmt = con.createStatement();
//            ResultSet rs = stmt.executeQuery("select * from dual");
//            con.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return e.toString();

//        }
        return result;

    }
}
