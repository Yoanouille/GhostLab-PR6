#include "game.h"


game *gen_game(int w, int h) {
    game *g = malloc(sizeof(game));
    if(g == NULL) {
        perror("malloc gen_game");
        exit(EXIT_FAILURE);
    }

    w = (rand() % 10) + 10;
    h = (rand() % 10) + 10;
    int aire = w * h;
    int nb_ghost = (aire * 5) / 100;
    printf("w*h : %d*%d nb_ghost : %d\n", w, h, nb_ghost);

    g->lab = build_lab(w, h);
    gen_lab(g->lab);
    gen_piege(g->lab, 10);

    g->players = NULL;
    g->id = 0;
    g->num_player = 0;
    g->bool_started = 0;
    g->finished = 0;
    g->sock_udp = 0;
    g->thread_g = NULL;
    g->nb_ghost = nb_ghost;
    g->ghosts = malloc(nb_ghost * sizeof(ghost));
    if(g->ghosts == NULL) {
        perror("malloc ghosts");
        exit(1);
    }

    return g;
}

void free_game(game *g) {
    destroy_lab(g->lab);
    close(g->sock_udp);
    free(g->ghosts);
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
        free_game(l->g);
        free(l);
        return n;
    }
    l->next = remove_game(l->next, id_game);
    return l;
}

int all_started(player_list *pl) {
    if(pl == NULL) return 1;
    return pl->p->bool_start_send && all_started(pl->next);
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


int size_list_game(game_list *l) {
    if(l == NULL) return 0;
    else return 1 + size_list_game(l->next);
}
int size_list_game_active(game_list *l) {
    if(l == NULL) return 0;
    else return (l->g->bool_started ? 0 : 1) + size_list_game_active(l->next);   
}


void print_game(game *g) {
    printf("ID : %d, SOCKUDP : %d, NUM_PLAYER : %d, STARTED: %d\n", g->id, g->sock_udp,g->num_player,g->bool_started);
}

void print_list_game(game_list *g) {
    if(g == NULL) {
        printf("\n");
        return;
    }
    print_game(g->g);
    print_list_game(g->next);
}

void init_ghost(ghost *g, int len, lab *l, player_list *lp) {
    for(int i = 0; i < len; i++) {
        g[i].catched = 0;
        g[i].x = 0;
        g[i].y = 0;
    }
    place_ghost(g, len, l, lp);
}

void place_ghost(ghost *g, int len, lab *l, player_list *lp) {
    for(int i = 0; i < len; i++) {
        int x = 0;
        int y = 0;
        do {
            x = rand() % (l->h);
            y = rand() % (l->w);
        } while(l->tab[x][y] == 0 || is_on_player(lp, x, y) || is_on_ghost_not_catched(g, len, x, y));
        g[i].x = x;
        g[i].y = y;
    }
}