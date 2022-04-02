CC=gcc
LDFLAGS=-Wall -pthread
TARGETS= lab server player

all: ${TARGETS}

lab: src/lab.c include/lab.h
	${CC} ${LDFLAGS} -Iinclude src/lab.c -o lab

server: src/server.c include/server.h
	${CC} ${LDFLAGS} -Iinclude src/server.c -o server

player : src/player.c include/player.h
	${CC} ${LDFLAGS} -Iinclude src/player.c -o player

clean: 
	rm -rf *.o ${TARGETS}

.PHONY: all clean
