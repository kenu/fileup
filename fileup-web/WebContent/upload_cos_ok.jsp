<%-- upload_cos_ok.jsp --%>
<%@ page contentType="text/html;charset=euc-kr" %>
<%@ page import="java.io.File,
                 java.io.IOException,
                 com.oreilly.servlet.MultipartRequest,
                 com.oreilly.servlet.multipart.DefaultFileRenamePolicy" %>
<html>
<head>
 <title>MultipartRequest�� �̿��� ���� ���ε�</title>
</head>
<body>
<%

  // request.setCharacterEncoding("euc-kr"); 

  String savePath = application.getRealPath("/") + "upload";
  int sizeLimit = 100 * 1024 * 1024 ;  // 100 �ް����� �����Ѵ�. �Ѿ�� IOException �߻�


  try{
	MultipartRequest multi=new MultipartRequest(request, savePath, sizeLimit, "euc-kr", new DefaultFileRenamePolicy());
	
	// �ѱ��� ���� ��� �Ʒ��� ���� "euc-kr" �Ķ���͸� �����Ѵ�. �ַ� Linux ȯ���� ��쿡 ���� �߻��Ѵ�.
	// MultipartRequest multi=new MultipartRequest(request, savePath, sizeLimit, new DefaultFileRenamePolicy());

	String fileName=multi.getFilesystemName("upfile");  // ������ �̸����
	String originalFileName = multi.getOriginalFileName("upfile"); // �ߺ�ó�� ������ ���� �̸� ���
  
	// ������ ���ε� ���� �ʾ�����
	if(fileName == null) {
%>
  
	<h2>������ ���ε� ���� �ʾҽ��ϴ�.</h2>
	<BR>
	<a href="javascript:history.back()">�ٽ� ���ε� �ϱ�</a>

<%
	// ������ ���ε� �Ǿ������� ó��
	} else {   
	    // �����ڿ��� "euc-kr" �Ķ���͸� �������� ��� �Ʒ��� ���� ���ڵ��� �ʿ��ϴ�
		// fileName			= new String(fileName.getBytes("8859_1"),"euc-kr"); // �ѱ� ����� ���� ���ڵ� 
		// originalFileName	= new String(originalFileName.getBytes("8859_1"),"euc-kr");
	
		File file1      = multi.getFile("upfile");  // ���� ��ü ���
		String division = multi.getParameter("division");
		String contents = multi.getParameter("contents");
		
		// �����ڿ��� "euc-kr"�� ������ ���
		// contents		= new String(contens.getBytes("8859_1"),"euc-kr");
%>
	<h2>���� ���ε尡 ���������� �Ϸ� �Ǿ����ϴ�!! </h2>
	����� ���� �̸� : <%=fileName%> <BR>
	����Ǳ� ������ ���� �̸� : <%=originalFileName%> <BR>
	���� : <%=getDivisionName(division)%> <BR>
	���� : <%=contents%> <BR>
	������ : <%=file1.length()%> Byte <BR>
	ContentType : <%=multi.getContentType("upfile")%> <BR>


<%
	}
  } catch(IOException e) {
	out.print("<h2> IOException �� �߻��߽��ϴ� </h2> <BR><pre>" + e.getMessage() + "</pre>");
  }

%>
</body>
</html>
<%!

	public String getDivisionName(String division) {

		String divisionName = "";
		try{

			if ( division.equals("game") ) {
				divisionName = "����";
			} else if ( division.equals("util") ) {
				divisionName = "��ƿ��Ƽ";
			} else if ( division.equals("doc") ) {
				divisionName = "�����ڷ�";
			} else if ( division.equals("movie") ) {
				divisionName = "������";
			} else if ( division.equals("music") ) {
				divisionName = "����";
			} else if ( division.equals("etc") ) {
				divisionName = "��Ÿ";
			}
		
		} catch (Exception e){
			divisionName = "��Ÿ";
		}

		return divisionName;
	}

%>


