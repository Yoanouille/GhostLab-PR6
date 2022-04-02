#ifndef PLAYER_H
#define PLAYER_H
#include <netinet/in.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>


typedef struct player{
    int sock;
    struct sockaddr_in addr;
    int x;
    int y;
    int score;
    char id[9];
} player;

typedef struct player_list {
    player *p;
    player_list *next;
} player_list;

player *genPlayer(int sock, struct sockaddr_in addr, int x, int y, char *id);
player_list *add_player(player_list *l, int sock, struct sockaddr_in addr, int x, int y, char *id);
int size_player_list(player_list *l);
player_list *remove_one_player_list(player_list *l, int sock);
void remove_all(player_list *l);
player *get_player(player_list *l, int sock);

#endif