package jp.co.intheforest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;


public class CBUCreateUser extends Thread {

	CBUCreateUseID c_UserID;
	private String userId;
	private String t;
	private String hostname;
	private int loop_num;
	private int remainde_num;
	final Logger logger = Logger.getLogger("CBULogging");
	
	
	CBUCreateUser(int loopnum,int remaindenum){
		c_UserID = new CBUCreateUseID();
		t = String.valueOf(System.currentTimeMillis());
		loop_num = loopnum;
		remainde_num = remaindenum;
		try {
			hostname = InetAddress.getLocalHost().getHostName().toLowerCase();
		} catch (UnknownHostException e) {
			hostname = "default_hostname";
		}
	}
	
	

    private String createUserJson() {

    	int n = 1000; //初期化必須
       int user_num = c_UserID.get(n);
		userId = hostname + "-" + t + "-" + String.valueOf(user_num);
       logger.info("user_id = " + userId);
    	JSONObject json = new JSONObject();
    	
    	json.put("userId", userId);
    	json.put("bucketName","bucket-"+ userId + "po.ntts.co.jp");
    	json.put("userType","User");
    	json.put("fullName",userId + " " + userId );
    	json.put("emailAddr",userId + "@" + userId);
    	json.put("address1",userId + userId + userId);
    	json.put("address2",userId + userId + userId);
    	json.put("city","Tokyo");
    	json.put("password","password");
    	json.put("groupId","CloudianDemo");
    	json.put("state",userId + userId + userId);
    	json.put("country","Japan");
    	json.put("country","Japan");
    	json.put("groupId","CloudianDemo");

    	return json.toString();
    }

//	private String AccountCreate(String sUrl, String sJson) {  
	private void AccountCreate() {  
		String json = createUserJson();

		HttpPut httpPut = new HttpPut("http://192.168.1.10:18081/user");
		httpPut.setHeader("Content-Type", "application/json");
		StringEntity se = null;
		try {
			se = new StringEntity(json);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		httpPut.setEntity(se); 
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse = null;
		 
		try {
			httpResponse = httpClient.execute(httpPut);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
		        throw new RuntimeException(httpResponse.toString());
		  }
	}
	@Override
	public void start() {
		logger.info("thread start");
		if ( remainde_num > 0 ) {
			for (int i = 1; i <= remainde_num; i++) {
				AccountCreate();
			}
		}

/*		 HttpPost httpPost = new HttpPost("http://localhost:4567/");
//		 httpPost.setEntity(new JSONEntity(json, true));
		 
		 HttpClient httpClient = new DefaultHttpClient();
		 HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
		        throw new RuntimeException(httpResponse.toString());
		  }
		*/ 
	}

	@Override
	public void run() {
		for (int i = 1; i <= loop_num; i++) {
			AccountCreate();
		}
	}

}
