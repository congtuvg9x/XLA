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
		document.getElementById("validation").style.color = "yellow";
	}
</script>
</head>
<body onload="init_load()">
<%!
	String value = "";
	String checkNull(String s) {
    return s == null ? "" : s;
}
%>

<%
try{
	ServletContext applicationObject = getServletConfig().getServletContext();
	value = (String)applicationObject.getAttribute("input");
} catch(Exception ex){}
%>
<%@include file="header.jsp" %>
<div id="header-featured">
	<div id="banner" class="container"> </div>
</div>

<div id="wrapper">
	<div id="featured-wrapper">
		<div id="featured" class="extra2 margin-btm container">
			<form class="form-wrapper cf" method="post" action="Validation">
			<input  id="file_input" name="file_input" type="file"/>
				<button style="font:'Arial'" type="submit">Thực hiện</button>
			</form>
		</div>
		
		<div>
			<%
				try{
					ServletContext applicationObject=getServletConfig().getServletContext();
					List<Double> precision = (List<Double>) applicationObject.getAttribute("precision");
					//double precision = (double) applicationObject.getAttribute("precision");
					int k = 0;
					Double pre;
					if(precision.size() != 0){
						out.print("<div class='title_result'>KẾT QUẢ</div>");
						
						Iterator iterator = precision.iterator(); 
						//while (iterator.hasNext()){
							pre = (Double) iterator.next();
							out.print("<div class='div_result'>Giá trị precision: <b>" + Math.round(pre*100.00)/1.0+ "%</b></div>");
							
							pre = (Double) iterator.next();
							out.print("<div class='div_result'>Giá trị recall :  <b>" + Math.round(pre*100.00)/1.0+ "%</b></div>");
						//}
					} else{
						out.println("Không tìm thấy kết quả");
					}
				}catch(Exception ex){
					//out.println(ex);
				}
			%>
		</div>
	</div>
</div>



<div id="copyright" class="container">
	<p>&copy; Copyright | Designed by <a href="http://www.thanhphi.890m.com">Thanh Phi</a>.</p>
</div>
</body>
</html>
