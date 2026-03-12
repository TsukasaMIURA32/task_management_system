<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登録完了</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <div class="auth-container">
        <div class="auth-card complete-card">
            <h1 class="auth-title">登録完了</h1>
            <p class="auth-subtitle">アカウントの登録が完了しました</p>

            <div class="success-message">
                登録したメールアドレスとパスワードでログインしてください。
            </div>

            <div class="complete-message">
                <p>管理ユーザーで登録された方は、申請が承認されるまでお待ちください。</p>
            </div>

            <div class="form-group">
                <a href="login.jsp" class="btn-primary btn-link">ログイン画面へ</a>
            </div>
        </div>
    </div>

</body>
</html>