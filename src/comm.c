#include "comm.h"
#include <endian.h>


int mySend(int sock,char* message,int size){
    int size_send = 0;
    do{
        int r = send(sock,message + size_send, size - size_send,0);
        if(r == -1){
            perror("send");
            return -1;
        }
        if(r == 0) {
            return 0;
        }
        size_send += r;

    }while(size_send < size);
    return size_send;
}


void send_game(int sock, game_list *l) {
    char mess[] = "GAMES n***";
    uint8_t nb_game = size_list_game(l);
    mess[6] = nb_game;
    printf("%d\n", nb_game);
    mySend(sock,mess, 10);
}

void send_ogame(int sock, game_list *l) {
    if(l == NULL || l->g->bool_started) return;
    char mess[] = "OGAME m s***";
    mess[6] = l->g->id;
    mess[8] = l->g->num_player;
    mySend(sock, mess, 12);
    send_ogame(sock, l->next);
}

int req_newPl(player *p, char *mess, game_list **l) {
    memcpy(p->id, mess+6,8);
    p->id[8] = '\0';
    game *g = gen_game(WIDTH,HEIGHT);
    add_player_game(g,p);
    p->his_game = g;
    *l = add_game(*l,g);
    if(g->id == 255 || p->bool_start_send){
        *l = remove_game(*l,255);
        p->his_game = NULL;
        char no[] = "REGNO***";
        mySend(p->sock,no,8);
        return EXIT_SUCCESS;
    }
    
    char port[5];
    memcpy(port,mess+15,4);
    port[4] = '\0';
    p->addr.sin_port = htons(atoi(port));
    char ok[] = "REGOK m***";
    ok[6] = g->id;
    mySend(p->sock,ok,10);
    return EXIT_SUCCESS;
}

int req_Regis(player *p, char *mess, game_list *l) {
    memcpy(p->id, mess+6,8);
    p->id[8] = '\0';
    char m = mess[20];
    game *g = get_game(l,m);
    if(get_player(g->players,p->id) != NULL || p->bool_start_send || g->bool_started){
        char no[] = "REGNO***";
        mySend(p->sock,no,8);
        return EXIT_SUCCESS;
    }
    
    
    add_player_game(g,p);
    p->his_game = g;

    char port[5];
    memcpy(port,mess+15,4);
    port[4] = '\0';
    p->addr.sin_port = htons(atoi(port));

    char ok[] = "REGOK m***";
    ok[6] = g->id;
    mySend(p->sock,ok,8);
    return EXIT_SUCCESS;
}

int req_unReg(player *p, game_list **l) {
    game *g = p->his_game;
    if(g == NULL || p->bool_start_send || g->bool_started){
        char dunno[] = "DUNNO***";
        mySend(p->sock,dunno,8);
        return EXIT_SUCCESS;
    }

    remove_player_game(g,p->sock);
    //Est-ce qu'on remove la game si il y a plus de joueur ?
    char ok[] = "UNROK m***";
    ok[6] = g->id;
    mySend(p->sock,ok,10);

    return EXIT_SUCCESS;
}

int req_Size(player *p, char *mess, game_list *l) {
    int m = mess[6];
    game *g = get_game(l,m);
    if(g == NULL || p->bool_start_send){
        char dunno[] = "DUNNO***";
        mySend(p->sock,dunno,8);
        return EXIT_SUCCESS;
    }
    char res[] = "SIZE! m hh ww***";
    res[6] = m;

    *(uint16_t *)(res + 8) = htole16(g->lab->h);
    *(uint16_t *)(res + 11) = htole16(g->lab->w);

    mySend(p->sock, res, 16);
    return EXIT_SUCCESS;
}

int req_List(player *p, char *mess, game_list *l) {
    int m = mess[6];
    game *g = get_game(l, m);
    if(g == NULL || p->bool_start_send){
        char dunno[] = "DUNNO***";
        mySend(p->sock,dunno,8);
        return EXIT_SUCCESS;
    }

    char res[] = "LIST! m s***";
    res[6] = m;
    res[8] = g->num_player;
    mySend(p->sock, res, 12);

    req_list_player(p->sock, g->players);
    return EXIT_SUCCESS;
}

void req_list_player(int sock, player_list *pl) {
    if(pl == NULL) return;
    char res[] = "PLAYR idididid***";
    memcpy(res + 6, pl->p->id, 8);
    mySend(sock, res, 17);
    req_list_player(sock, pl->next);
}