#----Set TPTP_HOME if not already imported from environment
TPTP_HOME ?= /home/graph/tptp
JJPARSER = $(TPTP_HOME)/ServiceTools/JJParser

CC = gcc
CFLAGS = -Wall -O3 -I$(JJPARSER) -D TPTP_HOME=\"$(TPTP_HOME)\"

OBJS = $(JJPARSER)/ListStatistics.o $(JJPARSER)/List.o $(JJPARSER)/Examine.o $(JJPARSER)/Modify.o $(JJPARSER)/Tree.o $(JJPARSER)/TreeStatistics.o $(JJPARSER)/ParseTPTP.o $(JJPARSER)/ParseTSTP.o $(JJPARSER)/PrintTSTP.o $(JJPARSER)/Parsing.o $(JJPARSER)/Signature.o $(JJPARSER)/Tokenizer.o $(JJPARSER)/Utilities.o $(JJPARSER)/Compare.o $(JJPARSER)/Interpret.o $(JJPARSER)/PrintXML.o $(JJPARSER)/PrintDFG.o $(JJPARSER)/PrintOtter.o $(JJPARSER)/PrintKIF.o $(JJPARSER)/PrintSUMO.o $(JJPARSER)/FileUtilities.o $(JJPARSER)/SystemOnTPTP.o

all: aprils

aprils: $(OBJS) aprils.o 
	$(CC) $(CFLAGS) -o $@ -lm $(OBJS) aprils.o

.c.o:
	$(CC) $(CFLAGS) -c -o $*.o $*.c

clean:
	rm -f *.o *~ aprils aprils.exe
