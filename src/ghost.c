#include "ghost.h"

int all_catched(ghost *g, int len) {
    for(int i = 0; i < len; i++) {
        if(!g[i].catched) return 0;
    }
    return 1;
}

int is_on_ghost_not_catched(ghost *g, int len, int x, int y) {
    for(int i = 0; i < len; i++) {
        if(g[i].catched == 0 && g[i].x == x && g[i].y == y) {
            return 1;
        }
    }
    return 0;
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