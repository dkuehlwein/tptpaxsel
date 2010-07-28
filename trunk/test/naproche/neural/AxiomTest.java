package test.naproche.neural;

import org.junit.Test;

import junit.framework.TestCase;
import naproche.neural.Axiom;

public class AxiomTest extends TestCase {
	Axiom axiom1 = new Axiom();
	Axiom axiom2 = new Axiom("fof('testName',axiom,! [X] : X=X,unknown,[relevance(0.9783)]).");

	@Test
	public final void testAxiom() {
		assertEquals(null, axiom1.name);
		assertEquals(null, axiom1.formula);
		assertEquals(0, axiom1.scoreAprils);
		assertEquals(0, axiom1.scoreNaproche);	
		assertEquals(0.0, axiom1.scoreAprilsDouble);
	}

	@Test
	public final void testAxiomString() {
		assertEquals("testName", axiom2.name);
		assertEquals("! [X] : X=X", axiom2.formula);
		assertEquals(0, axiom2.scoreAprils);
		assertEquals(0, axiom2.scoreNaproche);
		assertEquals(0.9783, axiom2.scoreAprilsDouble);
	}

}
