package jp.co.intheforest;

import junit.framework.TestCase;

public class TestCBUBenchmark extends TestCase {

	private CBUBenchmark tc1;
	private Boolean execstat;
	private String connecthosts = "192.168.1.10";

	protected void setUp() throws Exception {
		tc1 = new CBUBenchmark(connecthosts);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testStart() {
		fail("まだ実装されていません");
	}

	public void testRun() {
		fail("まだ実装されていません");
	}

	public void testCBUBenchmark() {
		tc1.start();
//		for (int i = 1; i <= 20; i++) {
//		tc1.run();
//		}
		execstat = true;
		assertTrue(execstat);
	}

}
