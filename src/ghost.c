#include "ghost.h"

void place_ghost(ghost *g, int len, lab *l) {
    for(int i = 0; i < len; i++) {
        int x = 0;
        int y = 0;
        do {
            x = rand() % (l->w);
            y = rand() % (l->h);
        } while(l->tab[y][x] == 0);
        g[i].x = x;
        g[i].y = y;
        g[i].catched = 0;
    }
}

int all_catched(ghost *g, int len) {
    for(int i = 0; i < len; i++) {
        if(!g[i].catched) return 0;
    }
    return 1;
}

int is_on_ghost(ghost *g, int len, int x, int y) {
    for(int i = 0; i < len; i++) {
        if(g[i].catched == 0 && g[i].x == x && g[i].y == y) {
            g[i].catched = 1;
            return 1;
        }
    }
    return 0;
}