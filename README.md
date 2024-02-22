# GhostLab-PR6
GhostLab PR6 Projet de programmation réseau Licence 3 UPC


----------------------------
Comment compiler le projet ?
----------------------------

Rentrer dans le repertoire du projet puis faites :

    make

Cela compilera tout le projet . 

Si vous voulez compiler seulement le server ou le client il suffit de faire :

    make server
    make Main.class -> (pour le client)


-------------------------------
Executer le server et le client
-------------------------------


Pour lancer le server il vous suffit de faire:

    ./server [port]

où [port] sera le port TCP du server


Pour le client il faut lancer:

    java Main [server_ip] [port]


où [server_ip] est l'adresse ip du server (ou le nom de la machine) et [port] le port du server (le port en argument du server)

Exemples :
------------------------------
$ make server
$ ./server 6666

------------------------------

$ make 
$ java Main lulu 6666 
(lance un client qui va se connecter à un server qui est sur lancé sur machine lulu et dont ce serveur écoute sur le port 6666)


------------
Jouer au jeu
------------
Pour jouer au jeu il vous faut créer une partie (ou en rejoindre une). Pour cela il vous suffit de rentrer un pseudo de 8 
caractères (vous ne pourrez pas en mettre plus) et d'appuyer sur "Create a new Game"

Si vous voulez rejoindre une partie il faut en sélectionner une sur le menu à gauche et appuyer sur "Register". Pour voir si il y 
a des parties il vous suffit d'appuyer sur "refresh"


Pour lancer la partie il suffit d'appuyer sur "start" vous allez alors vous retrouvez devant un écran d'attente le temps que tout
les joueurs de la partie appuyent sur start.

Une fois la partie commencée vous aurez à votre gauche le plateau et à votre droite un menu.

Pour voir les autres joueurs appuyé sur actualisé et ils apparaitrons brièvement en rouge.

Les fantômes apparaissent à chacun de leur déplacment brièvement en vert.

Pour se déplacer vous pouvez cliquer sur le flèches ou directement utiliser les flèches de votre clavier et appuyer sur entrée
pour validé le déplacment.


Les cases grises signifient que vous n'avez pas encore découvert la case.
Les cases blanches signifient que c'est un passage libre.
Les cases noires signifient que c'est un mur.
Les cases avec un petit carré noir au centre sont des pièges qui vous feront perdre 20 points à chaque passage dessus.

--------------------
Strucutre du pojet :
--------------------

Le projet est en 2 partie, une en C pour le server et une en java pour le client


~~~~~~~~~~
Partie C :
~~~~~~~~~~
On va la retrouver dans les repertoires src/ qui contient les fichiers .c et include/ qui contient les fichiers .h

Dans les .h on va retouver toutes les déclarations de vrariable et les structures telles que la structure pour le labyrinthe,
les parties, les joueurs et les fantomes. On retrouve aussi tout les include necessaire au fichier éponyme dans scr/

Les fichiers .c sont donc le code à proprement parler du server. 

    -server.c : Le fichier server.c est le fichier principal qui lance l'éxecution du programme. On y retrouve le main, le thread des joueurs (contenant le parseur des messages TCP), le thread des fantomes pour les fantomes.

    -comm.c : dans ce fichier on va retrouver toutes les fonctions d'envoie de messages aussi bien en TCP qu'en UDP, et les fonctions gérant les parties

    -game.c : On retrouve ici les fonctions afin d'ajouter et de supprimer des joueurs à une partie, créer et supprimer des parties

    -ghost.c : petit fichier avec 3 fonctions permettant la gestion des fantômes

    -lab.c : On retrouve ici des fonctions qui permettent la création d'un labyrinthe respectant les conditions posées

    -player.c : On retrouve ici les fonctions permettant la modifiaction ainsi que l'obtention des informations d'un joueurs (port, partie, score etc...)





~~~~~~~~~~~~~
Partie java :
~~~~~~~~~~~~~

On va retourer cette partie dans le repertoire scrjava/ pour les fichiers sauf Main.java qui est à la racine du projet

On peut séparer le code java en 2 parties : 


-Partie réseau:

    -ClientMulti.java : fichier java qui va gérer l'envoie et la réception de tout les messages multidiffusés
    
    -ClientTCP.java : On retrouve les fonctions pour recevoir et envoyer les messages en TCP 
    
    -ClientUDP.java : On retrouve ici les fonctions pour la reception et l'envoie de tous les messages UDP



-Partie Interface graphique:

    -Fenetre.java : classe principale de la partie graphique qui est le JFrame de l'interface et qui va regrouper les fonctions
        pour communiquer entre l'interface et les classes Client[...]
    
    -Start.java : Premier menu visible, seuelement 1 boutons présent qui lance la connexion TCP 
    
    -Acceuil.java : Le menu ou le joueur doit rentrer son pseudo et à en visuel les parties disponibles. Les boutons permettent 
        l'envoie des requètes de connexion, création de partie, liste de joueurs dans une partie...etc

    -EcranAttente.java : petit menu qui ne sert qu'a empêcher le joueur de pouvoir envoyé des requête après avoir fait start

    -MenuPartie.java : Classe qui affiche le plateau de jeu avec les joueurs, les fantômes et les pièges ainsi que le menu
        permettant les déplacement du joueur, la liste des joueurs avec leurs scores et le chat. C'est ici que tout l'affiche du jeu est géré avec le Thread d'actualisation pour la disparition progessive des joueurs et des fantômes.

    -EndScreen.java : Classe qui est la dernière scène affiché. Soit quand le joueur a quitté la partie soit quand la partie 
        c'est finie. Elle indique le gagnant de la parite et son score et permet de retourner à la 1ere scène (Start.java) 

    -PosOp.java : petite classe de stockage de valeur qui permet de placer et dessiner les joueurs/fantômes dans la classe 
        MenuPartie

    -BetterButton.java : petite Classe qui extend JButton afin d'avoir des boutons avec un style identique


