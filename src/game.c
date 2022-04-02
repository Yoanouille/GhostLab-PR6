#include "game.h"

game *gen_game(int w, int h) {
    game *g = malloc(sizeof(game));
    if(g == NULL) {
        perror("malloc gen_game");
        exit(EXIT_FAILURE);
    }

    g->lab = build_lab(w, h);
    gen_lab(g->lab);

    g->players = NULL;
    g->id = num_id;
    num_id += 1;

    return g;
}

void free_game(game *g) {
    destroy_lab(g->lab);
    remove_all(g->players);
    free(g);
}

void add_player_game(game *g,  int sock, struct sockaddr_in addr, int port_udp,int x, int y, char *id) {
    g->players = add_player(g->players, sock, addr, port_udp, x, y, id);
}

void remove_player_game(game *g, int sock) {
    g->players = remove_one_player_list(g->players, sock);
}

game_list *add_game(game_list *l, game *g) {
    if(l == NULL) {
        game_list *n = malloc(sizeof(game_list));
        if(n == NULL) {
            perror("malloc add_game");
            exit(EXIT_FAILURE);
        }
        n->g = g;
        return n;
    }
    l->next = add_game(l->next, g);
    return l;
}


game_list *remove_game(game_list *l, int id_game) {
    if(l == NULL) return NULL;
    if(l->g->id == id_game) {
        game_list *n = l->next;
        free(l->g);
        free(l);
        return n;
    }
    l->next = remove_game(l->next, id_game);
    return l;
}

void destroy_game_list(game_list *l) {
    if(l == NULL) return;
    game_list *n = l->next;
    free(l->g);
    free(l);
    destroy_game_list(n);
}
