package naproche.neural;

import java.util.Comparator;

public class AxiomAPRILSComparator implements Comparator<Axiom>{
	public int compare(Axiom a1, Axiom a2) {
		if ( a1.scoreAprilsDouble > a2.scoreAprilsDouble )
			return -1;
		else if (a1.scoreAprilsDouble < a2.scoreAprilsDouble)
			return 1;
		else
			return 0;
	}
}
