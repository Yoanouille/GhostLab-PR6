#ifndef GAME_H
#define GAME_H
#include <endian.h>
#include <pthread.h>
#include <sys/eventfd.h>
#include "player.h"
#include "lab.h"
#include "ghost.h"

#define nb_ghost 5

typedef struct player_list player_list;
typedef struct player player;

typedef struct game {
    int id;
    int num_player;
    player_list *players;
    lab *lab;
    int bool_started;
    int finished;
    int sock_udp;
    struct sockaddr_in addr;
    ghost ghosts[nb_ghost];
    pthread_t *thread_g;
 
} game;

typedef struct game_list game_list;
struct game_list{
    game_list *next;
    game *g;
};

game *gen_game(int w, int h);
void free_game(game *g);

void add_player_game_gen(game *g,  int sock, struct sockaddr_in addr, int x, int y, char *id);
void add_player_game(game *g, player *p);

void remove_player_game(game *g, int sock);
int all_started(player_list *pl);

game_list *add_game(game_list *l, game *g);
game_list *remove_game(game_list *l, int id_game);
void destroy_game_list(game_list *l);
game *get_game(game_list *l, int id);
//game *get_player_game(game_list *l,player *p);
int size_list_game(game_list *l);
int size_list_game_active(game_list *l);

void print_game(game *g);
void print_list_game(game_list *g);

#endif