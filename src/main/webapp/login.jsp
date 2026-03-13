<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ログイン画面</title>
<link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="auth-container">
    <div class="auth-card">
        <h1 class="auth-title">タスク管理システム</h1>
        <p class="auth-subtitle">アカウントにログイン</p>

        <%
        String error = (String) request.getAttribute("error");
        if (error != null) {
        %>
            <p class="error-message"><%= error %></p>
        <%
        }
        %>

        <form action="<%= request.getContextPath() %>/login" method="post">

            <div class="form-group">
                <label for="email">メールアドレス</label>
                <input type="email" id="email" name="email" placeholder="example@email.com" required>
            </div>

            <div class="form-group">
                <label for="password">パスワード</label>
                <input type="password" id="password" name="password" placeholder="パスワードを入力" required>
            </div>

            <div class="form-group">
                <button type="submit" class="btn-primary">ログイン</button>
            </div>

        </form>

        <div class="auth-links">
            <p><a href="register.jsp">アカウント作成</a></p>
            <p><a href="resetPassword.jsp">パスワードを忘れた方</a></p>
        </div>

    </div>
</div>

</body>
</html>