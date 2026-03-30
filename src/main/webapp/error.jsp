<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isErrorPage="true"%>

<%
Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");

String title = "エラー";
String message = "エラーが発生しました。";

if (statusCode != null) {
	if (statusCode == 404) {
		title = "ページが見つかりません";
		message = "指定されたページは存在しないか、移動した可能性があります。";
	} else if (statusCode == 500) {
		title = "システムエラーが発生しました";
		message = "申し訳ありません。時間をおいて再度お試しください。";
	} else if (statusCode == 403) {
		title = "アクセス権限がありません";
		message = "このページにアクセスする権限がありません。";
	}
}


Object loginUser = session.getAttribute("loginUser");
boolean isLoggedIn = (loginUser != null);

%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><%= title %></title>
<link href="<%=request.getContextPath()%>/css/error.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body>
	<div class="error-container">
		<h1 class="error-title"><i class="fas fa-bug"></i><%= title %></h1>
		<p class="error-message"><%= message %></p>
		<a href="<%=request.getContextPath()%>/login" id="backButton" class="back-link">
			ログインページに戻る
		</a>
	</div>
<!--<script src="<%=request.getContextPath()%>/js/error.js"></script>-->
</body>
</html>