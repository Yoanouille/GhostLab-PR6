#ifndef GHOST_H
#define GHOST_H
#include "lab.h"

typedef struct ghost {
    int x;
    int y;
    int catched;
} ghost;

//Savoir si tous les fantomes sont tous attrapés
int all_catched(ghost *g, int len);

//Savoir si le joueur/ou fantome est sur un fantome non attrapé
int is_on_ghost_not_catched(ghost *g, int len, int x, int y);

//Savoir si le joueur/ou fantome est sur un fantome
int is_on_ghost(ghost *g, int len, int x, int y);

#endif