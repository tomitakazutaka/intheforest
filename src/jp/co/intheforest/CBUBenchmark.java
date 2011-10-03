package jp.co.intheforest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;

public class CBUBenchmark extends Thread {
	
	private static String hostnames;
	final Logger logger = Logger.getLogger("CBULogging");

	
	CBUBenchmark (String connecthostname){
		logger.info("const run");
		hostnames = connecthostname;
	};
	
	private String getAccountList(String hostname) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://" + hostname + 
				":18081/user/list?userId=admin%2540cloudian.com&groupId=0");
		
		httpGet.setHeader("Content-Type", "application/json");
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sReturn = null;
		 if (  httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			 try {
				InputStream objStream = httpResponse.getEntity().getContent();
				InputStreamReader objReader = new InputStreamReader(objStream);
				BufferedReader objBuf = new BufferedReader(objReader);
				StringBuilder objJson = new StringBuilder();
				String sLine;
				while((sLine = objBuf.readLine()) != null){ 
				    logger.info(sLine + "\n");

					objJson.append(sLine); 
				}
				sReturn = objJson.toString(); 
			    objStream.close();
			    //logger.info(sReturn);
			 
			 } catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 } else {
		        throw new RuntimeException(httpResponse.toString());
		  }
			JSONObject json = new JSONObject();

		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void start() {
		logger.info("thread start");
        getAccountList(hostnames);
	}

	@Override
	public void run() {
		//String account_json = getAccountList();
		//logger.info("account json = " + account_json);
	}

}
