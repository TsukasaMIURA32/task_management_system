package task_management_system.com.task_management.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//DB接続クラス
public class DBCon {
 // データベースへの接続情報
 private static final String JDBC_URL = "jdbc:mysql://localhost:3306/task_management_db";
 private static final String USER     = "root";
 private static final String PASSWORD = "";
//JDBCドライバを追加しよう
 private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

//データベースのコネクション作成
 public static Connection getConnection() {
     try {
         //ドライバのロードを追加しよう
     	  Class.forName(DRIVER);
         return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
     } catch (SQLException e) {
         System.err.println("Connection failed: " + e.getMessage());
         return null;
     }  catch (ClassNotFoundException e) {
		        e.printStackTrace();
		        return null;
		    }
 }
}