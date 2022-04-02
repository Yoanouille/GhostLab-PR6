CC=gcc
LDFLAGS=-Wall -pthread
TARGETS= lab server

all: ${TARGETS}

lab: src/lab.c include/lab.h
	${CC} ${LDFLAGS} -Iinclude src/lab.c -o lab

server: src/server.c include/server.h
	${CC} ${LDFLAGS} -Iinclude src/server.c -o server

clean: 
	rm -rf *.o ${TARGETS}

.PHONY: all clean
