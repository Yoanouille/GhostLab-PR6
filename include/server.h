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

#include "game.h"


int traitement (int sock,char* mess,int* running);
void  *communication(void *arg);
int main(int argc, char const *argv[]);

