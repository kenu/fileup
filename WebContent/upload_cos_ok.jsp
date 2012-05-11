<%-- upload_cos_ok.jsp --%>
<%@ page contentType="text/html;charset=euc-kr" %>
<%@ page import="java.io.File,
                 java.io.IOException,
                 com.oreilly.servlet.MultipartRequest,
                 com.oreilly.servlet.multipart.DefaultFileRenamePolicy" %>
<html>
<head>
 <title>MultipartRequest를 이용한 파일 업로드</title>
</head>
<body>
<%

  // request.setCharacterEncoding("euc-kr"); 

  String savePath = application.getRealPath("/") + "upload";
  int sizeLimit = 1 * 1024 * 1024 ;  // 1 메가까지 제한한다. 넘어서면 IOException 발생


  try{
	MultipartRequest multi=new MultipartRequest(request, savePath, sizeLimit, "euc-kr", new DefaultFileRenamePolicy());
	
	// 한글이 깨질 경우 아래와 같이 "euc-kr" 파라미터를 제거한다. 주로 Linux 환경일 경우에 많이 발생한다.
	// MultipartRequest multi=new MultipartRequest(request, savePath, sizeLimit, new DefaultFileRenamePolicy());

	String fileName=multi.getFilesystemName("upfile");  // 파일의 이름얻기
	String originalFileName = multi.getOriginalFileName("upfile"); // 중복처리 이전의 파일 이름 얻기
  
	// 파일이 업로드 되지 않았을때
	if(fileName == null) {
%>
  
	<h2>파일이 업로드 되지 않았습니다.</h2>
	<BR>
	<a href="javascript:history.back()">다시 업로드 하기</a>

<%
	// 파일이 업로드 되었을때의 처리
	} else {   
	    // 생성자에서 "euc-kr" 파라미터를 제거했을 경우 아래와 같이 인코딩이 필요하다
		// fileName			= new String(fileName.getBytes("8859_1"),"euc-kr"); // 한글 출력을 위한 인코딩 
		// originalFileName	= new String(originalFileName.getBytes("8859_1"),"euc-kr");
	
		File file1      = multi.getFile("upfile");  // 파일 객체 얻기
		String division = multi.getParameter("division");
		String contents = multi.getParameter("contents");
		
		// 생성자에서 "euc-kr"을 제거한 경우
		// contents		= new String(contens.getBytes("8859_1"),"euc-kr");
%>
	<h2>파일 업로드가 정상적으로 완료 되었습니다!! </h2>
	저장된 파일 이름 : <%=fileName%> <BR>
	변경되기 이전의 파일 이름 : <%=originalFileName%> <BR>
	종류 : <%=getDivisionName(division)%> <BR>
	설명 : <%=contents%> <BR>
	사이즈 : <%=file1.length()%> Byte <BR>
	ContentType : <%=multi.getContentType("upfile")%> <BR>


<%
	}
  } catch(IOException e) {
	out.print("<h2> IOException 이 발생했습니다 </h2> <BR><pre>" + e.getMessage() + "</pre>");
  }

%>
</body>
</html>
<%!

	public String getDivisionName(String division) {

		String divisionName = "";
		try{

			if ( division.equals("game") ) {
				divisionName = "게임";
			} else if ( division.equals("util") ) {
				divisionName = "유틸리티";
			} else if ( division.equals("doc") ) {
				divisionName = "문서자료";
			} else if ( division.equals("movie") ) {
				divisionName = "동영상";
			} else if ( division.equals("music") ) {
				divisionName = "음악";
			} else if ( division.equals("etc") ) {
				divisionName = "기타";
			}
		
		} catch (Exception e){
			divisionName = "기타";
		}

		return divisionName;
	}

%>


