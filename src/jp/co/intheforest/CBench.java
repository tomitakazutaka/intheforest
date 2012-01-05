package jp.co.intheforest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class CBench extends Thread {
    

    private ArrayList<String> keylist = new ArrayList<String>();
    private ArrayList<String> exetype = new ArrayList<String>();

    private AtomicInteger userIdnum;
    private AtomicInteger filegetnum;
    private String uploadFileName;
    HashMap<String,String> usrInfo = new HashMap<String, String>();
    CBUtility cbutil;
    String execdate;
    static String hostname;
    
    FastDateFormat df = FastDateFormat.getInstance("yyyy/MM/dd(E) HH:mm:ss");

    final static Logger logger = LoggerFactory.getLogger("CBench");


    
    CBench (String uFName, String wConsistencyLevel, String rConsistencyLevel,String conHost) {

        logger.info("Benchmark Start!");
        userIdnum = new AtomicInteger(0);
        filegetnum = new AtomicInteger(0);
        uploadFileName = uFName;
        cbutil = new CBUtility(wConsistencyLevel,rConsistencyLevel,conHost);
        keylist = cbutil.getKeys();

        FastDateFormat startdf = FastDateFormat.getInstance("yyyyMMddHHmmss");
        execdate = startdf.format(System.currentTimeMillis());

        try {
            hostname = InetAddress.getLocalHost().getHostName().toLowerCase();
        } catch (UnknownHostException e) {
            logger.error("UnknownHostException : {}",e.getMessage());
        }
        logger.debug("CBench run");
    };

    public void setFilelist(ArrayList<String> exectype){
        exetype = exectype;
    }
    
    public void closeConnect(){
        cbutil.closeConnect();
    }

    public int deleteFile()
    {
        int i = userIdnum.getAndAdd(1);
        String key = keylist.get(i);
        if ( key==null || key.length()==0 ) { 
            logger.error("get key error.");
            return 1;
         }

        try {
            cbutil.deleteData(key);
            } catch (Exception ex) {
            logger.error("Delete Error : ex =" + ex.getMessage());
            return 1;
            }
        return 0;
    }

    public int getFile()
    {    
        int i = userIdnum.getAndAdd(1);
        String key = keylist.get(i);
        if ( key==null || key.length()==0 ) { 
            logger.error("get key error.");
            return 1;
         }
        try {
            cbutil.getData(key);
        } catch (Exception ex) {
            logger.error("Get Error : ex =" + ex.getMessage());
            return 1;
         }
        return 0;
    }    

    private int uploadFile(){

        String filepath = uploadFileName;
        if ( filepath  == null || filepath.length()==0 ) {
            logger.error("The file does not exist.");
            return 1;
            }
        String filedir = System.getProperty("user.dir") 
                + File.separator + "uploadfiles";
        
        File filetoupload = null;
        try {
            filetoupload = new File(filedir, filepath );
        } catch (NullPointerException npe) {
            logger.error("Get file error: " + npe.getMessage());
            return 1;
         }    
        if ( !filetoupload.isFile() ) { 
            logger.error("error : The file does not exist. ");
            return 1;
            }

        String dataline = null;
        try {
            FileReader f = new FileReader(filetoupload);
            BufferedReader b = new BufferedReader(f);
            String line;
            while((line = b.readLine())!=null){
                if (dataline == null){
                    dataline = line;
                } else {
                    dataline = dataline + line;
                  }
              }
            f.close();
         } catch(Exception e){
             logger.error("The file was not opened. : " + e.getMessage());
             return 1;
          }
           Random rnd = new Random();
           int uid = rnd.nextInt(1000);
           String key = hostname + "-" + execdate + "-" + uid;
            try {
                cbutil.putData(dataline, key);
               } catch (Exception ex) {
                logger.error("Put error encountered: " + ex.getMessage());
                return 1;
                  }    
        return 0;
    }    

    @Override
    public void start() {
        logger.debug("thread init start");
    }

    @Override
    public void run() {

        int n = filegetnum.getAndAdd(1);

        String etype = exetype.get(n);
        String usrId = null;


        long t = System.currentTimeMillis();

        if (etype.equals("isWrite") ) {

            int returnstate = uploadFile();
            if (returnstate == 1) {
                logger.error("isWrite Fail uname =" + usrId);
            }
            long s = System.currentTimeMillis();
            logger.info("isWrite Exec Time =" + String.format(" %8d ミリ秒",s-t));

        } else if (etype.equals("isRead")) {
            int returnstate = getFile();
            if (returnstate == 1) {
                logger.error("isRead Fail uname =" + usrId);
            }
            long s = System.currentTimeMillis();
            logger.info("isRead Exec Time =" + String.format(" %8d ミリ秒",s-t));

        } else if (etype.equals("isDelete")) {
            int returnstate = deleteFile();
            if (returnstate == 1) {
                logger.error("isDelete Fail uname =" + usrId);
            }
            long s = System.currentTimeMillis();
            logger.info("isDelete Exec Time =" + String.format(" %8d ミリ秒",s-t));
        }
    }

}
