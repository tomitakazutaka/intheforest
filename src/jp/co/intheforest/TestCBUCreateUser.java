package jp.co.intheforest;

import junit.framework.TestCase;

public class TestCBUCreateUser extends TestCase {
	private CBUCreateUser tc1;
	private Boolean execstat;
		
	protected void setUp() throws Exception {
		tc1 = new CBUCreateUser();
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	public void testStart() {
		fail("まだ実装されていません");
	}

	public void testRun() {
		fail("まだ実装されていません");
	} */

	public void testCBUCreateUser() {
		tc1.start();
		for (int i = 1; i <= 20; i++) {
		tc1.run();
		}
		execstat = true;
		assertTrue(execstat);
	}

}
