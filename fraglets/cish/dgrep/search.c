/*
    search.c - Copyright 1992 Klaus Rennecke
    Search routine for dgrep, grep using Boyer/Moore.
 
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

#include "dgrep.h"

int search (w, buf, start, size, delta2, delta3)
word	*w;
alph	*buf;
int	start, size;
int	*delta2, *delta3;
{
    int length = LG(w), j;
    int large = delta3[w->w[length-1]];
    register int i = start + length - 1, *d = delta3;
    alph *p1, *p2;

    for (;;) {
	while (i < size)
	    i += d[buf[i]];

	if (i < large)
	    return -1; /* not found */
	
	i -= large;

	p1 = buf + i - 1;
	p2 = w->w + (j = length-2);
	while (j >= 0 && *p1-- == *p2--) j--;

	i -= length - 1;
	if (j < 0)
	    return i; /* found */

	i += j + delta2[j];
    }
}
