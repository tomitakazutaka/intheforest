package jp.co.intheforest;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;



public class CBUConfig {
    /**
	    * ログ設定プロパティファイルのファイル名
	    */
	    protected static final String LOGGING_PROPERTIES
	        = "javalog.properties";

	    CBUConfig () {
	        final Logger logger = Logger.getLogger("CBULogging");

	        // クラスパスの中から ログ設定プロパティファイルを取得
	        final InputStream inStream = CBUConfig.class
	            .getClassLoader().getResourceAsStream(
	            LOGGING_PROPERTIES);
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

	
}
