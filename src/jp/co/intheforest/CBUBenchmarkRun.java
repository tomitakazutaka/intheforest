/**
 * 
 */
package jp.co.intheforest;

/**
 * @author kazutaka
 *
 */
public class CBUBenchmarkRun extends Thread {
	
	CBUBenchmarkRun() {};
	
	@Override
	public void start() {
		logger.info("thread start");
        getAccountList();
	}

	@Override
	public void run() {
		String account_json = getAccountList();
		logger.info("account json = " + account_json);
	}



}
