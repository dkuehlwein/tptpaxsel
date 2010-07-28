package naproche.neural;

import java.util.Comparator;

public class AxiomFinalComparator implements Comparator<Axiom>{
	public int compare(Axiom a1, Axiom a2) {
		if ( a1.scoreFinal > a2.scoreFinal )
			return -1;
		else if (a1.scoreFinal < a2.scoreFinal)
			return 1;
		else
			return 0;
	}
}