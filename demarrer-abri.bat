#!/bin/bash
#
# Exemple d'utilisation : ./demarrer-abri.sh [url_abri]
#
# Lance un simulateur de noeud central avec l'URL en argument. 
# Si aucune URL n'est precisee, le programme en utilise une par defaut 
# (cf. src/modele/Adresses.java).
#
# Gwenole Lecorve & David Guennec
# ENSSAT, Universite de Rennes 1
# Novembre 2015
#

CLASS_PATH=./build/classes

java -Djava.security.policy=./security.policy -Djava.rmi.server.codebase=file:${CLASS_PATH} -cp ${CLASS_PATH} AbriSimulateur $1

