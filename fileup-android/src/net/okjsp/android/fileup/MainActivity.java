package net.okjsp.android.fileup;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
   
	ImageView imageView;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        imageView=(ImageView)findViewById(R.id.imageView);
    }
    final int REQ_SELECT=0;
   
    //갤러리 호출해서 이미지 읽어오기
    public void push(View v){
    	//사진 읽어오기 위한 uri 작성하기.
    	 Uri uri = Uri.parse("content://media/external/images/media");
    	 //무언가 보여달라는 암시적 인텐트 객체 생성하기.
         Intent intent = new Intent(Intent.ACTION_VIEW, uri);
         //인텐트에 요청을 덧붙인다. 
         intent.setAction(Intent.ACTION_GET_CONTENT);
         //모든 이미지
         intent.setType("image/*");
         //결과값을 받아오는 액티비티를 실행한다.
         startActivityForResult(intent, REQ_SELECT);
         
    }
    //카메라로 찍기
    public void takePicture(View v){
    	  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (intent == null) return;
    	try{
			//인텐트에 데이터가 담겨 왔다면
			if(intent.getData() != null){
				//해당경로의 이미지를 intent에 담긴 이미지 uri를 이용해서 Bitmap형태로 읽어온다.
			    Bitmap selPhoto = Images.Media.getBitmap(getContentResolver(), intent.getData());
			    //이미지의 크기 조절하기.
			    selPhoto = Bitmap.createScaledBitmap(selPhoto, 100, 100, true);
			    //image_bt.setImageBitmap(selPhoto);//썸네일
			    //화면에 출력해본다.
			    imageView.setImageBitmap(selPhoto);
			    Log.e("선택 된 이미지 ", "selPhoto : " + selPhoto);
			   
			}
		}catch(FileNotFoundException e) {
		    e.printStackTrace();
		}catch(IOException e) {
		    e.printStackTrace();
		}
		//선택한 이미지의 uri를 읽어온다.   
		Uri selPhotoUri = intent.getData();
	
		//업로드할 서버의 url 주소
	    String urlString = "http://192.168.0.7:8080/fileup-server/upload_ok.jsp";
	    //절대경로를 획득한다!!! 중요~
	    Cursor c = getContentResolver().query(Uri.parse(selPhotoUri.toString()), null,null,null,null);
	    c.moveToNext();
	    //업로드할 파일의 절대경로 얻어오기("_data") 로 해도 된다.
	    String absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
	    Log.e("###파일의 절대 경로###", absolutePath);
	   //파일 업로드 시작!
	   HttpFileUpload(urlString ,"", absolutePath);
	  

    }
    
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****"; 

    public void HttpFileUpload(String urlString, String params, String fileName) {
    	
    	try{
    		//선택한 파일의 절대 경로를 이용해서 파일 입력 스트림 객체를 얻어온다.
    		FileInputStream mFileInputStream = new FileInputStream(fileName); 
    		//파일을 업로드할 서버의 url 주소를이용해서 URL 객체 생성하기.
    		URL connectUrl = new URL(urlString);
    		//Connection 객체 얻어오기. 
    		HttpURLConnection conn = (HttpURLConnection)connectUrl.openConnection();   
    		conn.setDoInput(true);//입력할수 있도록
    		conn.setDoOutput(true); //출력할수 있도록
    		conn.setUseCaches(false);  //캐쉬 사용하지 않음
    		//post 전송
    		conn.setRequestMethod("POST");
    		//파일 업로드 할수 있도록 설정하기.
    		conn.setRequestProperty("Connection", "Keep-Alive");
    		conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
       
    		//DataOutputStream 객체 생성하기.
    		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
    		//전송할 데이터의 시작임을 알린다.
    		dos.writeBytes(twoHyphens + boundary + lineEnd);
    		dos.writeBytes("Content-Disposition: form-data; name=\"upfile\";filename=\"" + fileName+"\"" + lineEnd);
    		dos.writeBytes(lineEnd);
    		//한번에 읽어들일수있는 스트림의 크기를 얻어온다.
    		int bytesAvailable = mFileInputStream.available();
    		//byte단위로 읽어오기 위하여 byte 배열 객체를 준비한다.
    		byte[] buffer = new byte[bytesAvailable];
    		int bytesRead = 0;
    		// read image
    		while (bytesRead!=-1) {
    			//파일에서 바이트단위로 읽어온다.
    			bytesRead = mFileInputStream.read(buffer);
    			if(bytesRead==-1)break; //더이상 읽을 데이터가 없다면 빠저나온다.
        		Log.d("Test", "image byte is " + bytesRead);
        		//읽은만큼 출력한다.
    			dos.write(buffer, 0, bytesRead);
    			//출력한 데이터 밀어내기
    			dos.flush();
    		} 
    		//전송할 데이터의 끝임을 알린다.
    		dos.writeBytes(lineEnd);
    		dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
    		//flush() 타이밍??
    		//dos.flush(); 
    		dos.close();//스트림 닫아주기   
    		mFileInputStream.close();//스트림 닫아주기.
    		// get response
    		int ch;
    		//입력 스트림 객체를 얻어온다.
    		InputStream is = conn.getInputStream();
    		StringBuffer b =new StringBuffer();
    		while( ( ch = is.read() ) != -1 ){
    			b.append( (char)ch );
    		}
    		String s=b.toString(); 
    		Log.d("Test", "result = " + s);
       } catch (Exception e) {
    	   Log.e("Test", "exception " + e.getMessage());
    	   Toast.makeText(this,"업로드중 에러발생!" +  e.getMessage(), 0).show();
       }  
   }
     
}