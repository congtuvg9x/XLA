<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@page import="main.StaticVariable"%>
<%@page import="java.util.*"%>
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
		document.getElementById("sift_matching").style.color = "yellow";
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
			<form class="form-wrapper cf" method="post" action="CompareSiftSearch" enctype="multipart/form-data">
				<input  id="file_input" name="file_input" type="file" webkitdirectory directory required=""/>
				<button style="font:'Arial'" type="submit">Tìm kiếm</button>
			</form>
		</div>

		<%
			if(request.getParameter("page") != null && Integer.parseInt(request.getParameter("numPage")) != 0){
				out.println("<div class='page'>");
				out.println("<form id='form_id' method='get' action='CompareSiftSearch'>");
				out.println("<input type='submit' name='press' value='Trang đầu' class='btn'></input>");
				out.println("<input type='submit' name='press' value='Trang trước' class='btn'></input>");
						
				out.println("<input type='hidden' name='input' value='" + checkNull(value) + "'/>");
				out.println("<input type='text' id='pageID' name='page' value='" + request.getParameter("page") + "'/>");
				out.println("<input type='hidden' name='numPage' value='" + request.getParameter("numPage") + "'/>");
						
				out.println("<input type='submit' name='press' value='Trang kế' class='btn'></input>");
				out.println("<input type='submit' name='press' value='Trang cuối' class='btn'></input>");
				out.println("</form>");
				out.println("</div>");
				
			}
		%>		

		<%
			try{
				String name[];
				ServletContext applicationObject = getServletConfig().getServletContext();
				List<String> nameImgs = new ArrayList<String>();
				nameImgs = (List<String>)applicationObject.getAttribute("nameImgs");
				String path = applicationObject.getContextPath();
				if(!nameImgs.isEmpty()){
					/*
					out.print("<div class='query'>");
						out.print("<div>ẢNH TÌM KIẾM</div>");
						out.print("<div><img src='images/query.jpg' width='100px' height='100px' /></div>");
					out.print("</div>");
					*/
					out.print("<div class='title_result'>KẾT QUẢ TÌM KIẾM</div>");
					for(int i = 0 ; i<nameImgs.size() ; i++){
						name = nameImgs.get(i).split(".sift");
						out.print("<div class='img_result'>");
							out.print("<div class='img_name'>Hình " + (i+1) + ": " + name[0] + ".jpg</div>");
							out.print("<div><img src='img/" + name[0] + ".jpg'/></div>");
						out.print("</div>");
						if(i % 3 == 2) out.print("<br/>");
						if(i == StaticVariable.offsetPage-1) break;
					}
				}
			} catch(Exception ex){}
		%>
		<div>			
		</div>
	</div>
</div>



<div id="copyright" class="container">
	<p>&copy; Copyright | Designed by <a href="http://www.thanhphi.890m.com">Thanh Phi</a>.</p>
</div>
</body>
</html>
