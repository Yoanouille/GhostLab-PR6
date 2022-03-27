CC=gcc
LDFLAGS=-Wall
TARGETS=lab

all: ${TARGETS}

lab: src/lab.c include/lab.h
	${CC} ${LDFLAGS} -Iinclude src/lab.c -o lab

clean: 
	rm -rf *.o ${TARGETS}