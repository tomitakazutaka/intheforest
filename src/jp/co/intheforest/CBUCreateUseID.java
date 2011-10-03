package jp.co.intheforest;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class CBUCreateUseID {

   final Logger logger = Logger.getLogger("CBULogging");
   AtomicInteger userId = new AtomicInteger(0);

	public synchronized int get(int n ) {
		logger.info("userid create");
		return n + userId.addAndGet(1);
	}
}
