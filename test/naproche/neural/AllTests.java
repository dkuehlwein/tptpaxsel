package test.naproche.neural;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for test.naproche.neural");
		//$JUnit-BEGIN$
		suite.addTestSuite(AxiomTest.class);
		suite.addTestSuite(ObligationTest.class);
		//$JUnit-END$
		return suite;
	}

}
