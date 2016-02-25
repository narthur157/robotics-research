package tests;

import java.io.IOException;
import server.BrickComm;
import server.BrickListener;
import server.BrickState;
import server.Logger;

public abstract class Test implements BrickListener {
	protected BrickComm comm;
	protected Logger logger;
	protected int changeFlag = 0;
	protected BrickState bs, prevBs;
	
	private int testCount = 0, numLoops = 0, stableCount = 0;
	
	public Test(BrickComm commInit) {
		comm = commInit;
	}
	
	final public void runTest(int numLoopsIn) throws IOException {
		numLoops = numLoopsIn;
		
		try {
			comm.addListener(this);
			
			while(testCount < numLoops) {
				// I don't think this needs to be synchronized because
				// we know this will only be notified when the comparison fails
				this.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			logger.close();
		}
	}
	
	abstract public void test() throws IOException;

	// implement BrickListener
	final public void updateBrick(BrickState bsIn) {
		prevBs = bs;
		bs = bsIn;
		collectData();
		
		// only run updates once stabilized
		if (bs.equals(prevBs)) {
			stableCount++;
			if (stableCount > 50) {
				stableCount = 0;
				
				try {
					test();
					testCount++;
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (testCount >= numLoops) {
					this.notify();
				}
			}
		}
	}
	
	final public void collectData() {
		logger.logln(bs.toString() + "\t" + changeFlag);
		System.out.println(bs.toString());
	}
}
