#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>

#define SIZE 8

int main(void) {
    int fd = open("test.txt", O_RDONLY);

    if(fd == -1) {
        perror("open");
        return 1;
    }

    char buff[SIZE + 1];
    memset(buff, 0, SIZE + 1);

    char mess[200];
    memset(buff, 0, 200);
    int x = 0;
    int pre = 0;
    int prepre = 0;
    while(1) {
        int re = read(fd, buff, SIZE);
        //Gere Erreur recv
        for(int i = 0; i < re; i++) {
            if(buff[i] == '*') {
                
                if(prepre) {
                    //Au lieu de printf, appeler la fonction en raccord avec la requête
                    //printf("COUCOU%c\n", mess[0]);
                    printf("%s\n", mess);
                    memset(mess, 0, 200);
                    x = 0;
                } 
                else if(pre) prepre = 1;
                else pre = 1;
            } else {
                pre = 0;
                prepre = 0;
                //printf("%c", buff[i]);
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
}