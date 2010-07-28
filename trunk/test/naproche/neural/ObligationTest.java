package test.naproche.neural;

import java.io.IOException;

import junit.framework.TestCase;

import naproche.neural.Axiom;
import naproche.neural.Obligation;

import org.junit.Test;

public class ObligationTest extends TestCase{
	public String testLocation = "/home/rekzah/workspace/naproche/examples/landau/6-apod(6)-holds(apod(6), 10, 0).input.aprils";
	public Obligation obligation;
	
	@Test
	public final void testObligation() {
		try {
			obligation = new Obligation(testLocation);
			// Conjecture Test
			assertEquals("holds(apod(6), 10, 0)", obligation.conjecture.name);
			assertEquals("(vsucc(vd7) != vsucc(vd8) )", obligation.conjecture.formula);
			Axiom a1 = obligation.premises.get(0);
			assertEquals("holds(prot(6), 9, 0)", a1.name);
			assertEquals(0.981095, a1.scoreAprilsDouble, 0);
			assertEquals(1, a1.scoreAprils);
			a1 = obligation.premises.get(1);
			assertEquals("ass(cond(proof(7), 0), 0)", a1.name);
			assertEquals(0.981095, a1.scoreAprilsDouble, 0);
			assertEquals(2, a1.scoreAprils);
			a1 = obligation.premises.get(3);
			assertEquals("qu(cond(axiom(1), 0), holds(scope(axiom(1)), 2, 0))", a1.name);
			assertEquals(0.948360, a1.scoreAprilsDouble, 0);
			assertEquals(4, a1.scoreAprils);
		} catch (IOException e) {
			fail("Could not find test File");
		}		
	}
}
