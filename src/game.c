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
    g->id = 0;
    g->num_player = 0;
    g->bool_started = 0;

    return g;
}

void free_game(game *g) {
    destroy_lab(g->lab);
    remove_all(g->players);
    free(g);
}

void add_player_game_gen(game *g,  int sock, struct sockaddr_in addr, int x, int y, char *id) {
    g->players = add_player_gen(g->players, sock, addr, x, y, id);
    g->num_player++;
}

void add_player_game(game *g, player *p){
    g->players = add_player(g->players,p);
    g->num_player++;
}

void remove_player_game(game *g, int sock) {
    g->players = remove_one_player_list(g->players, sock);
    g->num_player--;
}

// game_list *add_game(game_list *l, game *g) {
//     if(l == NULL) {
//         game_list *n = malloc(sizeof(game_list));
//         if(n == NULL) {
//             perror("malloc add_game");
//             exit(EXIT_FAILURE);
//         }
//         n->g = g;
//         return n;
//     }
//     l->next = add_game(l->next, g);
//     return l;
// }

game_list *add_game(game_list *l, game *g) {
    if(l == NULL || l->g->id > g->id) 
    {
        game_list *n = malloc(sizeof(game_list));
        if(n == NULL) {
            perror("malloc add_game");
            exit(EXIT_FAILURE);
        }
        n->g = g;
        n->next = l;
        return n;
    }
    g->id += 1;
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

game *get_game(game_list *l, int id) {
    if(l == NULL) return NULL;
    if(l->g->id == id) return l->g;
    return get_game(l->next, id);
}

game *get_player_game(game_list *l,player *p){
    if(l==NULL) return NULL;
    if(get_player_from_sock(l->g->players,p->sock) != NULL) return l->g;
    return get_player_game(l->next,p);
}

int size_list_game(game_list *l) {
    if(l == NULL) return 0;
    else return 1 + size_list_game(l->next);
}