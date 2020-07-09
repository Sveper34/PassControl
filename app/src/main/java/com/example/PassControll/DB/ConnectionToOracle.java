package com.example.PassControll.DB;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.transform.Result;

public class ConnectionToOracle extends AsyncTask {
    public int port;

    @Override
    protected String doInBackground(Object[] objects) {
        String result = "";
        try {
                Connection con = DriverManager.getConnection("jdbc:oracle:thin:@192.168.42.140:1522:orcl", "system", "123456789");//, "plan_emp3_test", "plan_emp3_test");// проброс портов
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("select * from dual");
                con.close();
                while(rs.next()){
                    result=rs.getString("dummy");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        return  result;
    }
}
