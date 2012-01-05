package jp.co.intheforest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.LogManager;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.BasicParser;



public class CBMain {

    private static int thread_num;
    private String fileexecname;
    private String rConsistencyLevel;
    private String wConsistencyLevel;
    private int writereadRatio;
    private int writeRatio;
    private int deleteRatio;
    private int readRatio;
    private String conHost;
    
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    protected static final String DEAULT_PROPERTIES_DIR
    = "conf";
    protected static final String LOGGING_PROPERTIES
                                             = "javalog.properties";
    protected static final String BENCHMARK_PROPERTIES
                                             = "benchmark.properties";
    
    
    final static Logger logger 
            = LoggerFactory.getLogger("CBMain");

    /**
     * @param args
     * @return 
     */

    /**
     * static initializer によるログ設定の初期化
     */
     static {

    	 String logconfig = System.getProperty("javalog.configuration");
         if (logconfig == null) {
             logconfig = System.getProperty("user.dir") + File.separator 
                                + DEAULT_PROPERTIES_DIR + File.separator
                                + LOGGING_PROPERTIES;
         }
         InputStream inStream = null;
         try{
             inStream = new FileInputStream(new File(logconfig));
             LogManager.getLogManager().readConfiguration(inStream);
         } catch ( IOException e){
             logger.error("ログ設定: " + LOGGING_PROPERTIES
                  + " はクラスパス上に見つかりませんでした。");
             System.exit(1);
         } finally {
             try {
                 if (inStream != null) inStream.close();
             } catch (IOException e) {
                 logger.error("ログ設定プロパティファイルのストリームクローズ時に例外が発生しました。:"
                             + e.toString());
                 System.exit(1);
               }
           }
         }

    private int Args_Operation (String[] args) throws Exception {
        // Argment Operation
        Options options = new Options();
        
        // thread number
        OptionBuilder.withLongOpt("thread_num");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("実行thread数");
        OptionBuilder.withDescription("実行thread数を指定します");
        Option threadOption = OptionBuilder.create("t");
        options.addOption(threadOption);
        // upload data size
        OptionBuilder.withLongOpt("upload_datasize");
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withArgName("アップロードデータサイズ");
        OptionBuilder.withDescription("アップロード用データサイズを指定します");
        Option udsizeOption = OptionBuilder.create("s");
        options.addOption(udsizeOption);

        // upload data size
        OptionBuilder.withLongOpt("hostname");
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withArgName("接続先ホスト名");
        OptionBuilder.withDescription("接続先ホスト名を指定します");
        Option hostOption = OptionBuilder.create("H");
        options.addOption(hostOption);

        // help
        OptionBuilder.withLongOpt("help");
        OptionBuilder.withDescription("このヘルプを表示します");
        Option helpOption = OptionBuilder.create("h");
        options.addOption(helpOption);

        CommandLineParser parser = new BasicParser();

        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter help = new HelpFormatter();
            help.printHelp(CBMain.class.getName(), options, true);
            return 1;
         }

       fileexecname = commandLine.getOptionValue("s");
       conHost = commandLine.getOptionValue("H");


        if (commandLine.hasOption("t")) {
            thread_num = Integer.parseInt(commandLine.getOptionValue("t"));
              if (thread_num > 100) {
                  logger.error("スレッド数は100以下にしてください。");
                  System.exit(1);
                }
          } else {
            thread_num = 1;
          }


        logger.info("Cloudian Benchmark Utility Start");

        return 0;
    }
	private int confiureSetting() {

    String benchmarkconfig = System.getProperty("benchmark.configuration");
    Properties configuration = new Properties();
    if (benchmarkconfig == null) {
        benchmarkconfig = System.getProperty("user.dir") + File.separator 
                            + DEAULT_PROPERTIES_DIR + File.separator
                            + BENCHMARK_PROPERTIES;
     }
     InputStream inStream = null;
     try{
         inStream = new FileInputStream(new File(benchmarkconfig));
         configuration.load(inStream);
         rConsistencyLevel = configuration.getProperty("readConsistency");
         if ( rConsistencyLevel == null) {
             logger.error("rConsistencyLevel is required.");
             if (inStream != null) inStream.close();
              return 1;
          }
         wConsistencyLevel = configuration.getProperty("writeConsistency");
         if ( wConsistencyLevel == null) {
             logger.error("wConsistencyLevel is required.");
             if (inStream != null) inStream.close();
              return 1;
          }
         String wrRatio = configuration.getProperty("wrRatio");
         if ( wrRatio == null) {
             logger.error("wrRatio is required.");
             if (inStream != null) inStream.close();
              return 1;
          }
         String wdRatio = configuration.getProperty("wdRatio");
         if ( wdRatio == null) {
             logger.error("wdRatio is required.");
             if (inStream != null) inStream.close();
              return 1;
          }
         String[] wr = wrRatio.split(":" , -1);
         String[] wd = wdRatio.split(":", -1);
         writereadRatio = Integer.valueOf(wr[0]).intValue();
         readRatio = Integer.valueOf(wr[1]).intValue();
         writeRatio = Integer.valueOf(wd[0]).intValue();
         deleteRatio = Integer.valueOf(wd[1]).intValue();
     } catch ( IOException e){
         logger.error("ベンチマーク設定: " + LOGGING_PROPERTIES
              + " はクラスパス上に見つかりませんでした。");
         return 1;
     } finally {
         try {
             if (inStream != null) inStream.close();
         } catch (IOException e) {
             logger.error("ベンチマーク設定プロパティファイルのストリームクローズ時に例外が発生しました。:"
                         + e.toString());
             System.exit(1);
           }
       }

		// TODO Auto-generated method stub
		return 0;
	}
    
    private void Benchmark_Run() {

        int readcount;
        int wdcount;
        int wcount;
        int dcount;
        ArrayList<String> exectype = new ArrayList<String>();
        if ( readRatio > writereadRatio ){
            readcount = (int) Math.floor(thread_num * ( readRatio * 0.1));
            wdcount = (int) Math.ceil(thread_num * (writereadRatio * 0.1));
         } else {
             readcount = (int) Math.ceil(thread_num * ( readRatio * 0.1));
             wdcount = (int) Math.floor(thread_num * (writereadRatio * 0.1));
         }
        if ( writeRatio > deleteRatio) {
            wcount = (int) Math.floor(wdcount * (writeRatio * 0.1 ));
            dcount = (int) Math.ceil(wdcount * ( deleteRatio * 0.1));
        } else {
            wcount = (int) Math.ceil(wdcount * (writeRatio * 0.1 ));
            dcount = (int) Math.floor(wdcount * ( deleteRatio * 0.1));
        }
        String[] errwd = {String.valueOf(readcount),String.valueOf(wcount),String.valueOf(dcount)};
        logger.debug("readcount : {} wcount : {} dcount {}",errwd );
        
        for (int i = 0; i <= readcount -1 ; i++) {
            exectype.add("isRead");
        }

        for (int i = 0; i <= wcount -1 ; i++) {
            exectype.add("isWrite");
        }
        for (int i = 0; i <= dcount -1 ; i++) {
                    exectype.add("isDelete");
        }
        Collections.shuffle(exectype);
        
        logger.debug(exectype.toString());

        CBench cbubm = new CBench(fileexecname,wConsistencyLevel,rConsistencyLevel,conHost);
        cbubm.setFilelist(exectype);
        for (int i = 0; i < thread_num; i++) {
            cbubm.start();
            cbubm.run();
        }
        try {
			cbubm.join();
		} catch (InterruptedException e) {
			logger.error("thread join error : {}",e);
		}
        cbubm.closeConnect();
    }


    public static void main(final String[] args) throws Exception{

       logger.info("Cassandra Benchmark Start");
       CBMain cbumain = new CBMain();

       int execstate = cbumain.Args_Operation(args);
       if (execstate == 1) {
           logger.error("Cassandra Benchmark Args Option Error Exit.");
           System.exit(1);
           };

       cbumain.confiureSetting();
       if (execstate == 1) {
           System.exit(1);
           logger.error("Cassandra Benchmark Configure Setting Error Exit.");
           };
       cbumain.Benchmark_Run();

       logger.info("Cassandra Benchmark finished");

       System.exit(0);
    }




}
