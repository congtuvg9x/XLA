<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Lập chỉ mục</title>
<meta name="keywords" content="" />
<meta name="description" content="" />
<link href="http://fonts.googleapis.com/css?family=Source+Sans+Pro:200,300,400,600,700,900" rel="stylesheet" />
<link href="default.css" rel="stylesheet" type="text/css" media="all" />
<link href="fonts.css" rel="stylesheet" type="text/css" media="all" />

</head>
<body>
<%@include file="header.jsp" %>
<div id="header-featured">
	<div id="banner" class="container"> </div>
</div>

<div id="wrapper">
	<div id="featured-wrapper">
		<div class="extra2 container">
		<h2 class="title-index">ĐỌC CSDL</h2>
	    <form method="post" action="Index" enctype="multipart/form-data">
            <table class="table-inverted-index" border="1" cellspacing="0" align="center" width="82%">
                <tr>
                    <td class="indextd">Chọn thư mục</td>
                    <td><input  id="file_input" name="file_input" type="file" webkitdirectory directory/></td>
                </tr>
                <tr>
                    <td colspan="2"><input type="submit" value="Thực hiện"/></td>
                </tr>
            </table>
        </form>
        
                <%
            try{
                ServletContext applicationObject=getServletConfig().getServletContext();
                Map<String, Map<Integer, Integer>> invertedIndex = (Map<String, Map<Integer, Integer>>) applicationObject.getAttribute("InvertedIndex");
                List<Float> timeInvertedIndex = (List<Float>) applicationObject.getAttribute("timeInvertedIndex");
                
                out.print("<div class='time'>Thời gian lập chỉ mục nghịch đảo: (" + timeInvertedIndex.get(0) + " giây)</div>");
                
                if(invertedIndex.size()!=0){
                    out.print("<table border='1' class='table-inverted-index' cellpadding='0' cellspacing='0' align='center' width='82%'>");
                        out.print("<tr>");
                            out.print("<td colspan='4'><h2 align='center' class='title-index'>DANH SÁCH CHỈ MỤC</h2></td>");
                        out.print("</tr>");
                        out.print("<tr>");
                            out.print("<th align='center'>STT</th>");
                            out.print("<th align='center'>Token</th>");
                            out.print("<th align='center'>Document Frequency</th>");
                            out.print("<th align='center'>[Tài liệu Term_Frequency]</th>");
                        out.print("</tr>");
                    int count=1;
                    int tf = 0;
                    for(String s : invertedIndex.keySet()){
                    	String str = "";
                    	Map<Integer, Integer> posting= invertedIndex.get(s);
                    	for(int item : posting.keySet()){
                    		tf = posting.get(item);
                    		str += "[" + item + " " + tf + "] ";
                    	}
                        out.print("<tr>");
                            out.print("<td align='center'>"+count+"</td>");
                            out.print("<td style='padding-left:20px'>"+s+"</td>");
                            out.print("<td style='padding-left:20px'>"+posting.size()+"</td>");
                            out.print("<td style='padding-left:20px'>"+str+"</td>");
                        out.print("</tr>");
                        count++;
                    }
                    out.print("</table>");
                }
            }
            catch(Exception ex){

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
