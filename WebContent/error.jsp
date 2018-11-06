<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@page import="java.util.*"%>
<%@page import="main.StaticVariable"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Tìm kiếm</title>
<meta name="keywords" content="" />
<meta name="description" content="" />
<link href="http://fonts.googleapis.com/css?family=Source+Sans+Pro:200,300,400,600,700,900" rel="stylesheet" />
<link href="default.css" rel="stylesheet" type="text/css" media="all" />
<link href="fonts.css" rel="stylesheet" type="text/css" media="all" />

<script type="text/javascript">
	function init_load(){
		document.getElementById("bag_words").style.color = "yellow";
	}
</script>
</head>
<body>

<%@include file="header.jsp" %>

<div id="header-featured">
	<div id="banner" class="container"> </div>
</div>

<div id="wrapper">
	<h3 style="text-align: center;">Lỗi!!!</h3>
	<div style="text-align: center;">Chưa có ảnh trong CSDL ảnh hoặc không tồn tại file sift...Vào thư mục XLA/data/sift để kiểm tra</div>
	<div style="text-align: center;">Vui lòng thực hiện tại các bước hướng dẫn. Kiểm tra lại thư mục ảnh đầu vào hoặc đường dẫn đến thư mục. Kiểm tra đường dẫn không gian tên,...</div>
</div>



</body>
</html>
