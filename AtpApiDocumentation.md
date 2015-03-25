# Abbreviations #
  * PSA = Premise Selection Algorithm
  * ATP = Automated Theorem Prover

# Abstract #
For effective use of an ATP a Premise Selection Algorithm is set up and used, configurable by a range of parameters. This API should make the configuration and use of such PSA (and subsequently the ATP-backend) much easier.

To achieve this, the API presents itself as an abstraction-layer on a single directory (containing tptp-compatible input-files) which is chosen on construction of an API-Object (and may not be changed later).
Once set up the API may be run on all or a single file(s) in the directory and print out its findings in real-time through a PrintStream Object which may be configured independently (default is System.out). Afterwards (or during, which may lead to unexpected behavior) the configuration may be changed and everything may be rerun.

# Setup #
tptpaxsel needs to be set up according to the [installationGuide](installationGuide.md), especially $PROJECTROOT/lib needs to be in the java-classpath and must contain the APRILS and Vampire binaries (if one wishes to use APRILS and/or Vampire), additionaly the E prover needs to be in the systems PATH (if one wishes to use E).

# Parameters #
  * PrintStream outStream - Every output will be written to outStream.
  * double weightObligationEdges, weightAPRILS, weightNaproche - Weight of the Graph, APRILS and Naproche inputs for the PSAs.
  * changeExistential, deleteExistential - Set the weight of each Existential clause to 0 or delete each Existential clause. Mutually exclusive.
  * String technique - "simple" or "growth", chooses the used PSA
  * String prover - "V", "E", "EV" or "VE", selects E and/or Vampire as ATP-backend. Both can only selected with "growth"-PSA, VE and EV are identical.
  * int time - How much time the ATP has.
The following applies only to the growth-PSA
  * String growthMethod - "plus" or "mul", chooses whether selected premises are grown additively or multiplicatively
  * int growthStartValue - how many premises are selected at the beginning
  * int growthIncrValue - the addend resp. factor with which the selected premises are grown (in respect to growthMethod)
Note: When technique is "simple" all premises are selected at once.

All these parameters are marked private and may only be accessed by their respective get/set methods.

# Methods #
  * **Constructor** AtpApi(location) - initalises the API into the directory at location, in relativity to project root. (i.e. when project root is "/home/user/tptp" and location is "files/input" the API will use the directory "/home/user/tptp/files/input" and will be able to run every .input or .tptp file in this directory.
  * public void setAll(double weightObligationEdges, double weightAPRILS, double, weightNaproche,	String technique, String prover, String growthMethod, int growthStartValue, int growthIncrValue, int time, boolean changeExistential, boolean deleteExistential) - Sets all parameters at once.
  * public HashMap<String,String> getConfig - returns all Parameters (non-string will be converted to strings) as Hashmap (indexed by their name).
  * public void setSimple(double weightObligationEdges, double weightAPRILS, double weightNaproche, String prover, int time) - quickly configure the API for use with "simple"-PSA.
  * public void premiseGrowth(double weightObligationEdges, double weightAPRILS, double weightNaproche, String prover, String growthMethod, int growthStartValue, int growthIncrValue, int time) - quickly configure the API for use with "growh"-PSA
  * public ObligationStatistics runSingleByFilename(String filename) - runs selected prover with selected configuration on a single tptp-input-file, chosen by filename. Returns ObligationStatistics object with all the output-data (in addition to printing the data to outStream).
  * public ObligationStatistics runSingleByAbsolutePath(String filename) - runs selected prover with selected configuration on a single tptp-input-file, chosen by absolute path to the file. Returns ObligationStatistics object with all the output-data (in addition to printing the data to outStream).
  * public void runAll() - runs selected prover with selected configuration on all tptp-input-files in the directory, prints out additional performance-information in the end to files "testoutput" and "timeline" in the directory.
The output of all three run-Methods will be in a machine-readable format (including header for easy import in any spreadsheet-application).

# Example #
```
import tptpaxsel.!AtpApi;
[..]
!AtpApi api = new !AtpApi("/examples/landau"); //initialise API for proofchecking the example files created from Landaus book
api.setSimple(0.0,0.0,1.0, "V", 3); //use 100% Naproche-weight, the Vampire-prover (fastest) and give it 3 seconds (empirically shown to be a good time. with more time better results are achieved rarely)
api.runAll(); //Starts proving all included theorems by Landau, and printing them in a machine-readable format (tabulator-seperated) to stdout.
```

Please Note: Deeper in the framework a switch exists to make the output human-readable, but currently the API is restricted to delimiter-seperated values.
For testing purposes one may edit the source and set "machine" to "human" in every call to checkSingleObligation or checkObligations.

# Known Isuses / TODO #
With multithreading-magic or other shenanigans it sould be possible to alter the API-configuration during a run, which is in theory quite fine but due to race-conditions may cause the application to malfunction or display any other kind of unexpected behavior.
A lock should be implemented.

A complete runthrough of runAll() may take a lot of time and one may wish to terminate ahead of completion. A facility for that should be implemented.