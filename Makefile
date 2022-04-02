CC=gcc
LDFLAGS=-Wall -pthread -Iinclude
TARGETS= server Main.class

all: ${TARGETS}

server: server.o player.o lab.o
	${CC} -o server server.o player.o lab.o

server.o: src/server.c include/server.h include/player.h include/lab.h
	${CC} ${LDFLAGS} -c src/server.c

player.o: src/player.c include/player.h
	${CC} ${LDFLAGS} -c src/player.c

lab.o: src/lab.c include/lab.h
	${CC} ${LDFLAGS} -c src/lab.c

Main.class: Main.java
	javac Main.java

clean: 
	rm -rf *.o ${TARGETS} srcjava/*.class

.PHONY: all clean
