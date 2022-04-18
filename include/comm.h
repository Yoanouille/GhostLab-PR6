#ifndef COMM_H
#define COMM_H

#include "server.h"

#include "game.h"
#include "player.h"
#include "ghost.h"

int mySend(int sock,char *message,int size);
int send_game(int sock, game_list *l);

int req_newPl(player *p, char *mess, game_list **l);
int req_Regis(player *p, char *mess, game_list *l);
int req_unReg(player *p, game_list **l);
int req_Size(player *p, char *mess, game_list *l);
int req_List(player *p, char *mess, game_list *l);

int init_game(game *g);
int init_joueur(player_list *p, lab *l, char *welcome);
int send_ghost(game *g, int x, int y);
player *get_winner(player_list *p, player *best_p);
int move(char *mess, player *p, game *g, int dir);

int req_glis(game *g, player *p);
int send_mess_all(char *mess, int len, player *p);
int send_mess_perso(char *req, int len, player *p);

#endif