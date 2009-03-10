# CFLAGS=-g
CFLAGS=-O -fstrength-reduce
CC=gcc

OBJ=delta.o dgrep.o read.o search.o

dgrep: $(OBJ)
	$(CC) -o $@ $(OBJ)

clean:
	rm -f dgrep delta.o dgrep.o read.o search.o core

delta.o dgrep.o read.o search.o: dgrep.h
