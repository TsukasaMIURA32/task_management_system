package task_management_system.com.task_management.util;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;

// DB接続クラス
public class DBCon {

    // ローカル用
    private static final String JDBC_URL =
            "jdbc:mysql://localhost:3306/task_management_system_db"
            + "?useSSL=false"
            + "&characterEncoding=UTF-8"
            + "&serverTimezone=Asia/Tokyo";

    private static final String USER = "appuser";
    private static final String PASSWORD = "password1234!";

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    // データベース接続
    public static Connection getConnection() {

        try {
            Class.forName(DRIVER);

            // ===== Heroku(JawsDB)用 =====
            String jawsdbUrl = System.getenv("JAWSDB_URL");

            if (jawsdbUrl != null) {

                URI dbUri = new URI(jawsdbUrl);

                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];

                String jdbcUrl =
                        "jdbc:mysql://"
                        + dbUri.getHost()
                        + ":"
                        + dbUri.getPort()
                        + dbUri.getPath()
                        + "?useSSL=false"
                        + "&characterEncoding=UTF-8"
                        + "&serverTimezone=Asia/Tokyo";

                return DriverManager.getConnection(jdbcUrl, username, password);
            }

            // ===== ローカル用 =====
            return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);

        } catch (Exception e) {
            System.err.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}