#ifndef PLAYER_H
#define PLAYER_H
#include <netinet/in.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "game.h"

typedef struct game game;

//Structure stockant les données du joueur
typedef struct player{
    int sock;
    struct sockaddr_in addr;
    int x;
    int y;
    int score;
    char id[9];
    int bool_start_send;
    game *his_game;
} player;

//Structure de liste de joueurs
typedef struct player_list player_list;
struct player_list {
    player *p;
    player_list *next;
};

//Génère un joueur
player *genPlayer(int sock, struct sockaddr_in addr, int x, int y, char *id);

//Ajoute un joueurs dans la liste de joueurs
player_list *add_player_gen(player_list *l, int sock, struct sockaddr_in addr, int x, int y, char *id);
player_list *add_player(player_list *list,player*p);

//Permet de connaître la taille de la liste de joueur
int size_player_list(player_list *l);

//Enlève un joueur de la liste
player_list *remove_one_player_list(player_list *l, int sock);

//Enlève tous les joueurs de la liste
void remove_all(player_list *l);

//Permet de récupérer un joueur précis
player *get_player(player_list *l, char *id);
player *get_player_from_sock(player_list *l, int sock);

//Affiche les joueurs de la liste
void print_player_list(player_list *l);

//Permet de savoir si on est sur un joueur
int is_on_player(player_list *l, int x, int y);

//Permet de savoir si un joueur a le port "port", utile pour créer la socket de multicast 
int has_port(player_list *l, int port);

#endif