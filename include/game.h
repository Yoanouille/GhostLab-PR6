#ifndef GAME_H
#define GAME_H
#include "player.h"
#include "lab.h"

typedef struct game {
    int id;
    int num_player;
    player_list *players;
    lab *lab;
    int bool_started;
    //et les fantomes
    //peut être le nombre de joueurs ? mais on peut l'avoir avec players
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

game_list *add_game(game_list *l, game *g);
game_list *remove_game(game_list *l, int id_game);
void destroy_game_list(game_list *l);
game *get_game(game_list *l, int id);
game *get_player_game(game_list *l,player *p);
int size_list_game(game_list *l);

#endif