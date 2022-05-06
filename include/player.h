#ifndef PLAYER_H
#define PLAYER_H
#include <netinet/in.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "game.h"

typedef struct game game;

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

typedef struct player_list player_list;
struct player_list {
    player *p;
    player_list *next;
};

player *genPlayer(int sock, struct sockaddr_in addr, int x, int y, char *id);
player_list *add_player_gen(player_list *l, int sock, struct sockaddr_in addr, int x, int y, char *id);
player_list *add_player(player_list *list,player*p);
int size_player_list(player_list *l);
player_list *remove_one_player_list(player_list *l, int sock);
void remove_all(player_list *l);
player *get_player(player_list *l, char *id);
player *get_player_from_sock(player_list *l, int sock);

void print_player_list(player_list *l);
int is_on_player(player_list *l, int x, int y);

#endif