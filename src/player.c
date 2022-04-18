#include "player.h"



player *genPlayer(int sock, struct sockaddr_in addr, int x, int y, char *id){
    player  *p = malloc(sizeof(player));
    if(p == NULL){
        perror("malloc genPlayer");
        exit(1);
    }
    p->sock = sock;
    p->addr = addr;
    p->x = x;
    p->y = y;
    p->score = 0;
    p->bool_start_send = 0;
    p->his_game = NULL;
    
    memset(p->id, 0, 9);
    strncpy(p->id, id, 8);

    return p;
}

player_list *add_player_gen(player_list *list, int sock, struct sockaddr_in addr, int x, int y, char *id) {
    if(list == NULL) {
        player_list *l = malloc(sizeof(player_list));
        if(l == NULL) {
            perror("malloc player add_player");
            exit(EXIT_FAILURE);
        }
        l->p = genPlayer(sock, addr, x, y, id);
        l->next = NULL;
        return l;
    }
    list->next = add_player_gen(list->next, sock, addr, x, y, id);
    return list;
}

player_list *add_player(player_list *list,player*p){
    if(list == NULL) {
        player_list *l = malloc(sizeof(player_list));
        if(l == NULL) {
            perror("malloc player add_player");
            exit(EXIT_FAILURE);
        }
        l->p = p;
        l->next = NULL;
        return l;
    }
    list->next = add_player(list->next, p);
    return list;
}

int size_player_list(player_list *l) {
    if(l == NULL) return 0;
    return 1 + size_player_list(l->next);
}

//Peut être remove avec l'id ?
player_list *remove_one_player_list(player_list *l, int sock) {
    if(l == NULL) return NULL;
    if(l->p->sock == sock){
        player_list *n = l->next;
        free(l);
        return n;
    } 
    l->next = remove_one_player_list(l->next, sock);
    return l;
}

void remove_all(player_list *l) {
    if(l == NULL) return;
    remove_all(l->next);
    free(l);
}

player *get_player(player_list *l, char *id) {
    if(l == NULL) return NULL;
    if(!strncmp(l->p->id, id,8)) return l->p;
    return get_player(l->next, id);
}

player *get_player_from_sock(player_list *l, int sock){
    if(l == NULL) return NULL;
    if(l->p->sock == sock) return l->p;
    return get_player_from_sock(l->next, sock);
}

void print_player_list(player_list *l) {
    if(l == NULL) {
        printf("\n");
        return;
    }
    printf("%d ", l->p->sock);
    print_player_list(l->next);
}