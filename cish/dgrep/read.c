/*
    read.c - Copyright 1992 Klaus Rennecke
    Input routine for dgrep, grep using Boyer/Moore.
 
    Permission is hereby granted, free of charge, to any person
    obtaining a copy of this software and associated documentation
    files (the "Software"), to deal in the Software without
    restriction, including without limitation the rights to use, copy,
    modify, merge, publish, distribute, sublicense, and/or sell copies
    of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
 
    The above copyright notice and this permission notice shall be
    included in all copies or substantial portions of the Software.
 
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
    EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
    NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
    HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
    WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
    DEALINGS IN THE SOFTWARE.
*/

#include <stdio.h>
#include "dgrep.h"

int readbuf (fd, blist, size)
int	fd;
dbuf	*blist;	 		/* List of 3 dbufs */
int	size;			/* Size of the word to search for */
{
    dbuf swap;
    int i;
    alph *p1, *p2;

    /* rotate buffers */
    swap = blist[2];
    blist[2] = blist[1];
    blist[1] = blist[0];
    blist[0] = swap;

    /* if words longer than 1, copy size-1 bytes from previous buffer */
    if (--size) {
	if (size > blist[1].n) size = blist[1].n;
	p1 = blist[0].buffer;
	p2 = blist[1].buffer + blist[1].n - size;
	for (i = 0; i < size; i++)
	    p1[i] = p2[i];
    }
    
    /* read next chunk */
    blist[0].n = read (fd, blist[0].buffer + size, blist[0].length - size);

    /* adjust for size */
    if (blist[0].n >= 0)
	blist[0].n += size;

    return blist[0].n;
}

int writebuf (fd, blist, pos, size)
int	fd;			/* Fd to _read_ from */
dbuf	*blist;			/* List of 3 dbufs */
int	pos, size;
{
    int i, j = pos;
    alph *p;

    /* only size-1 bytes are retained from previous buffer */
    size--;

    /* search previous '\n' */
    for (i = 0; i < 3; i++, j = blist[i].n - size - 1) {
	for (p = blist[i].buffer; j >= 0 && p[j] != '\n'; j--);
	if (j >= 0) break;
    }

    /* advance this '\n'. if not found or at and of buffer, advance buffer */
    if (j < 0 || ++j >= blist[i].n - size) {
	i--; /* buffer index is reverse */
	j = 0;
    }

    /* output this line and return index if next '\n' found */
    for (; i >= 0; i--, j = 0) {
	for (p = blist[i].buffer; j < blist[i].n - size; j++) {
	    putchar (p[j]);
	    if (p[j] == '\n') return j;
	}
    }

    /* now, read more input until a '\n' occurs */
    while (blist[0].n > size + 1) {
	if (readbuf (fd, blist, size + 1) < 0)
	    return -1;
	for (p = blist[0].buffer, j = 0; j < blist[0].n - size; j++) {
	    putchar (p[j]);
	    if (p[j] == '\n') return j;
	}
    }

    /* sigh - end of file read - check last size alphs */
    for (p = blist[0].buffer; j < blist[0].n; j++) {
	putchar (p[j]);
	if (p[j] == '\n') return j;
    }

    /* oops - no '\n' at end of file - be generous */
    putchar ('\n');
    return j;
}
