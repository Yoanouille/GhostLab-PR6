#ifndef COMM_H
#define COMM_H

#include "server.h"

#include "game.h"
#include "player.h"
#include "ghost.h"

//Fonction qui envoie un message de taille size en TCP en utilisant la socket sock
int mySend(int sock,char *message,int size);

//Envoie le message GAME! suivit des OGAME
int send_game(int sock, game_list *l);

//Gère la requête NEWPL
int req_newPl(player *p, char *mess, game_list **l);

//Gère la requête REGIS
int req_Regis(player *p, char *mess, game_list *l);

//Gère la requête UNREG
int req_unReg(player *p, game_list **l, void *(*ghost_thread)(void *));

//Gère la requête SIZE?
int req_Size(player *p, char *mess, game_list *l);

//Gère la requête LIST?
int req_List(player *p, char *mess, game_list *l);

//Permet de lancer la partie si il faut la lancer. ghost_thread est la fonction qui gère le thread des fantomes
void start_game(player *p, void *(*ghost_thread)(void *));

//Permet d'initialisé la partie, i.e. créer la sock udp multicast, placer les fantomes et les joueurs et envoyer le message WELCO
int init_game(game *g);

//Initialise les joueurs dans une partie
int init_joueur(player_list *p, lab *l, ghost *g,char *welcome);

//envoie le message GHOST en multi
int send_ghost(game *g, int x, int y);

//permet d'obtenir le gagnant d'un partie
player *get_winner(player_list *p, player *best_p);

//Gère les déplacements du joueur et envoie tous les messages qu'il faut (UDP + TCP + MULTI)
int move(char *mess, player *p, game *g, int dir);

//Gère la requête GLIS?
int req_glis(game *g, player *p);

//Gère l'envoie des messages multi
int send_mess_all(char *mess, int len, player *p);

//Gère les messages UDP
int send_mess_perso(char *req, int len, player *p);

#endif