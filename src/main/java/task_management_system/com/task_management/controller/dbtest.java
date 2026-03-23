package task_management_system.com.task_management.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * エラーページ確認用サーブレット
 */
@WebServlet("/test/error500")
public class dbtest extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/* =========================
		   強制的に500エラーを発生させる
		========================= */
		throw new ServletException("500エラーテストです。");
	}
}