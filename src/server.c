#include "server.h"

#define SIZE 8

int traitement (char* mess,int* running){


    return EXIT_SUCCESS;
}



/* Here the TCP connection is established
You treat the communication protocol between server and ONE client
*/
void  *communication(void *arg){
    int sock = *(int *) (arg);

      char buff[SIZE + 1];
    memset(buff, 0, SIZE + 1);

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
                    
                    printf("%s\n", mess);
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
            return 1;
        }
        if(re < SIZE) break;
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
        pthread_t th;

        
        if(pthread_create(&th,NULL,communication,sock_client) != 0){
            perror("Thread_create");
            return EXIT_FAILURE;
        }
    }
    assert(close(sock_serv)==0);
    return EXIT_SUCCESS;
}