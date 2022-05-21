# GhostLab-PR6
GhostLab PR6 Projet de programmation réseau Licence 3 UPC


##Comment compiler le projet ?

Rentrer dans le repertoire du projet puis faites :

```
make
```
Cela compilera tout le projet . 

Si vous voulez compiler seulement le server ou le client il suffit de faire :
```
make server
make Main.class -> (pour le client)
```

##Executer le server et le client

Pour lancer le server il vous suffit de faire 
```
./server [port]
```
où [port] sera le port TCP du server


Pour le client il faut lancer 
```
java Main [server_ip] [port]
```
où [server_ip] est l'adresse ip du server (ou le nom de la machine) et [port] le port du server (le port en argument du server)