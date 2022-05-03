#include "server.h"

game_list* g_list;

pthread_mutex_t lock = PTHREAD_MUTEX_INITIALIZER;


//Function used on one message

int traitement (player *p,char* mess,int* running, int len){
    printf("%s\n", mess);
    if(strncmp(mess,"NEWPL",5) == 0){
        pthread_mutex_lock(&lock);
        int r = req_newPl(p, mess, &g_list);
        pthread_mutex_unlock(&lock);
        return r;

    } else if (strncmp(mess,"REGIS",5) == 0){
        //Register the player
        pthread_mutex_lock(&lock);
        int r = req_Regis(p, mess, g_list);
        pthread_mutex_unlock(&lock);
        return r;

    }else if (strncmp(mess,"UNREG",5) == 0){
        //Unregister the player
        pthread_mutex_lock(&lock);
        int r = req_unReg(p, &g_list);
        pthread_mutex_unlock(&lock);
        return r;
    }else if (strncmp(mess,"SIZE?",5) == 0){
        //Send size of the maze
        pthread_mutex_lock(&lock);
        int r = req_Size(p, mess, g_list);
        pthread_mutex_unlock(&lock);
        return r;
    }else if (strncmp(mess,"LIST?",5) == 0){
        pthread_mutex_lock(&lock);
        int r = req_List(p, mess, g_list);
        pthread_mutex_unlock(&lock);
        return r;
    }else if (strncmp(mess,"GAME?",5) == 0){
        //List of still not launched games
        pthread_mutex_lock(&lock);
        send_game(p->sock, g_list);
        pthread_mutex_unlock(&lock);
        return EXIT_SUCCESS;
    }else if (strncmp(mess,"START",5) == 0){
        //Start the game if all players sent start
        pthread_mutex_lock(&lock);
        p->bool_start_send = 1;
        if(p->his_game != NULL) {
            if(all_started(p->his_game->players)) {
                //Init la game
                init_game(p->his_game);
                //Lancer un Thread pour la game !
                pthread_mutex_unlock(&lock);
            } else {  
                pthread_mutex_unlock(&lock);  
                //RIEN
            }
        } else pthread_mutex_unlock(&lock);
        return EXIT_SUCCESS;
    }else if(strncmp(mess, "UPMOV", 5) == 0) {
        pthread_mutex_lock(&lock);
        move(mess, p, p->his_game, 0);
        pthread_mutex_unlock(&lock);
        return EXIT_SUCCESS;
    }
    else if(strncmp(mess, "DOMOV", 5) == 0) {
        pthread_mutex_lock(&lock);
        move(mess, p, p->his_game, 1);
        pthread_mutex_unlock(&lock);
        return EXIT_SUCCESS;
    }
    else if(strncmp(mess, "LEMOV", 5) == 0) {
        pthread_mutex_lock(&lock);
        move(mess, p, p->his_game, 2);
        pthread_mutex_unlock(&lock);
        return EXIT_SUCCESS;
    }
    else if(strncmp(mess, "RIMOV", 5) == 0) {
        pthread_mutex_lock(&lock);
        move(mess, p, p->his_game, 3);
        pthread_mutex_unlock(&lock);
        return EXIT_SUCCESS;

    } else if(strncmp(mess, "GLIS?", 5) == 0) {
        pthread_mutex_lock(&lock);
        req_glis(p->his_game, p);
        pthread_mutex_unlock(&lock);
        return EXIT_SUCCESS;

    } else if(strncmp(mess, "IQUIT", 5) == 0) {
        char message [] = "GOBYE***";
        mySend(p->sock,message,8);
        return EXIT_FAILURE;

    } else if (strncmp(mess, "MALL?", 5) == 0){
        char *m = mess + 6;
        len -= 6;
        pthread_mutex_lock(&lock);
        send_mess_all(m, len, p);
        pthread_mutex_unlock(&lock);
        return EXIT_SUCCESS;

    } else if(strncmp(mess, "SEND?", 5) == 0) {
        pthread_mutex_lock(&lock);
        send_mess_perso(mess, len, p);
        pthread_mutex_unlock(&lock);
        return EXIT_SUCCESS;
    }
    else {
        char message [] = "GOBYE***";
        mySend(p->sock,message,8);
        return EXIT_FAILURE;
    }
    return EXIT_FAILURE;
}


/* Here the TCP connection is established
You treat the communication protocol between server and ONE client
*/
void *communication(void *arg){
    srand(time(NULL));
    player *p = (player *) (arg);
    //First message send is the number of games
    char message[10];
    memcpy(message,"GAMES n***",10);
    
    pthread_mutex_lock(&lock);
   
    send_game(p->sock, g_list);
    pthread_mutex_unlock(&lock);
    
    //Buffer where we receive data
    char buff[SIZE + 1];
    memset(buff, 0, SIZE + 1);

    //Futur full message
    char mess[200];
    memset(mess, 0, 200);
    int x = 0;
    int pre = 0;
    int prepre = 0;
    int running = 1;
    while(running) {
        int re = recv(p->sock, buff, SIZE,0);
        //printf("%d\n", re);
        if(re ==-1){
            perror("recv");
            break;
        }else if(re == 0){
            dprintf(2,"Client closed connection\n");
            break;
        }
        for(int i = 0; i < re; i++) {
            if(buff[i] == '*') {
                
                if(prepre) {
                    if(traitement(p,mess,&running, x) != EXIT_SUCCESS){
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

    printf("FIN THREAD\n");

    pthread_mutex_lock(&lock);
    if(p->his_game != NULL) {
        remove_player_game(p->his_game, p->sock);
        if(p->his_game->num_player == 0) {
            g_list = remove_game(g_list, p->his_game->id);
        }
    }
    pthread_mutex_unlock(&lock);
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