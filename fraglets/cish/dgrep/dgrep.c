/*
    dgrep.c - Copyright 1992 Klaus Rennecke
    Main() for dgrep, grep using Boyer/Moore.
 
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

static char usage[] = "dgrep: usage: dgrep <string> [file ...]\n";

main (argc, argv)
int	argc;
char	**argv;
{
    extern char *malloc(), *strcpy();
    dbuf blist[3];
    word *w;
    int file, fd, size, pos, found;
    int *delta2, *delta3;

    if (argc < 2) {
	fputs (usage, stderr);
	exit (1);
    }

    if (!(w = (word *)malloc (sizeof (*w) + strlen (argv[1]))) ||
	!(w->length = strlen (strcpy (w->w, argv[1]))) ||
	!(blist[0].length = blist[1].length = blist[2].length = SBUF(w)) ||
	!(blist[0].buffer = (alph *)malloc (blist[0].length)) ||
	!(blist[1].buffer = (alph *)malloc (blist[1].length)) ||
	!(blist[2].buffer = (alph *)malloc (blist[2].length))) {
	perror ("malloc");
	exit (1);
    }

    file = 2;
    size = LG(w);
    found = 0;
    if (!delta (w, &delta2, &delta3)) {
	perror ("malloc");
	exit (1);
    }

    do {
	if (file >= argc) fd = 0;
	else {
	    fd = open (argv[file++], 0);
	    if (fd < 0) {
		perror (argv[file-1]);
		continue;
	    }
	}
	blist[0].n = blist[1].n = blist[2].n = 0;
	do {
	    if (readbuf (fd, blist, size) < 0) {
		perror ("read");
		break;
	    }
	    pos = 0;
	    while (pos >= 0) {
		pos = search (w, blist[0].buffer, pos, blist[0].n,
		    delta2, delta3);
		if (pos >= 0) {
		    found = 1;
		    if (argc > 3) printf ("%s:", argv[file-1]);
		    pos = writebuf (fd, blist, pos, size);
		    if (pos >= blist[0].n) pos = -1;
		}
	    }
	} while (blist[0].n > size);
	if (fd) close (fd);
    } while (file < argc);

    return !found;
}
