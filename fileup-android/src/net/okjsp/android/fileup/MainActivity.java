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
   
    //������ ȣ���ؼ� �̹��� �о����
    public void push(View v){
    	//���� �о���� ���� uri �ۼ��ϱ�.
    	 Uri uri = Uri.parse("content://media/external/images/media");
    	 //���� �����޶�� �Ͻ��� ����Ʈ ��ü �����ϱ�.
         Intent intent = new Intent(Intent.ACTION_VIEW, uri);
         //����Ʈ�� ��û�� �����δ�. 
         intent.setAction(Intent.ACTION_GET_CONTENT);
         //��� �̹���
         intent.setType("image/*");
         //������� �޾ƿ��� ��Ƽ��Ƽ�� �����Ѵ�.
         startActivityForResult(intent, REQ_SELECT);
         
    }
    //ī�޶�� ���
    public void takePicture(View v){
    	  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (intent == null) return;
    	try{
			//����Ʈ�� �����Ͱ� ��� �Դٸ�
			if(intent.getData() != null){
				//�ش����� �̹����� intent�� ��� �̹��� uri�� �̿��ؼ� Bitmap���·� �о�´�.
			    Bitmap selPhoto = Images.Media.getBitmap(getContentResolver(), intent.getData());
			    //�̹����� ũ�� �����ϱ�.
			    selPhoto = Bitmap.createScaledBitmap(selPhoto, 100, 100, true);
			    //image_bt.setImageBitmap(selPhoto);//�����
			    //ȭ�鿡 ����غ���.
			    imageView.setImageBitmap(selPhoto);
			    Log.e("���� �� �̹��� ", "selPhoto : " + selPhoto);
			   
			}
		}catch(FileNotFoundException e) {
		    e.printStackTrace();
		}catch(IOException e) {
		    e.printStackTrace();
		}
		//������ �̹����� uri�� �о�´�.   
		Uri selPhotoUri = intent.getData();
	
		//���ε��� ������ url �ּ�
	    String urlString = "http://14.63.227.64:8080/fileup-server/upload_ok.jsp";
	    //�����θ� ȹ���Ѵ�!!! �߿�~
	    Cursor c = getContentResolver().query(Uri.parse(selPhotoUri.toString()), null,null,null,null);
	    c.moveToNext();
	    //���ε��� ������ ������ ������("_data") �� �ص� �ȴ�.
	    String absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
	    Log.e("###������ ���� ���###", absolutePath);
	   //���� ���ε� ����!
	   HttpFileUpload(urlString ,"", absolutePath);
	  

    }
    
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****"; 

    public void HttpFileUpload(String urlString, String params, String fileName) {
    	
    	try{
    		//������ ������ ���� ��θ� �̿��ؼ� ���� �Է� ��Ʈ�� ��ü�� ���´�.
    		FileInputStream mFileInputStream = new FileInputStream(fileName); 
    		//������ ���ε��� ������ url �ּҸ��̿��ؼ� URL ��ü �����ϱ�.
    		URL connectUrl = new URL(urlString);
    		//Connection ��ü ������. 
    		HttpURLConnection conn = (HttpURLConnection)connectUrl.openConnection();   
    		conn.setDoInput(true);//�Է��Ҽ� �ֵ���
    		conn.setDoOutput(true); //����Ҽ� �ֵ���
    		conn.setUseCaches(false);  //ĳ�� ������� ����
    		//post ����
    		conn.setRequestMethod("POST");
    		//���� ���ε� �Ҽ� �ֵ��� �����ϱ�.
    		conn.setRequestProperty("Connection", "Keep-Alive");
    		conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
       
    		//DataOutputStream ��ü �����ϱ�.
    		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
    		//������ �������� �������� �˸���.
    		dos.writeBytes(twoHyphens + boundary + lineEnd);
    		dos.writeBytes("Content-Disposition: form-data; name=\"upfile\";filename=\"" + fileName+"\"" + lineEnd);
    		dos.writeBytes(lineEnd);
    		//�ѹ��� �о���ϼ��ִ� ��Ʈ���� ũ�⸦ ���´�.
    		int bytesAvailable = mFileInputStream.available();
    		//byte������ �о���� ���Ͽ� byte �迭 ��ü�� �غ��Ѵ�.
    		byte[] buffer = new byte[bytesAvailable];
    		int bytesRead = 0;
    		// read image
    		while (bytesRead!=-1) {
    			//���Ͽ��� ����Ʈ������ �о�´�.
    			bytesRead = mFileInputStream.read(buffer);
    			if(bytesRead==-1)break; //���̻� ���� �����Ͱ� ���ٸ� �������´�.
        		Log.d("Test", "image byte is " + bytesRead);
        		//������ŭ ����Ѵ�.
    			dos.write(buffer, 0, bytesRead);
    			//����� ������ �о��
    			dos.flush();
    		} 
    		//������ �������� ������ �˸���.
    		dos.writeBytes(lineEnd);
    		dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
    		//flush() Ÿ�̹�??
    		//dos.flush(); 
    		dos.close();//��Ʈ�� �ݾ��ֱ�   
    		mFileInputStream.close();//��Ʈ�� �ݾ��ֱ�.
    		// get response
    		int ch;
    		//�Է� ��Ʈ�� ��ü�� ���´�.
    		InputStream is = conn.getInputStream();
    		StringBuffer b =new StringBuffer();
    		while( ( ch = is.read() ) != -1 ){
    			b.append( (char)ch );
    		}
    		String s=b.toString(); 
    		Log.d("Test", "result = " + s);
       } catch (Exception e) {
    	   Log.e("Test", "exception " + e.getMessage());
    	   Toast.makeText(this,"���ε��� �����߻�!" +  e.getMessage(), 0).show();
       }  
   }
     
}