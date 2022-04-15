#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>
#include <string.h>
#include <endian.h>
#include <sys/eventfd.h>
#include <sys/select.h>


int max = 3;

int count = 0;

pthread_mutex_t lock = PTHREAD_MUTEX_INITIALIZER;

typedef struct argu {
    int sock;
    int fd;

} argu;

void *comm(void *arg) {
    printf("Bonjour Client !\n");
    argu *a = ((argu *)arg);
    int fd = a->fd;
    int sock = a->sock;

    int bool = 0;

    pthread_mutex_lock(&lock);
    count++;
    if(count == max) {
        bool = 1;
        //REVEILLER
        uint64_t c = 1;
        if(write(fd, &c, sizeof(uint64_t)) == -1) {
            perror("write");
            exit(1);
        }
    }
    pthread_mutex_unlock(&lock);

    if(!bool) {
        int fdmax = fd + 1;
        fd_set fdread;
        FD_ZERO(&fdread);
        FD_SET(fd, &fdread);
        printf("JE VAIS BLOQUER !\n");
        select(fdmax, &fdread, NULL, NULL, NULL);

        printf("FIN DU BLOQUAGE\n");
        //bloquer
    }

    char mess[] = "COUCOU";
    send(sock, mess, strlen(mess), 0);

    close(sock);
    
    printf("FIN THREAD !\n");
    free(a);
    return NULL;
}


int main(int argc, char const *argv[]){
    int fd = eventfd(0, 0);

    int sock_serv;
    if((sock_serv = socket(AF_INET,SOCK_STREAM,0)) <= 0 ){
        perror("serv_sock");
        return EXIT_FAILURE;
    }
    struct sockaddr_in addr;
    addr.sin_family=PF_INET;
    addr.sin_addr.s_addr=htonl(INADDR_ANY);
    addr.sin_port=htons(6667);

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

        argu *a = malloc(sizeof(argu));
        a->fd = fd;
        a->sock = sock_client;

        pthread_t th;
        if(pthread_create(&th,NULL, comm, a) != 0){
            perror("Thread_create");
            return EXIT_FAILURE;
        }
    }
    assert(close(sock_serv)==0);
    return EXIT_SUCCESS;
}