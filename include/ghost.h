#ifndef GHOST_H
#define GHOST_H
#include "lab.h"

typedef struct ghost {
    int x;
    int y;
    int catched;
} ghost;


void place_ghost(ghost *g, int len, lab *l);
int all_catched(ghost *g, int len);
int is_on_ghost(ghost *g, int len, int x, int y);

#endif