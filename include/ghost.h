#ifndef GHOST_H
#define GHOST_H
#include "lab.h"

typedef struct ghost {
    int x;
    int y;
    int catched;
} ghost;

int all_catched(ghost *g, int len);
int is_on_ghost_not_catched(ghost *g, int len, int x, int y);
int is_on_ghost(ghost *g, int len, int x, int y);

#endif