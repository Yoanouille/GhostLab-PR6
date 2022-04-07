#include "server.h"

#define SIZE 8
#define HEIGHT 20
#define WIDTH 20

game_list* g_list;

pthread_mutex_t lock = PTHREAD_MUTEX_INITIALIZER;



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

    }while(size_send < size);
    return size_send;
}

//Function used on one message

int traitement (player *p,char* mess,int* running){
    if(strncmp(mess,"NEWPL",5) == 0){
        memcpy(p->id, mess+6,8);
        p->id[8] = '\0';
        game *g = gen_game(WIDTH,HEIGHT);
        add_player_game(g,p);
        pthread_mutex_lock(&lock);
        g_list = add_game(g_list,g);
        if(g->id == 255 || p->bool_start_send){
            g_list = remove_game(g_list,255);
            char no[] = "REGNO***";
            mySend(p->sock,no,8);
            pthread_mutex_unlock(&lock);
            return EXIT_FAILURE;
        }
        pthread_mutex_unlock(&lock);
        
        char port[4];
        memcpy(port,mess+15,4);
        p->addr.sin_port = htons(atoi(port));
        char ok[] = "REGOK m***";
        ok[6] = g->id;
        mySend(p->sock,ok,8);
        return EXIT_SUCCESS;

    } else if (strncmp(mess,"REGIS",5) == 0){
        //Register the player
        char id[8];
        memcpy(id, mess+6,8);
        char m = mess[20];
        pthread_mutex_unlock(&lock);
        game *g = get_game(g_list,m);
        if(get_player(g->players,id) != NULL || p->bool_start_send || g->bool_started){
            char no[] = "REGNO***";
            mySend(p->sock,no,8);
            pthread_mutex_unlock(&lock);
            return EXIT_FAILURE;
        }
        
        
        add_player_game(g,p);
        pthread_mutex_unlock(&lock);

        char port[4];
        memcpy(port,mess+15,4);
        p->addr.sin_port = htons(atoi(port));

        char ok[] = "REGOK m***";
        ok[6] = g->id;
        mySend(p->sock,ok,8);
        return EXIT_SUCCESS;


    }else if (strncmp(mess,"UNREG",5) == 0){
        //Unregister the player
        pthread_mutex_lock(&lock);
        game *g = get_player_game(g_list,p);
        if(g == NULL || p->bool_start_send || g->bool_started){
            char dunno[] = "DUNNO***";
            mySend(p->sock,dunno,8);
            pthread_mutex_unlock(&lock);
            return EXIT_FAILURE;
        }

        remove_player_game(g,p->sock);
        pthread_mutex_unlock(&lock);
        char ok[] = "UNROK m***";
        ok[6] = g->id;
        mySend(p->sock,ok,10);
        return EXIT_SUCCESS;
    }else if (strncmp(mess,"SIZE!",5) == 0){
        //Send size of the maze
        int m = mess[6];

        pthread_mutex_lock(&lock);
        game *g = get_game(g_list,m);
        if(g == NULL || p->bool_start_send){
            char dunno[] = "DUNNO***";
            mySend(p->sock,dunno,8);
            pthread_mutex_unlock(&lock);
            return EXIT_FAILURE;
        }
        char size[] = "SIZE! m hh ww***";
        size[6] = m;


        return EXIT_SUCCESS;
    }else if (strncmp(mess,"LIST",5) == 0){
        //List of other players
        return EXIT_SUCCESS;
    }else if (strncmp(mess,"GAME",5) == 0){
        //List of still not launched games
        return EXIT_SUCCESS;
    }else if (strncmp(mess,"START",5) == 0){
        //Start the game if all players sent start
        return EXIT_SUCCESS;
    }else {
        char message [] = "GOBYE***";
        mySend(p->sock,message,8);
        return EXIT_FAILURE;
    }
}


void send_ogame(game_list *l, int sock) {
    if(l == NULL) return;
    char *mess = "OGAME m s***";
    mess[6] = l->g->id;
    mess[8] = l->g->num_player;
    mySend(sock, mess, 12);
    send_ogame(l->next, sock);
}


/* Here the TCP connection is established
You treat the communication protocol between server and ONE client
*/
void *communication(void *arg){
    player *p = (player *) (arg);
    //First message send is the number of games
    char message[10];
    memcpy(message,"GAMES n***",10);
    pthread_mutex_lock(&lock);
    uint8_t nb_game = size_list_game(g_list);
    message[6] = nb_game;
    mySend(p->sock,message,sizeof(message));
    send_ogame(g_list, p->sock);
    pthread_mutex_unlock(&lock);
    
    //Buffer where we receive data
    char buff[SIZE + 1];
    memset(buff, 0, SIZE + 1);

    //Futur full message
    char mess[200];
    memset(buff, 0, 200);
    int x = 0;
    int pre = 0;
    int prepre = 0;
    int running = 1;
    while(running) {
        int re = recv(p->sock, buff, SIZE,0);
        if(re ==-1){
            perror("recv");
            exit(EXIT_FAILURE);
        }else if(re == 0){
            dprintf(2,"Client closed connection");
            exit(EXIT_FAILURE);
        }
        for(int i = 0; i < re; i++) {
            if(buff[i] == '*') {
                
                if(prepre) {
                    if(traitement(p,mess,&running) != EXIT_SUCCESS){
                        running = 0;
                        break;
                    }
                    memset(mess, 0, 200);
                    x = 0;
                } 
                else if(pre) prepre = 1;
                else pre = 1;
            } else {
                pre = 0;
                prepre = 0;
                mess[x] = buff[i];
                x++;
            }
        }
        if(re == -1) {
            perror("read");
            return NULL ;
        }
    }

    close(p->sock);
    free(arg);
    return NULL;
}


int main(int argc, char const *argv[]){
    if(argc != 2){
        dprintf(2,"Wrong number of arguments\n");
        return EXIT_FAILURE;
    }
    int port = atoi(argv[1]);
    if (port < 0 || port >65535){
        dprintf(2,"Choose port's number between 1024 and 9999\n"); 
        return EXIT_FAILURE;
    }
    int sock_serv;
    if((sock_serv = socket(AF_INET,SOCK_STREAM,0)) <= 0 ){
        perror("serv_sock");
        return EXIT_FAILURE;
    }
    struct sockaddr_in addr;
    addr.sin_family=PF_INET;
    addr.sin_addr.s_addr=htonl(INADDR_ANY);
    addr.sin_port=htons(port);

    if(bind(sock_serv,(struct sockaddr *) &addr, sizeof(addr)) < 0 ){
        perror("bind");
        return EXIT_FAILURE;
    }
    
    if(listen(sock_serv,0) != 0){
        perror("listen");
        return EXIT_FAILURE;
    }
    while (1){
        struct sockaddr_in caller;
        socklen_t size = sizeof(caller); 
        int sock_client = accept(sock_serv,(struct sockaddr *)&caller,&size);
        if(sock_client < 0){
            perror("accept");
            return EXIT_FAILURE;
        }
        player *p = genPlayer(sock_client,caller, 0, 0, "NULLNULL");
        //One thread per client
        pthread_t th;
        if(pthread_create(&th,NULL,communication,p) != 0){
            perror("Thread_create");
            return EXIT_FAILURE;
        }
    }
    assert(close(sock_serv)==0);
    return EXIT_SUCCESS;
}