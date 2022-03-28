#ifndef PLAYER_H
#define PLAYER_H
#include <netinet/in.h>

typedef struct player{
    int x;
    int y;
    int score;
    char id[6];
    struct sockaddr_in addr;
} player;

player * genPlayer();

#endif