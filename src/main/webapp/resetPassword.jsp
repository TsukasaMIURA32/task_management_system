<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>パスワード再設定</title>
<link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="auth-container">
    <div class="auth-card">
        <h1 class="auth-title">パスワード再設定</h1>
        <p class="auth-subtitle">新しいパスワードを設定してください</p>

        <%
        String error = (String) request.getAttribute("error");
        if (error != null) {
        %>
            <p class="error-message"><%= error %></p>
        <%
        }
        %>

        <form action="resetPassword" method="post">
            <div class="form-group">
                <label for="email">メールアドレス</label>
                <input type="email" id="email" name="email" placeholder="example@email.com" required>
            </div>

            <div class="form-group">
                <label for="newPassword">新しいパスワード</label>
                <input type="password" id="newPassword" name="newPassword" placeholder="新しいパスワードを入力" required>
            </div>

            <div class="form-group">
                <label for="confirmPassword">新しいパスワード（確認用）</label>
                <input type="password" id="confirmPassword" name="confirmPassword" placeholder="もう一度入力" required>
            </div>

            <div class="form-group">
                <button type="submit" class="btn-primary">再設定する</button>
            </div>
        </form>

        <div class="auth-links">
            <p><a href="login.jsp">ログイン画面へ戻る</a></p>
        </div>
    </div>
</div>

</body>
</html>