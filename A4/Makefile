JFLAGS = -g
JC = javac
JAVA = java
.SUFFIXES: .java .class
.java.class:
	$(JC) -cp "libs/*" $(JFLAGS) *.java

CLASSES = ModelYouTube.java Main.java Canvas.java View.java Observer.java MyFlowLayout.java VideoDataType.java VideoPanel.java 

default: classes

classes: $(CLASSES:.java=.class)

ifeq ($(OS),Windows_NT)
run: default
	$(JAVA) -cp "libs/*;" Main
clean:
	del *.class
else
run: default
	$(JAVA) -cp ":libs/*" Main
clean:
	rm *.class
endif

