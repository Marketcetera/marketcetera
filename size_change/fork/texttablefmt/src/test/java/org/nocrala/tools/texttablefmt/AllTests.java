package org.nocrala.tools.texttablefmt;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;

public class AllTests {

  private static Logger logger = Logger.getLogger(AllTests.class);

  public static Test suite() {
    TestSuite ts = new TestSuite();
    ts.addTestSuite(CellTests.class);
    ts.addTestSuite(RowTests.class);
    ts.addTestSuite(TableTests.class);
    ts.addTestSuite(TableColSpanTests.class);
    ts.addTestSuite(StreamingTableTests.class);
    return ts;
  }

  public static void main(final String[] args) {
    logger.debug("Starting tests...");
    Test ts = suite();
    TestResult tr = junit.textui.TestRunner.run(ts);
    if (!tr.wasSuccessful()) {
      System.exit(1);
    }
  }

}
