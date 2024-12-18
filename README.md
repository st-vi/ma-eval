# ma-eval
This repository contains the code to encode pseudo-boolean constraints in cnf and in pseudo boolean constraints for the evaluation of my masters thesis.

# build
mvn clean compile assembly:single

# dependencies
needs the pseudo-boolean-uvl-encoder project. If it is up to date in the maven repo no manual steps are needed. If not build and install it in your local maven repo:

https://github.com/st-vi/pseudo-boolean-uvl-encoder
`mvn install:install-file -Dfile=pseudo-boolean-uvl-encoder-1.0-SNAPSHOT-jar-with-dependencies.jar -DgroupId=de.vill -DartifactId=pseudo-boolean-uvl-encoder -Dversion=1.0 -Dpackaging=jar`
Furthermore, it is necessary to install the featureide library for dimacs encoding in your local maven repo.
https://featureide.github.io/
