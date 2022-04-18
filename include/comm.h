#ifndef COMM_H
#define COMM_H

#include "server.h"

#include "game.h"
#include "player.h"
#include "ghost.h"

int mySend(int sock,char *message,int size);
void send_game(int sock, game_list *l);
void send_ogame(game_list *l, char *mess);

int req_newPl(player *p, char *mess, game_list **l);
int req_Regis(player *p, char *mess, game_list *l);
int req_unReg(player *p, game_list **l);
int req_Size(player *p, char *mess, game_list *l);
int req_List(player *p, char *mess, game_list *l);

void init_game(game *g);
void init_joueur(player_list *p, lab *l, char *welcome);
void move(char *mess, player *p, game *g, int dir);

void req_glis(game *g, player *p);

#endif