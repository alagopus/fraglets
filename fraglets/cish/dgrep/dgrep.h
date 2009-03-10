/*
    dgrep.h - Copyright 1992 Klaus Rennecke
    grep using Boyer/Moore.
 
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

typedef unsigned char alph;	/* The alphabet type */

typedef struct word {
    int		length;		/* Length of the word */
    alph	w[1];		/* Data of the word */
} word;

#define LG(w)	(w->length)	/* Compute word length */

#define MINSZ		(8192)
#define SBUF(w)		(LG(w)<(MINSZ>>4)?MINSZ:LG(w)<<4)
#define LARGE(w)	(SBUF(w)+LG(w)*2)
#define ORD(t)		(1<<(sizeof (t)*8))

typedef struct dbuf {
    int		length;		/* Length allocated */
    int		n;		/* Alph read into buffer */
    alph	*buffer;
} dbuf;
