# Introduction #

This text explains how to set up and run tptpaxsel using eclipse on a linux machine.

# Set Up #
  * Make sure you have Java, SVN and Eclipse installed on your System.
  * Download the source:
```
svn checkout http://tptpaxsel.googlecode.com/svn/trunk/ tptpaxsel-read-only
```
  * Open Eclipse and create a new java project.
  * Select "Create project from existing source" and browse to the source code.
  * Click next. Select "Libraries" and add all JARs in \lib. Also add the library JUnit4.

# Installing the ATP #
tptpaxsel uses the ATPs E and Vampire. You need to install at least one of them.

## Installing E ##
  * Go to www.eprover.org.
  * Download the source.
  * Install it to /usr/local/bin

## Installing Vampire ##
  * Go to www.vprover.org.
  * Go to downloads and fill out the form.
  * Download the binaries from the provided link.
  * Put the vampire\_lin32 file in tptpaxsel/lib/VAMPIRE/

# Using tptpaxsel #
Now that all the preliminaries are done with, we can start using tptpaxsel.

  * Open Eclipse and open the file Main.java in src/tptpaxsel
  * Copy and paste the following code in the main method:
```
Example example = new Example("Landau");
Obligations obligations = example.obligations;
obligations.weightAPRILS = weightAPRILS;
obligations.weightNaproche = weightNaproche;
obligations.weightObligationGraph = weightObligationEdges;
obligations.maxTime = maxTime;
PSASimple psa = new PSASimple("E",5);
psa.changeCheckSettings(obligations);
psa.logInfo();
obligations.checkObligations();
```
  * If you want to use vampire instead of e replace the PSASimple line with:
```
PSASimple psa = new PSASimple("V",5);
```
  * Run the code.