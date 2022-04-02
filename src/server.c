#include "server.h"

#define SIZE 8



int NUMBER_GAMES = 0;



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
    
}


//Function used on one message

int traitement (int sock,char* mess,int* running){
    if(strncmp(mess,"NEWPL",5) == 0){
        //Create a game
        return EXIT_SUCCESS;
    } else if (strncmp(mess,"REGIS",5) == 0){
        //Register the player
        return EXIT_SUCCESS;
    }else if (strncmp(mess,"UNREG",5) == 0){
        //Unregister the player
        return EXIT_SUCCESS;
    }else if (strncmp(mess,"SIZE",5) == 0){
        //Send size of the maze
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
        mySend(sock,message,8);
        return EXIT_FAILURE;
    }
}



/* Here the TCP connection is established
You treat the communication protocol between server and ONE client
*/
void *communication(void *arg){
    int sock = *(int *) (arg);
    //First message send is the number of games
    char message[10];
    memcpy(message,"GAMES n***",10);
    message[6] = NUMBER_GAMES;
    mySend(sock,message,sizeof(message));

    for(int i = 0; i < NUMBER_GAMES; i++){
        //TODO send the [OGAME_m_s***] messages
    }
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
        int re = recv(sock, buff, SIZE,0);
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
                    if(traitement(sock,mess,&running) != EXIT_SUCCESS){
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

    close(sock);
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
        int *sock_client = malloc(sizeof(int));
        *sock_client = accept(sock_serv,(struct sockaddr *)&caller,&size);
        if(*sock_client < 0){
            perror("accept");
            return EXIT_FAILURE;
        }

        //One thread per client
        pthread_t th;
        if(pthread_create(&th,NULL,communication,sock_client) != 0){
            perror("Thread_create");
            return EXIT_FAILURE;
        }
    }
    assert(close(sock_serv)==0);
    return EXIT_SUCCESS;
}