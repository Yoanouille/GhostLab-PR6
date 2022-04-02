#include "player.h"


player *genPlayer(){
    player  *p = malloc(sizeof(player));
    if(p == NULL){
        perror("malloc genPlayer");
        exit(1);
    }
    return p;
}