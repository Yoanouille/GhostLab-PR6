CC=gcc
LDFLAGS=-Wall -pthread -Iinclude
TARGETS= server Main.class

all: ${TARGETS}

server: server.o player.o lab.o game.o comm.o ghost.o
	${CC} -o server -pthread  server.o player.o lab.o game.o comm.o ghost.o

server.o: src/server.c include/server.h include/player.h include/lab.h include/game.h
	${CC} ${LDFLAGS} -c src/server.c

player.o: src/player.c include/player.h
	${CC} ${LDFLAGS} -c src/player.c

lab.o: src/lab.c include/lab.h
	${CC} ${LDFLAGS} -c src/lab.c

game.o: src/game.c include/game.h include/lab.h include/player.h
	${CC} ${LDFLAGS} -c src/game.c

comm.o: src/comm.c include/comm.h include/player.h include/server.h include/game.h
	${CC} ${LDFLAGS} -c src/comm.c

ghost.o: src/ghost.c include/lab.h include/ghost.h
	${CC} ${LDFLAGS} -c src/ghost.c

Main.class: Main.java srcjava/ClientTCP.java srcjava/Fenetre.java srcjava/Accueil.java srcjava/ClientMulti.java srcjava/ClientUDP.java
	javac Main.java

clean: 
	rm -rf *.o ${TARGETS} srcjava/*.class

.PHONY: all clean
