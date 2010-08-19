package test.naproche.neural;

import org.junit.Test;

import junit.framework.TestCase;
import naproche.neural.Axiom;

public class AxiomTest extends TestCase {
	Axiom axiom1 = new Axiom();
	
	@Test
	public final void testAxiom() {
		assertEquals(null, axiom1.name);
		assertEquals(null, axiom1.formula);
		assertEquals(0, axiom1.scoreAprils);
		assertEquals(0, axiom1.scoreNaproche);	
		assertEquals(0.0, axiom1.scoreAprilsDouble);
	}

}
