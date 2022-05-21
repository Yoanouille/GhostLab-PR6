#ifndef GAME_H
#define GAME_H
#include <endian.h>
#include <pthread.h>
#include <sys/eventfd.h>
#include <unistd.h>
#include "player.h"
#include "lab.h"
#include "ghost.h"

typedef struct player_list player_list;
typedef struct player player;

//Structure stockant les données d'une game
typedef struct game {
    int id;
    int num_player;
    player_list *players;
    lab *lab;
    int bool_started;
    int finished;
    int sock_udp;
    struct sockaddr_in addr;
    ghost *ghosts;
    int nb_ghost;
    pthread_t *thread_g;
 
} game;

//Structure de liste de game
typedef struct game_list game_list;
struct game_list{
    game_list *next;
    game *g;
};

//Construit une partie
game *gen_game(int w, int h);

//Libère les ressources d'une partie
void free_game(game *g);

//Ajoute un player dans la liste de joueur d'une game
void add_player_game_gen(game *g,  int sock, struct sockaddr_in addr, int x, int y, char *id);
void add_player_game(game *g, player *p);

//Enlève un joueur de la partie
void remove_player_game(game *g, int sock);

//renvoie si tous les joueurs ont appuyé sur start
int all_started(player_list *pl);

//Ajoute la game à la liste
game_list *add_game(game_list *l, game *g);

//Enlève la game de la liste
game_list *remove_game(game_list *l, int id_game);

//Détruit toute la liste de parties
void destroy_game_list(game_list *l);

//Permet d'obtenir une partie à partir de son id
game *get_game(game_list *l, int id);

//Permet d'obtenir la nombre de parties
int size_list_game(game_list *l);

//Permet d'obtenir le nombre de parties non lancées
int size_list_game_active(game_list *l);

//Fonctions d'affichage
void print_game(game *g);
void print_list_game(game_list *g);

//Initialise les fantomes sur le labyrinthe de la partie
void init_ghost(ghost *g, int len, lab *l, player_list *lp);
void place_ghost(ghost *g, int len, lab *l, player_list *lp);

#endif