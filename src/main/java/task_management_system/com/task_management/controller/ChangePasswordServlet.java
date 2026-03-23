package task_management_system.com.task_management.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import task_management_system.com.task_management.dao.UserDAO;
import task_management_system.com.task_management.dto.UserDTO;
import task_management_system.com.task_management.util.PasswordUtil;

/**
 * Servlet implementation class ChangePasswordServlet
 */
@WebServlet(name="ChangePasswordServlet", urlPatterns="/user/changepassword")
public class ChangePasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// 8文字以上、数字を1文字以上、記号を1文字以上含む
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$");
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangePasswordServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}



	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();

		try {
			/* =========================
			   セッション・ログインチェック
			========================= */
			HttpSession session = request.getSession(false);

			if (session == null) {
				out.print("{\"success\":false,\"message\":\"ログイン情報がありません。\"}");
				return;
			}

			UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");

			if (loginUser == null) {
				out.print("{\"success\":false,\"message\":\"ログイン情報がありません。\"}");
				return;
			}

			/* =========================
			   パラメータ取得
			========================= */
			String currentPassword = request.getParameter("currentPassword");
			String newPassword = request.getParameter("newPassword");
			String confirmNewPassword = request.getParameter("confirmNewPassword");

			/* =========================
			   バリデーション
			========================= */
			if (currentPassword == null || currentPassword.isBlank()
					|| newPassword == null || newPassword.isBlank()
					|| confirmNewPassword == null || confirmNewPassword.isBlank()) {
				out.print("{\"success\":false,\"message\":\"未入力の項目があります。\"}");
				return;
			}

			if (!newPassword.equals(confirmNewPassword)) {
				out.print("{\"success\":false,\"message\":\"新しいパスワードと確認用パスワードが一致しません。\"}");
				return;
			}

			if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
				out.print("{\"success\":false,\"message\":\"パスワードは8文字以上で、数字と記号を1文字以上含めてください。\"}");
				return;
			}

			UserDAO userDao = new UserDAO();
			UserDTO dbUser = userDao.findByEmail(loginUser.getEmail());

			if (dbUser == null) {
				out.print("{\"success\":false,\"message\":\"ユーザー情報が見つかりません。\"}");
				return;
			}

			/* =========================
			   現在のパスワード照合
			   DBにはハッシュが保存されている前提
			========================= */
			boolean passwordMatch = PasswordUtil.matches(currentPassword, dbUser.getPassword());

			if (!passwordMatch) {
				out.print("{\"success\":false,\"message\":\"現在のパスワードが正しくありません。\"}");
				return;
			}

			/* =========================
			   新しいパスワードをハッシュ化して更新
			========================= */
			String hashedPassword = PasswordUtil.hashPassword(newPassword);
			boolean result = userDao.updatePasswordByEmail(loginUser.getEmail(), hashedPassword);

			if (result) {
				out.print("{\"success\":true,\"message\":\"パスワードを変更しました。\"}");
			} else {
				out.print("{\"success\":false,\"message\":\"パスワード変更に失敗しました。\"}");
			}

		} catch (Exception e) {
			e.printStackTrace();
			out.print("{\"success\":false,\"message\":\"システムエラーが発生しました。\"}");
		}
	}
}
