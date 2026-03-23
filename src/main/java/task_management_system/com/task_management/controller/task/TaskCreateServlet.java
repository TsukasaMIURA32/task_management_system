package task_management_system.com.task_management.controller.task;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import task_management_system.com.task_management.dao.TaskDAO;
import task_management_system.com.task_management.dao.TaskUserDAO;
import task_management_system.com.task_management.dto.TaskDTO;
import task_management_system.com.task_management.dto.UserDTO;

@WebServlet(name = "TaskCreateServlet", urlPatterns = "/task/create")
@MultipartConfig
public class TaskCreateServlet extends HttpServlet {
	private TaskDAO taskDAO = new TaskDAO();
	private TaskUserDAO taskUserDAO = new TaskUserDAO();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {

	    // リクエストの文字コードをUTF-8に設定（日本語対策）
	    request.setCharacterEncoding("UTF-8");

	    // 既存のセッションを取得（なければnull）
	    HttpSession session = request.getSession(false);

	    // セッションがない＝未ログイン → ログイン画面へ
	    if (session == null) {
	        response.sendRedirect(request.getContextPath() + "/login");
	        return;
	    }

	    // セッションからログインユーザーを取得
	    UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");

	    // ログインユーザーが取得できない場合もログイン画面へ
	    if (loginUser == null) {
	        response.sendRedirect(request.getContextPath() + "/login");
	        return;
	    }

	    // フォームから送られてきた値を取得
	    String title = request.getParameter("title");       // タイトル
	    String content = request.getParameter("content");   // 本文
	    String colorIdStr = request.getParameter("colorId"); // 色ID（文字列）
	    String[] sharedUserIds = request.getParameterValues("sharedUserIds"); // 共有ユーザー

	    /* =========================
	       バリデーション
	    ========================= */
//	    System.out.println(colorIdStr);
	    // デフォルトの色ID（未指定時）
	    int colorId = 1;

	    try {
	        if (colorIdStr != null && !colorIdStr.isBlank()) {
	            colorId = Integer.parseInt(colorIdStr);
	        }
	    } catch (NumberFormatException e) {
	        // 数値変換できない場合はデフォルト値のまま
	        colorId = 1;
	    }

	    // 範囲外もデフォルトに補正
	    if (colorId < 1 || colorId > 5) {
	        colorId = 1;
	    }
	    


	    // multipartで送られてきた全パーツ取得（画像など）
	    Collection<Part> parts = request.getParts();

	    // 最大サイズ（64KB）
	    long maxSize = 64 * 1024;

	    // アップロードされた画像のサイズチェック
	    for (Part part : parts) {
	        // name="image" のファイルのみ対象
	        if ("image".equals(part.getName()) && part.getSize() > 0) {

	            // サイズオーバーなら処理中断
	            if (part.getSize() > maxSize) {
	                session.setAttribute("flashMessage", "画像サイズが大きすぎます。64KB以下にしてください。");
	                response.sendRedirect(request.getContextPath() + "/dashboard");
	                return;
	            }
	        }
	    }

	    try {
	        // タスク情報をDTOに詰める
	        TaskDTO dto = new TaskDTO();

	        // 作成者（owner）としてログインユーザーIDをセット
	        dto.setOwnerId(loginUser.getId());

	        // タイトル・内容・色をセット
	        dto.setTitle(title);
	        dto.setContent(content);
	        dto.setColorId(colorId);

	        // ① tasksテーブルに登録
	        int taskId = taskDAO.insert(dto);

	        // 登録失敗チェック
	        if (taskId <= 0) {
	            session.setAttribute("flashMessage", "タスクの作成に失敗しました。");
	            response.sendRedirect(request.getContextPath() + "/dashboard");
	            return;
	        }

	        // ② tasks_users に共有ユーザー登録（＋自分も含める想定）
	        taskUserDAO.insertTaskUsers(taskId, sharedUserIds, loginUser.getId());

	        // ③ 画像登録処理
	        for (Part part : parts) {
	            if ("image".equals(part.getName()) && part.getSize() > 0) {

	                // 入力ストリームを取得（自動クローズされる）
	                try (InputStream is = part.getInputStream()) {

	                    // DBに画像保存
	                    int imageResult = taskDAO.insertTaskImage(taskId, is);

	                    // 画像登録失敗チェック
	                    if (imageResult <= 0) {
	                        session.setAttribute("flashMessage", "画像の登録に失敗しました。");
	                        response.sendRedirect(request.getContextPath() + "/dashboard");
	                        return;
	                    }
	                }
	            }
	        }

	        // 一覧画面へ戻る
	        response.sendRedirect(request.getContextPath() + "/dashboard");

	    } catch (Exception e) {
	        // 想定外エラー（DB接続失敗など）
	        e.printStackTrace();

	        // サーバーエラーとして上位に投げる → error.jspへ
	        throw new ServletException("タスク作成処理でエラーが発生しました。", e);
	    }
	}
}