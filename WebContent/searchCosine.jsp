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
			<form class="form-wrapper cf" method="post" action="SearchCosine" enctype="multipart/form-data">
				<input  id="file_input" name="file_input" type="file" webkitdirectory directory required=""/>
				<button style="font:'Arial'" type="submit">Tìm kiếm</button>
			</form>
		</div>
		
		<%
			if(request.getParameter("page") != null && Integer.parseInt(request.getParameter("numPage")) != 0){
				out.println("<div class='page'>");
				out.println("<form id='form_id' method='get' action='SearchCosine'>");
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
		<!--  
		<div class="page">
			<form id="form_id" method="get" action="Search">
				<input type="submit" name="press" value="First"></input>
				<input type="submit" name="press" value="Next"></input>
				
				<input type="hidden" name="input" value="<%=checkNull(value)%>"/>
				<input type="text" id="pageID" name="page" value="<%= request.getParameter("page")%>"/>
				<input type="hidden" name="numPage" value="<%= request.getParameter("numPage")%>"/>
				
				<input type="submit" name="press" value="Previous"></input>
				<input type="submit" name="press" value="End"></input>
			</form>
		</div>
		-->
		<div>
			<%
				try{
					ServletContext applicationObject=getServletConfig().getServletContext();
					List<Double> cosine = (List<Double>) applicationObject.getAttribute("cosine");
					List<String> name = (List<String>) applicationObject.getAttribute("name_image");
					String input = (String) applicationObject.getAttribute("input");
					int k = 0;
					Double cosine_img;
					if(cosine.size() != 0){
						/*
						out.print("<div class='query'>");
						out.print("<div>Ảnh tìm kiếm</div>");
						out.print("<div><img src='images/query_cosine.jpg' width='100px' height='100px' /></div>");
						out.print("</div>");
						*/
						out.print("<div class='title_result'>KẾT QUẢ TÌM KIẾM</div>");
						
						Iterator iterator = cosine.iterator(); 
						Iterator iteratorId = name.iterator(); 
						while (iteratorId.hasNext()){
							String name_img = (String) iteratorId.next();
							cosine_img = (Double) iterator.next();
							
							out.print("<div class='img_result'>");
								out.print("<div class='img_name'>Hình " + (k+1) + ": " + name_img + ".jpg</div>");
								out.print("<div><img src='img/" + name_img + ".jpg'/></div>");
							out.print("</div>");
						
							if (k % 3 == 2) out.println("<br/>");
							if(k == StaticVariable.offsetPage-1) break;
							
							k++;
							//out.println("<div> Doc: " + iterator.next() + "</div>");  
						}
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
