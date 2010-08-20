package naproche.neural;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import tptp_parser.SimpleTptpParserOutput;
import tptp_parser.TptpLexer;
import tptp_parser.TptpParser;

/**
 * Provides the means to parse the output of the ATPs EP 1.0, EP 1.2 and Vampire 1.0
 * 
 * @author Daniel Kühlwein
 *
 */
public class ATPOutputParser {

	public BufferedReader in;
	public BufferedWriter out;		
	public Vector<Axiom> usedAxioms;
	
	public ATPOutputParser(BufferedReader in, BufferedWriter out) {		
		this.in = in;
		this.out = out;		
		usedAxioms = new Vector<Axiom>();
	}
	
	/**
	 * Very ugly part: We first parse the input line by line. When the proof starts, we concat lines that correspond to formulas and parse these lines
	 * with the tptp parser. Best way I found so far to parse E and Vampire output.	 
	 * 
	 * @param graph The proof Graph
	 * @param conjecture The name of the conjecture. Used to add edges to the graph.
	 * @param weightUsedGraph The weight of the edges added to the graph
	 * @return
	 */
	public boolean parse(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph, String conjectureName, double weightUsedGraph) {
		boolean checkResult = false;
		String axiomName;
		String line;
		String nextline;
		DefaultWeightedEdge edge;
		/* getNewLine is used to keep track whether or not we already read a new line or not. This happens when a line starts with fof.
		 * without getNewLine, the loop might skip lines. */
		boolean getNewLine = true; 
		try {
			// -------------------- Add the used graph ------------------------				
			while ((line = in.readLine()) != null) {	    				    			   			
				out.write(line);
				out.write("\r\n");
				/* If we have a proof, start a second loop and parse the proof */
				if ( line.startsWith("# SZS status Theorem") | line.startsWith("% SZS status Theorem")) {
					checkResult = true;	
				}
				if (line.startsWith("# SZS output start") | line.startsWith("% SZS output start")) {					
					while ( !(line.startsWith("# SZS output end") | line.startsWith("% SZS output end"))) {
						if (getNewLine) {
							line = in.readLine();
							out.write(line);
							out.write("\r\n");
						}
						getNewLine = true;
						/* We might have multiline formulas */
						if (line.startsWith("fof")) {
							getNewLine = false;
							nextline = in.readLine();
							out.write(nextline);
							out.write("\r\n");
							while ( !(nextline.startsWith("fof") | nextline.startsWith("cnf") | nextline.startsWith("#") | nextline.startsWith("%")) ) {
								line = line+nextline;
								nextline = in.readLine();
								out.write(nextline);
								out.write("\r\n");
							} 
							/* At this point, line is a fof formula in string form that can be parser with the tptp parser which is what we do */
							TptpLexer lexer = new TptpLexer(new StringReader(line));
							TptpParser parser = new TptpParser(lexer);
							SimpleTptpParserOutput parserOutput = new SimpleTptpParserOutput();
							String role;
							try {
								for (SimpleTptpParserOutput.TopLevelItem formula = 
									(SimpleTptpParserOutput.TopLevelItem)parser.topLevelItem(parserOutput);
								formula != null;
								formula = (SimpleTptpParserOutput.TopLevelItem)parser.topLevelItem(parserOutput))
								{
									role = ((SimpleTptpParserOutput.AnnotatedFormula)formula).getRole().toString();				
									if (role.equals("axiom")) {									
										axiomName = ((SimpleTptpParserOutput.AnnotatedFormula)formula).getAnnotations().toString();
										axiomName = axiomName.split("',")[1];
										if (axiomName.startsWith("'")) {
											axiomName = axiomName.substring(1, axiomName.length()-2);
										} else {
											axiomName = axiomName.substring(0, axiomName.length()-1);
										}
										edge = new DefaultWeightedEdge();
										graph.addEdge(conjectureName, axiomName, edge);
										graph.setEdgeWeight(edge, weightUsedGraph);								
										usedAxioms.add(new Axiom(axiomName, (SimpleTptpParserOutput.AnnotatedFormula)formula));
									}
								}		 
							}
							// General ANTLR exception, provides diagnostics.
							catch (antlr.ANTLRException e) {
								System.err.println("Syntax error in " + "test" + ": " + e);
							}
							
							/* Reset the line an continue parsing */
							line = nextline;
						}
						
					}
					out.write(line);
					out.write("\r\n");
				}
			}
		} catch (IOException e) {
			System.out.println("Cannot parse prover output");
			e.printStackTrace();
		}		
		return checkResult;
	}
}
