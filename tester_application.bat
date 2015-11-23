#!/bin/bash
#
# Exemple d'utilisation : ./tester-application.sh
#
# 
# Lance la simulation du reseau VaultChat pour l'exemple donne dans le sujet. 
# L'annuaire RMI est demarre, puis stoppe par le script.
#
# Gwenole Lecorve & David Guennec
# ENSSAT, Universite de Rennes 1
# Novembre 2015
#

set CLASS_PATH=./build/classes

cd $CLASS_PATH
start rmiregistry 2020
cd - > /dev/null
sleep 0.2
java -Djava.security.policy=./security.policy -Djava.rmi.server.codebase=file:${CLASS_PATH} -cp ${CLASS_PATH} NoeudCentralSimulateur rmi://localhost:2020/noeud-central &
TIMEOUT /T 0.2 /NOBREAK 
java -Djava.security.policy=./security.policy -Djava.rmi.server.codebase=file:${CLASS_PATH} -cp ${CLASS_PATH} AbriSimulateur rmi://localhost:2020/abri31 &
TIMEOUT /T 0.2 /NOBREAK 
java -Djava.security.policy=./security.policy -Djava.rmi.server.codebase=file:${CLASS_PATH} -cp ${CLASS_PATH} AbriSimulateur rmi://localhost:2020/abri57 &
TIMEOUT /T 0.2 /NOBREAK 
java -Djava.security.policy=./security.policy -Djava.rmi.server.codebase=file:${CLASS_PATH} -cp ${CLASS_PATH} AbriSimulateur rmi://localhost:2020/abri58 &
TIMEOUT /T 0.2 /NOBREAK 
java -Djava.security.policy=./security.policy -Djava.rmi.server.codebase=file:${CLASS_PATH} -cp ${CLASS_PATH} AbriSimulateur rmi://localhost:2020/abri71 &
TIMEOUT /T 0.2 /NOBREAK 
java -Djava.security.policy=./security.policy -Djava.rmi.server.codebase=file:${CLASS_PATH} -cp ${CLASS_PATH} AbriSimulateur rmi://localhost:2020/abri108 
taskkill rmiregistry
