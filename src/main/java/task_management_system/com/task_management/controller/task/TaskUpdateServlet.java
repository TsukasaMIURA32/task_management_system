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
import task_management_system.com.task_management.dao.TaskImageDAO;
import task_management_system.com.task_management.dao.TaskUserDAO;
import task_management_system.com.task_management.dto.TaskDTO;
import task_management_system.com.task_management.dto.UserDTO;

@WebServlet(name = "TaskUpdateServlet", urlPatterns = "/task/update")
@MultipartConfig
public class TaskUpdateServlet extends HttpServlet {
    private TaskDAO taskDAO = new TaskDAO();
    private TaskUserDAO taskUserDAO = new TaskUserDAO();
    private TaskImageDAO taskImageDAO = new TaskImageDAO();

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
        String taskIdStr = request.getParameter("taskId");          // 更新対象タスクID
        String title = request.getParameter("title");               // タイトル
        String content = request.getParameter("content");           // 本文
        String colorIdStr = request.getParameter("colorId");       // 色ID（文字列）
        String[] sharedUserIds = request.getParameterValues("sharedUserIds"); // 共有ユーザー
        String deleteImageIds = request.getParameter("deleteImageIds");       // 削除対象画像ID一覧
        System.out.println(taskIdStr);
        /* =========================
           バリデーション
        ========================= */

        // taskId は必須のため、数値変換できない場合は不正入力として戻す
        int taskId;
        try {
            taskId = Integer.parseInt(taskIdStr);
            System.out.println(taskId);
        } catch (NumberFormatException e) {
            session.setAttribute("flashMessage", "タスクの更新ができませんでした。");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
     // ログインユーザーがそのタスクの編集権限を持っているか確認
        boolean canEdit = taskUserDAO.existsTaskUser(taskId, loginUser.getId());

        if (!canEdit) {
            session.setAttribute("flashMessage", "不正な操作です。");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

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

        // 範囲外の色IDもデフォルトに補正
        if (colorId < 1 || colorId > 5) {
            colorId = 1;
        }

        // multipartで送られてきた全パーツ取得（画像など）
        Collection<Part> parts = request.getParts();

        // 最大サイズ（64KB）
        long maxSize = 64 * 1024;

        // 新規追加画像のサイズチェック
        for (Part part : parts) {
            // name="newImages" のファイルのみ対象
            if ("newImages".equals(part.getName()) && part.getSize() > 0) {

                // サイズオーバーなら処理中断
                if (part.getSize() > maxSize) {
                    session.setAttribute("flashMessage", "画像サイズが大きすぎます。64KB以下にしてください。");
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                    return;
                }
            }
        }

        try {
            // 更新用DTOに値を詰める
            TaskDTO dto = new TaskDTO();
            dto.setId(taskId);
            dto.setTitle(title);
            dto.setContent(content);
            dto.setColorId(colorId);

            // 1. tasks テーブルを更新
            int updateResult = taskDAO.update(dto);

            // 更新失敗チェック
            if (updateResult <= 0) {
                session.setAttribute("flashMessage", "タスクの更新ができませんでした。");
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            }

            // 2. tasks_users をいったん削除して再登録
            taskUserDAO.deleteTaskUsersByTaskId(taskId);
            taskUserDAO.insertTaskUsers(taskId, sharedUserIds, loginUser.getId());

         // 3. 削除対象画像を削除
            if (deleteImageIds != null && !deleteImageIds.isBlank()) {
                String[] imageIdArray = deleteImageIds.split(",");

                for (String imageIdStr : imageIdArray) {
                    if (imageIdStr != null && !imageIdStr.isBlank()) {
                        try {
                            int imageId = Integer.parseInt(imageIdStr.trim());

                            // 指定されたtaskIdに紐づく画像だけ削除する
                            taskImageDAO.deleteByIdAndTaskId(imageId, taskId);

                        } catch (NumberFormatException e) {
                            // 数値に変換できない不正な画像IDは無視して続行
                        }
                    }
                }
            }
	
	            // 4. 新規画像を追加
	            for (Part part : parts) {
	                if ("newImages".equals(part.getName()) && part.getSize() > 0) {
	
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
            throw new ServletException("タスク更新処理でエラーが発生しました。", e);
        }
    }
}