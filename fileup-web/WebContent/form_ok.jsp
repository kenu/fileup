<%@ page contentType="text/html;charset=ms949" %>
<META HTTP-EQUIV="Content-type" CONTENT="text/html;charset=ksc5601">
<%@ page import="java.io.*"%>
<% request.setCharacterEncoding("ms949"); %>
request 정보
<pre>
contentType  : <%=request.getContentType()%>
contentLength: <%=request.getContentLength()%>
method : <%=request.getMethod()%>
field1 : <%=request.getParameter("field1")%>
file1  : <%=request.getParameter("file1")%>
</pre>
<hr>
request 스트림
<xmp><%

    // request 를 스트림으로 받아서 BufferedReader 에 넣는다.
    BufferedReader in = new BufferedReader(
        new InputStreamReader( request.getInputStream() ) );

    String line;

    // BufferedReader 에서 한줄씩 빼서 출력한다.
    // 스트림의 끝에 도달할 경우 null 이 반환된다.
    while ((line = in.readLine()) != null) {
        out.println(line);
    } // end while
%></xmp>