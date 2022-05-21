#include "comm.h"
#include <endian.h>


static int multi_port = 1111;

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

void send_ogame(game_list *l, char *mess) {
    if(l == NULL) return;
    if(l->g->bool_started == 0) {
        char mess_bis[] = "OGAME m s***";
        mess_bis[6] = l->g->id;
        mess_bis[8] = l->g->num_player;
        memcpy(mess, mess_bis, 12);
        send_ogame(l->next, mess + 12);
    } else send_ogame(l->next, mess);
}


int send_game(int sock, game_list *l) {
    uint8_t nb_game = size_list_game_active(l);
    char mess[10 + nb_game * 12];
    memset(mess, 0, 10 + nb_game * 12);


    memcpy(mess, "GAMES n***", 10);

    mess[6] = nb_game;
    //printf("%d\n", nb_game);
    //mySend(sock,mess, 10);

    send_ogame(l, mess + 10);

    if(mySend(sock, mess, 10 + nb_game * 12) == -1) return EXIT_FAILURE;

    return EXIT_SUCCESS;
}

int req_newPl(player *p, char *mess, game_list **l) {
    memcpy(p->id, mess+6,8);
    p->id[8] = '\0';
    //printf("ID : %s\n", p->id);
    game *g = gen_game(10,10);
    add_player_game(g,p);
    p->his_game = g;
    *l = add_game(*l,g);
    if(g->id == 255 || p->bool_start_send){
        *l = remove_game(*l,255);
        p->his_game = NULL;
        char no[] = "REGNO***";
        if(mySend(p->sock,no,8) == -1) return EXIT_FAILURE;
        return EXIT_SUCCESS;
    }
    
    char port[5];
    memcpy(port,mess+15,4);
    port[4] = '\0';
    p->addr.sin_port = htons(atoi(port));
    char ok[] = "REGOK m***";
    ok[6] = g->id;
    if(mySend(p->sock,ok,10) == -1) return EXIT_FAILURE;
    return EXIT_SUCCESS;
}

int req_Regis(player *p, char *mess, game_list *l) {
    memcpy(p->id, mess+6,8);
    p->id[8] = '\0';
    char m = mess[20];
    game *g = get_game(l,m);
    if(g == NULL || get_player(g->players,p->id) != NULL || p->bool_start_send || g->bool_started){
        char no[] = "REGNO***";
        if(mySend(p->sock,no,8) == -1) return EXIT_FAILURE;
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
    if(mySend(p->sock,ok,10) == -1) return EXIT_FAILURE;
    return EXIT_SUCCESS;
}

int req_unReg(player *p, game_list **l, void *(*ghost_thread)(void *)) {
    game *g = p->his_game;
    if(g == NULL || p->bool_start_send || g->bool_started){
        char dunno[] = "DUNNO***";
        if(mySend(p->sock,dunno,8) == -1) return EXIT_FAILURE;
        return EXIT_SUCCESS;
    }
    int id = g->id;

    remove_player_game(g,p->sock);
    if(g->num_player == 0) {
        *l = remove_game(*l, g->id);
    } else if(g->bool_started == 0) {
        start_game(p, ghost_thread);
    }
    p->his_game = NULL;
    char ok[] = "UNROK m***";
    ok[6] = id;
    printf("UNREG %d***\n", id);
    if(mySend(p->sock,ok,10) == -1) return EXIT_FAILURE;

    return EXIT_SUCCESS;
}

int req_Size(player *p, char *mess, game_list *l) {
    int m = mess[6];
    game *g = get_game(l,m);
    if(g == NULL || p->bool_start_send){
        char dunno[] = "DUNNO***";
        if(mySend(p->sock,dunno,8) == -1) return EXIT_FAILURE;
        return EXIT_SUCCESS;
    }
    char res[] = "SIZE! m hh ww***";
    res[6] = m;

    *(uint16_t *)(res + 8) = htole16(g->lab->h);
    *(uint16_t *)(res + 11) = htole16(g->lab->w);

    if(mySend(p->sock, res, 16) == -1) return EXIT_FAILURE;
    return EXIT_SUCCESS;
}

void req_list_player(player_list *pl, char *mess) {
    if(pl == NULL) return;
    char res[] = "PLAYR idididid***";
    memcpy(res + 6, pl->p->id, 8);
    memcpy(mess, res, 17);
    req_list_player(pl->next, mess + 17);
}

int req_List(player *p, char *mess, game_list *l) {
    int m = mess[6];
    game *g = get_game(l, m);
    if(g == NULL || p->bool_start_send){
        char dunno[] = "DUNNO***";
        if(mySend(p->sock,dunno,8) == -1) return EXIT_FAILURE;
        return EXIT_SUCCESS;
    }

    char res[12 + 17 * g->num_player];
    memcpy(res, "LIST! m s***", 12);
    res[6] = m;
    res[8] = g->num_player;
    //mySend(p->sock, res, 12);

    req_list_player(g->players, res + 12);

    if(mySend(p->sock, res, 12 + 17 * g->num_player) == -1) return EXIT_FAILURE;
    return EXIT_SUCCESS;
}

int getPort(game *g) {
    while(has_port(g->players, multi_port)) {
        multi_port++;
    }
    return multi_port;
}

void start_game(player *p, void *(*ghost_thread)(void *)) {
    if(all_started(p->his_game->players)) {
        //Init la game
        p->his_game->bool_started = 1;
        init_game(p->his_game);
        pthread_t *thread_ghost = malloc(sizeof(pthread_t));
        if(thread_ghost == NULL) {
            perror("malloc pthread ghost");
            exit(1);
        }
        pthread_create(thread_ghost,NULL,ghost_thread,p->his_game);
        p->his_game->thread_g = thread_ghost;
        //Lancer un Thread pour la game !
    }
}

int init_game(game *g) {
    //Initialisation de la socket UDP
    int his_port = getPort(g);
    multi_port++;
    int sock = socket(PF_INET, SOCK_DGRAM, 0);
    if(sock == -1) {
        perror("socket udp init_game");
        exit(EXIT_FAILURE);
    }
    g->addr.sin_family = AF_INET;
    g->addr.sin_port = htons(his_port);
    if(inet_aton("225.1.2.4", &g->addr.sin_addr) == 0) {
        dprintf(2, "Erreur inet_aton multicast init game\n");
        exit(EXIT_FAILURE);
    }

    g->sock_udp  = sock;
    init_ghost(g->ghosts, g->nb_ghost, g->lab, g->players);
    print_lab_2(g->lab);

    //Initialisation Welcome
    char welcome[] = "WELCO m hh ww f 225.1.2.4###### port***";
    welcome[6] = g->id;
    welcome[8] = (g->lab->h)%256;
    welcome[9] = (g->lab->h)/256;
    welcome[11] = (g->lab->w)%256;
    welcome[12] = (g->lab->w)/256;
    welcome[14] = g->nb_ghost;
    snprintf(welcome + 32, 8, "%d***", his_port);    

    //Initialisation des joueurs
    return init_joueur(g->players, g->lab, g->ghosts,welcome);
}

int init_joueur(player_list *p, lab *l, ghost *g,char *welcome) {
    if(p == NULL) return EXIT_SUCCESS;
    int x = 0;
    int y = 0;
    do {
        x = rand() % (l->h);
        y = rand() % (l->w);
    } while(l->tab[x][y] == 0 || is_on_ghost_not_catched(g, p->p->his_game->nb_ghost, x, y));

    p->p->x = x;
    p->p->y = y;

    int r = EXIT_SUCCESS;
    if(mySend(p->p->sock, welcome, 39) == -1) r = EXIT_FAILURE;
    char mess[42];
    snprintf(mess, 42, "POSIT %s %03d %03d***", p->p->id, x, y);
    if(mySend(p->p->sock, mess, 25) == -1) r = EXIT_FAILURE;    
    if(r == EXIT_SUCCESS) r = init_joueur(p->next, l, g,welcome);
    return r;
}

int send_ghost(game *g, int x, int y) {
    char mess[50];
    snprintf(mess, 50, "GHOST %03d %03d+++", x, y);
    int r = sendto(g->sock_udp, mess, 16, 0, (struct sockaddr *)&(g->addr), sizeof(struct sockaddr_in));
    return r;
}

int send_score(player *p, int x, int y, int point) {
    char mess[100];
    snprintf(mess, 100, "SCORE %s %04d %03d %03d+++", p->id, point, x, y);
    int r = sendto(p->his_game->sock_udp, mess, 30, 0, (struct sockaddr *)&(p->his_game->addr), sizeof(struct sockaddr_in));
    return r;
}

player *get_winner(player_list *p, player *best_p) {
    if(p == NULL) return best_p;
    if(best_p == NULL) return get_winner(p->next, p->p);
    if(p->p->score > best_p->score) return get_winner(p->next, p->p);
    else return get_winner(p->next, best_p);
}

int send_end(game *g) {
    player *best = get_winner(g->players, NULL);

    char mess[50];
    snprintf(mess, 50, "ENDGA %s %04d+++", best->id, best->score);
    int r = sendto(g->sock_udp, mess, 22, 0, (struct sockaddr *)&(g->addr), sizeof(struct sockaddr_in));
    return r;
}

int send_move(player *p) {
    char mess[42];
    snprintf(mess, 42, "MOVE! %03d %03d***", p->x, p->y);
    if(mySend(p->sock, mess, 16) == -1) return EXIT_FAILURE;
    return EXIT_SUCCESS;
}

int send_move_points(player *p) {
    char mess[42];
    snprintf(mess, 42, "MOVEF %03d %03d %04d***", p->x, p->y, p->score);
    if(mySend(p->sock, mess, 21) == -1) return EXIT_FAILURE;
    return EXIT_SUCCESS;
}

int send_trap(game *g, player *p, int x, int y, int score) {
    char mess[50];
    snprintf(mess, 50, "TRAP! %03d %03d %04d+++", x, y, score);
    int r = sendto(g->sock_udp, mess, 21, 0,(struct sockaddr *) &(p->addr), sizeof(struct sockaddr_in));
    return r;
}

//0 -> UP | 1 -> DOWN | 2 -> LEFT | 3 -> RIGHT
int move(char *mess, player *p, game *g, int dir) {
    int d = atoi(mess + 6);
    printf("Il se déplace de %d cases\n", d);
    int score = 0;
    int end = 0;
    int dx = 0;
    int dy = 0;
    switch(dir) {
        case 0 : 
            dx = -1;
            dy = 0;
            break;
        case 1 : 
            dx = 1;
            dy = 0;
            break;
        case 2 :
            dx = 0;
            dy = -1;
            break;
        case 3 :
            dx = 0;
            dy = 1;
            break;
    }
    int bool_score = 0;
    int bool_trap = 0;
    for(int i = 0; i < d; i++) {
        bool_score = 0;
        bool_trap = 0;
        if(p->y + dy < 0 || p->y+dy >= g->lab->w || p->x+dx <0 ||  p->x+dx >= g->lab->h) break;
        if(g->lab->tab[p->x + dx] [p->y + dy]== 0) break;
        if(is_on_ghost(g->ghosts, g->nb_ghost, p->x + dx, p->y + dy)){
            score += 100;
            bool_score = 1;
            
            if(all_catched(g->ghosts, g->nb_ghost)) {
                end = 1;
            }
        } 
        if(g->lab->tab[p->x + dx][p->y + dy] == 2) {
            score -= 20;
            bool_trap = 1;   
        }
        if(bool_score) send_score(p, p->x + dx, p->y + dy, p->score + score);
        if(bool_trap) {
            if ((p->score + score ) < 0 ){
                send_trap(g, p, p->x + dx, p->y + dy, 0);
            }else {
                send_trap(g, p, p->x + dx, p->y + dy, p->score + score);
            }
        }
        p->x += dx;
        p->y += dy;
    }
    if ((p->score + score ) < 0 ){
        p->score = 0;
    }else {
        p->score += score;
    }
    int re;
    if(score == 0) re = send_move(p);
    else re = send_move_points(p);

    if(end){ //ENVOYER MESSAGE END
        send_end(p->his_game);
        p->his_game->finished = 1;
    }
    return re;
}

void send_gplyr(player_list *p, char *mess) {
    if(p == NULL) return;
    char mess_bis[100];
    memset(mess_bis, 0, 100);
    snprintf(mess_bis, 100, "GPLYR %s %03d %03d %04d***", p->p->id, p->p->x, p->p->y, p->p->score);
    memcpy(mess, mess_bis, 30);
    send_gplyr(p->next, mess + 30);
}

int req_glis(game *g, player *p) {
    char res[10 + g->num_player * 30];
    memcpy(res, "GLIS! s***", 10);
    res[6] = g->num_player;

    send_gplyr(g->players, res + 10);
    if(mySend(p->sock, res, 10 + g->num_player * 30) == -1) return EXIT_FAILURE;
    return EXIT_SUCCESS;
}

int send_mess_all(char *mess, int len, player *p) {
    char copy[len + 1];
    strncpy(copy, mess, len);
    copy[len] = 0;
    printf("MESS : %s\n", copy);

    char res[18 + len + 1];
    memcpy(res, "MESSA ", 6);
    memcpy(res + 6, p->id, 8);
    res[14] = ' ';
    memcpy(res + 15, mess, len);
    memcpy(res + 15 + len, "+++", 3);

    res[len + 18] = 0;
    printf("REPONSE MALL : %s\n", res);
 
    int r = sendto(p->his_game->sock_udp, res, 18 + len, 0, (struct sockaddr *)&p->his_game->addr, sizeof(struct sockaddr_in));
    if(r == -1) return EXIT_FAILURE;

    char rep[] = "MALL!***";
    if(mySend(p->sock, rep, 8) == -1) return EXIT_FAILURE;
    
    return EXIT_SUCCESS;
}

int send_mess_perso(char *req, int len, player *p) {
    char id[9];
    memcpy(id, req + 6, 8);
    id[8] = 0;

    char *mess = req + 15;
    len -= 15;

    player *p_to = get_player(p->his_game->players, id);
    if(p_to == NULL) {
        char rep[] = "NSEND***";
        if(mySend(p->sock, rep, 8) == -1) return EXIT_FAILURE;
        return EXIT_SUCCESS;
    }

    char res[18 + len + 1];
    memcpy(res, "MESSP ", 6);
    memcpy(res + 6, p->id, 8);
    res[14] = ' ';
    memcpy(res + 15, mess, len);
    memcpy(res + 15 + len, "+++", 3);
    res[len + 18] = 0;

    printf("REPONSE MESSP : %s\n", res);

    int r = sendto(p->his_game->sock_udp, res, 18 + len, 0, (struct sockaddr *)&p_to->addr, sizeof(struct sockaddr_in));
    if(r == -1) return EXIT_FAILURE;

    char rep[] = "SEND!***";
    if(mySend(p->sock, rep, 8) == -1) return EXIT_FAILURE;
    return EXIT_SUCCESS;
}