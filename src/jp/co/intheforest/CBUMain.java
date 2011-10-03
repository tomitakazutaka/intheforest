package jp.co.intheforest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
//import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.BasicParser;



public class CBUMain {

    static String mode;
    static boolean isDebug;
    static int thread_num;
    static int account_num;
    static String hostname;
    protected static final String LOGGING_PROPERTIES
    = "javalog.properties";
    protected static final String DEAULT_PROPERTIES_DIR
    = "./conf/";
    

    final static Logger logger = Logger.getLogger("Croudian Benchmark Utility");

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
          logconfig = DEAULT_PROPERTIES_DIR + LOGGING_PROPERTIES;
              }
        	 // クラスパスの中から ログ設定プロパティファイルを取得
         File currentDirectory = new File("./conf/javalog.properties");
         logger.info(currentDirectory.getAbsolutePath());
         logger.info("ログ設定ファイル: " + logconfig );
         final InputStream inStream = CBUMain.class
             .getClassLoader().getResourceAsStream(
             logconfig);
           if (inStream == null) {
             logger.info("ログ設定: " + LOGGING_PROPERTIES
                  + " はクラスパス上に見つかりませんでした。");
           } else {
             try {
                 LogManager.getLogManager().readConfiguration(
                     inStream);
             } catch (IOException e) {
                 logger.warning("ログ設定: LogManager設定の際に"
                     +"例外が発生しました。:"+ e.toString());
             } finally {
                 try {
                     if (inStream != null) inStream.close();
                 } catch (IOException e) {
                     logger.warning("ログ設定: ログ設定プロパティ"
                         +"ファイルのストリームクローズ時に例外が"
                         +"発生しました。:"+ e.toString());
                 }
             }
         }
		}

    private int Args_Operation (String[] args) throws Exception {
    	// Argment Operation
        Options options = new Options();
        
        // mode
        OptionBuilder.withLongOpt("mode");
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withArgName("[account_create|data_create|benchmark]");
        OptionBuilder.withDescription("実行モードを指定します");
        Option modeOption = OptionBuilder.create("m");
        options.addOption(modeOption);

        // Host
        OptionBuilder.withLongOpt("host");
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withArgName("[IPAdress|Hostname]");
        OptionBuilder.withDescription("接続先サーバーを指定します");
        Option hostOption = OptionBuilder.create("H");
        options.addOption(hostOption);

        
        // thread number
        OptionBuilder.withLongOpt("thread_num");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("実行thread数");
        OptionBuilder.withDescription("実行thread数を指定します");
        Option threadOption = OptionBuilder.create("t");
        options.addOption(threadOption);
        
        // account number
        OptionBuilder.withLongOpt("account_num");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("実行アカウント数もしくは作成アカウント数");
        OptionBuilder.withDescription("実行アカウント数もしくは作成アカウント数を指定します");
        Option accnumOption = OptionBuilder.create("a");
        options.addOption(accnumOption);
        

        //debug mode
        OptionBuilder.withLongOpt("debug");
        OptionBuilder.withDescription("デバッグモードを指定します");
        Option debugOption = OptionBuilder.create("d");
        options.addOption(debugOption);

        // help
        OptionBuilder.withLongOpt("help");
        OptionBuilder.withDescription("このヘルプを表示します");
        Option helpOption = OptionBuilder.create("h");
    
        options.addOption(helpOption);

        
        CommandLineParser parser = new BasicParser();
/*
 *         CommandLineParser parser = new BasicParser(){

			@Override
			public CommandLine parse(Options arg0, String[] arg1, boolean arg2)
					throws ParseException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public CommandLine parse(Options arg0, String[] arg1) throws ParseException {
				// TODO Auto-generated method stub
				return null;
			}
		};
*/

        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter help = new HelpFormatter();
            help.printHelp(CBUMain.class.getName(), options, true);
            return 1;
         }

        if (commandLine.hasOption("d")) {
 	       isDebug = true;
          }

        if (commandLine.hasOption("h")) {
            HelpFormatter help = new HelpFormatter();
            help.printHelp(CBUMain.class.getName(), options, true);
            return 1;
     	}
     	if (commandLine.hasOption("m")) {
            logger.info("mode = " + commandLine.getOptionValue("m"));

     		if (
     			(commandLine.getOptionValue("m").equals("account_create")) || 
     			(commandLine.getOptionValue("m").equals("data_create")) || 
     			(commandLine.getOptionValue("m").equals("benchmark" )))
     			{
         			mode = commandLine.getOptionValue("m");
     			} else {
     				 logger.warning("mode type error = " + commandLine.getOptionValue("m") );
     	            HelpFormatter help = new HelpFormatter();
     	            help.printHelp(CBUMain.class.getName(), options, true);
     	            return 1;
     			}
     		}
     		
        if (commandLine.hasOption("t")) {
            thread_num = Integer.parseInt(commandLine.getOptionValue("t"));
          } else {
            thread_num = 1;
          }

        if (commandLine.hasOption("a")) {
            account_num = Integer.parseInt(commandLine.getOptionValue("a"));
          } else {
            account_num = 1;
          }

     	if (commandLine.hasOption("H")) {
 	       if (InetAddress.getByName(
 	    		   commandLine.getOptionValue("H")) != null){
 	    	   hostname = (InetAddress.getByName(
 	    			   commandLine.getOptionValue("H")).toString());
 	       } /*else if ( InetAddress.getByAddress(
 	    		   commandLine.getOptionValue("H")) != null) {
 	    	   hostname = commandLine.getOptionValue("H");
 	       }*/ else {
 	    	   hostname = "localhost";
            };
          }

        
     	
        logger.info("Cloudian Benchmark Utility Start");

    	return 0;
    }
    
	private void Create_User_Run() {
		int loopnum;
		int remaindenum = 0;
		if ( account_num > thread_num) {
			loopnum = account_num / thread_num;
			remaindenum = account_num % thread_num;
		}	else {
			loopnum = 1;
		}
		CBUCreateUser cbucu = new CBUCreateUser(loopnum,remaindenum);
		cbucu.start();
		for (int i = 1; i <= thread_num; i++) {
		cbucu.run();
		}

	}

	private void Benchmark_Run() {
		CBUBenchmark cbubm = new CBUBenchmark(hostname);
		cbubm.start();
//		for (int i = 1; i <= thread_num; i++) {
//		cbucu.run();
//		}
	}

	
    public static void main(final String[] args) throws Exception{

       logger.info("Cloudian Benchmark Utility Start");
    	CBUMain cbumain = new CBUMain();
		int execstate = cbumain.Args_Operation(args);
		if (execstate == 1) { System.exit(1); };

		if ( mode.equals("account_create")){
			System.out.print(mode);
	         //  Create User Tool
	         cbumain.Create_User_Run();
		} else if (mode.equals("benchmark")) {
	         cbumain.Benchmark_Run();
			
		}
         // Data Upload Tool
         //cbumain.Data_Upload_Run();

		System.out.print(mode);
        System.exit(0);
        
    }



}
