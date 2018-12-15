JC = javac
.SUFFIXES: .java .class
.java.class: 
	$(JC) $(JFLAGS) $*.java

CLASSES = ContractorBrokerApp.java

all:	clean classes
default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
	$(RM) *.java~
	$(RM) suncertify/db/*.class
	$(RM) suncertify/db/*.java~
	$(RM) suncertify/server/*.class
	$(RM) suncertify/server/*.java~
	$(RM) suncertify/server/gui/*.class
	$(RM) suncertify/server/gui/*.java~
	$(RM) suncertify/common/*.class
	$(RM) suncertify/common/*.java~
	$(RM) suncertify/client/*.class
	$(RM) suncertify/client/*.java~
	$(RM) suncertify/client/gui/*.class
	$(RM) suncertify/client/gui/*.java~
	$(RM) suncertify/common/gui/*.class
	$(RM) suncertify/common/gui/*.java~
