package task_management_system.com.task_management.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import task_management_system.com.task_management.util.DBCon;

@WebServlet("/dbtest")
public class dbtest extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection conn = DBCon.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                out.println("<h1>データベース接続成功！</h1>");
            } else {
                out.println("<h1>データベース接続失敗</h1>");
            }
        } catch (Exception e) {
            out.println("<h1>エラーが発生しました</h1>");
            out.println("<p>" + e.getMessage() + "</p>");
            e.printStackTrace();
        }
    }
}