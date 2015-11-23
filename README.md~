# repartiteur-laser
Projet d'algorithmique distribuée à l'ENSSAT

## Énoncé

Nous souhaitons tester un dispositif permettant de brancher plusieurs fibres optiques et de transmettre les informations transitant sur d'une de ces fibres vers l'une des autres. Nous nommons ce dispositif répartiteur laser dans la suite du document. Il est fait appel à vous pour développer la partie logicielle permettant un contrôle réparti des informations échangées.
Le répartiteur laser est schématisé en figure 1. Celui-ci se présente sous la forme d'une sphère sur laquelle viennent se connecter des fibres optiques. Un prisme situé au centre de la sphère peut être orienté de manière à rediriger un laser issu du connecteur avec une fibre optique vers le connecteur d'une autre fibre optique. 
Chaque connecteur est dotée d'un micro-processeur. Les connecteurs sont reliés entre eux de telle sorte que n'importe lequel peut dialoguer, si besoin, avec n'importe quel autre et chaque connecteur peut également envoyer des ordres à une puce qui assure l'orientation du prisme. Le rôle des connecteurs des fibres optiques est donc de :

1. détecter l'arrivée d'informations ;

2. enregistrer la fibre destinataire (identifiée par une URL) vers laquelle les informations doivent être transmises ;

3. demander le droit d'utiliser le prisme ;

4. ordonner une nouvelle orientation du prisme ;

5. transmettre les informations grâce à un laser ;

6. restituer le droit d'utilisation du prisme à la fin de l'émission.

Dans ce projet, nous ne nous intéressons qu'aux étapes 3 et 6 chargées d'assurer l'exclusion mutuelle sur l'utilisation du prisme.

## Utilisation
4 scripts sont disponibles : lancer-prisme.sh, lancer-connecteur.sh, tester-prime.sh et tester-application.sh. Une explication est donnée au début de chaque fichier.
