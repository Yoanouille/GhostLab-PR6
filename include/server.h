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
#include <sys/select.h>

#include "game.h"
#include "player.h"
#include "comm.h"

#define SIZE 8
#define HEIGHT 20
#define WIDTH 20

