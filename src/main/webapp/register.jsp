<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>アカウント作成</title>
<link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="auth-container">
    <div class="auth-card">
        <h1 class="auth-title">アカウント作成</h1>
        <p class="auth-subtitle">必要な情報を入力してください</p>

        <%
        String error = (String) request.getAttribute("error");
        if (error != null) {
        %>
            <p class="error-message"><%= error %></p>
        <%
        }
        %>

        <form action="register" method="post">
            <div class="form-group">
                <label for="userName">名前</label>
                <input type="text" id="userName" name="userName" placeholder="名前を入力" required>
            </div>

            <div class="form-group">
                <label for="email">メールアドレス</label>
                <input type="email" id="email" name="email" placeholder="example@email.com" required>
            </div>

            <div class="form-group">
                <label for="password">パスワード</label>
                <input type="password" id="password" name="password" placeholder="パスワードを入力" required>
            </div>

            <div class="form-group">
                <label>ユーザー種別</label>
                <div class="radio-group">
                    <label>
                        <input type="radio" name="role" value="user" checked>
                        一般ユーザー
                    </label>
                    <label>
                        <input type="radio" name="role" value="admin">
                        管理ユーザー
                    </label>
                </div>
            </div>

            <div class="form-group">
                <button type="submit" class="btn-primary">登録</button>
            </div>
        </form>

        <div class="auth-links">
            <p><a href="login.jsp">ログイン画面へ戻る</a></p>
        </div>
    </div>
</div>

</body>
</html>

