# Explication des classes et méthodes

## Broker
Classe intermédiaire pour la création des canaux de connexion entre les différentes tâches de notre système. Chaque brocker possède un nom unique permettant de l'identifier. Elle possède deux methodes : 

- **accept** retournant un channel (canal de connexion) prenant en parèmetre un numéro de port (entier postif). Cette methode est bloquante donc lorsqu'elle est appelé sur un objet de type broker celui ci ne peut plus envoyer d'autre demande de connexion ou réaliser des demandes de connexion tant que la première demande accept n'est pas traiter.
ON a donc deux différents cas : 
	> la methode accept aboutit, on retourne donc un channel de communication
	> le methode accept n'aboutit pas après un certain temps, on retourne une exception de type RuntimeException.
	
- **connect** : elle fonctionne pareillement que la même manière que accept mais en plus du port de connexion prend également en apramètre le nom du brocker de connexion.
Cette methode n'est déclenchée qu'après avoir reçu un accept; elle est également bloquante et le port avec lequel la methode connect est appelé doit être la même que celui avec lequel on a reçu la demande "acdept" car le channel renvoyé dans les deux cas est le même.
Si on est toujours bloqué dans la methode connect après un certain temps, on lève une exception de type RuntimeException.

## Channel
Cette classe représente les canaux de connexion entre nos différentes tâches. Ces canaux sont bidirectionnels, FIFO, sans perte et les données transmits sont sous forme de flux continue d'octets. Elle possède 4 méthodes :

- **read** : cette methode prend en paramètre un tableau de octets dans lequel sont stocké les données lui dans le channel, le numéro de début de lecture et la longueur de lecture souhaité; elle renvoie le nombre d'octet réellement lu.
Cette opération est bloquante donc lorsqu'elle est exécuté sur un channel par une tâche, celle-ci ne peut plus exécuter une autre instruction (une autre méthode) sur le même channel. On sort du blocaque lorsque le nombre renvoyé par read égale à la longueur que l'on voulait lire initialement. Si il y a un problème lors de la lecture cette methode renovie -1

- **write** : Cette methode prend en paramètre un tableau de octets dans lequel se trouvent les données à écrire dans le canal, le numéro de debut d'écriture dans le canal et la longueur du message à écrire; elle renvoie le nombre d'octet réellement écris.
Cette opération est bloquante de lamême manière que le read.

- **disconnect** : cette méthode permet de déconnecter le canal du coté de la tâche que l'appelle. Ainsi, lorque cette methode est appélé : 
	> sur le thread appelant : 
	si une opération de lecture était en cours, celle si s'arrête automatiquement, les bits restant dans le canal sont détruits/jetés et la methode read renvoie -1; si une opération d'écriture était en cours, elle et intérompue, le retse des données à écrire est déruit et la methode renvoie -1.
	> sur le thread de l'autre coté du cannal :
	si une opération de lecture ou d'écriture était en cours, celle si se termine avant constat de la rupture du canal; ensuite le canal est également déconnectée par ce thread.

- **disconnected** : renvoie true si le le canal est déconnecté ou false sinon.

## Task
Cette classe encapsule l'exécution de nos classes (serveur, client etc...). Son construteur comprend deux arguments : un Broker et un runnable. Les runble sont implémentés par nos classe serveur/client. IL a une principale méthode statique getBrocker qui renvoie le brocker utiliser dans toute l'application.


