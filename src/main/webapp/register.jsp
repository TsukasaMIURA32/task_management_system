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
        <p class="auth-subtitle">必要事項を入力してください</p>

        <%
        String error = (String) request.getAttribute("error");
        if (error != null) {
        %>
            <p class="error-message"><%= error %></p>
        <%
        }
        %>

        <form action="<%= request.getContextPath() %>/register" method="post">

            <div class="form-group">
                <label for="userName">名前</label>
                <input type="text"
                       id="userName"
                       name="userName"
                       value="<%= request.getAttribute("userName") != null ? request.getAttribute("userName") : "" %>"
                       required>
            </div>

            <div class="form-group">
                <label for="email">メールアドレス</label>
                <input type="email"
                       id="email"
                       name="email"
                       value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>"
                       required>
            </div>

            <div class="form-group">
                <label for="password">パスワード</label>
                <input type="password"
                       id="password"
                       name="password"
                       required
                       minlength="8"
                       pattern="^(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$"
                       title="パスワードは8文字以上で、数字と記号を1文字以上含めてください">
                <p class="form-hint">※8文字以上で、数字と記号を1文字以上含めてください</p>
            </div>

            <div class="form-group">
                <label for="confirmPassword">パスワード（確認用）</label>
                <input type="password"
                       id="confirmPassword"
                       name="confirmPassword"
                       required
                       minlength="8">
            </div>

            <div class="form-group">
                <label>ユーザー種別</label>
                <div class="radio-group">
                    <label>
                        <input type="radio" name="role" value="user"
                            <%= "admin".equals(request.getAttribute("role")) ? "" : "checked" %>>
                        一般ユーザー
                    </label>
                    <label>
                        <input type="radio" name="role" value="admin"
                            <%= "admin".equals(request.getAttribute("role")) ? "checked" : "" %>>
                        管理ユーザー
                    </label>
                </div>
            </div>

            <button type="submit" class="btn-primary">登録する</button>
        </form>

        <div class="auth-links">
            <a href="<%= request.getContextPath() %>/login.jsp">ログイン画面へ戻る</a>
        </div>
    </div>
</div>

</body>
</html>