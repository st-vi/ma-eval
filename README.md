# ma-eval
This repository contains the code to encode pseudo-boolean constraints in cnf and in pseudo boolean constraints for the evaluation of my masters thesis.

# build
mvn clean compile assembly:single

# dependencies
currently it is necessary to install the java-fm-metamodel locally in the maven repo. This might change as soon as it is in the offical maven repo.
Furthermore, it is necessary to install the featureide library for dimacs encoding.