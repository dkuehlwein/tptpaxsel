/**
 * 
 */
package naproche.neural;


import naproche.neural.example.Example;


/**
 * @author rekzah
 *
 */
public class Main {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {		
		Example example = new Example("Mizar");
		Obligations foo = example.obligations;
		//Default values are weightAPRILS = 1; weightObligationGraph=1; maxTime = 5 * 1000
		double weightObligationEdges = 1.0;
		double weightAPRILS = 0.0;
		double maxTime = 5 * 1000;
		foo.weightAPRILS = weightAPRILS;
		foo.weightObligationGraph = weightObligationEdges;
		foo.maxTime = 5 * 1000;
		foo.checkObligations();
	}

}
