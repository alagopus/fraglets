/*
    delta.c - Copyright 1992 Klaus Rennecke
    Compute the delta tables for dgrep, grep using Boyer/Moore.
 
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

int rpr (j, w, length, delta)
int	j;
alph	*w;
int	length;
int	*delta;
{
    int i, d;

    /* search substring j+1..length, return immediately if empty substring */
    if (++j >= length) return j-1;

    /* first possible occurence */
    d = j - delta[j];

    /* match the whole rest of w[j..length-1] */
    for (i = 0; j + i < length; i++) {
	/* if not before w[0], compare */
	if (d + i >= 0 && w[d+i] != w[j+i]) {
	    if (d < 0) {
		/* looking for substring in w[j..length-1] at word[0] */
		i = -(d--);
	    } else {
		/* looking for next possible occurence */
		i = 0;
		d -= delta[d];
	    }
	}
    }
    return d;
}

int delta (w, delta2, delta3)
word	*w;
int	**delta2;
int	**delta3;
{
    extern char *malloc();
    alph c;
    int i, length, *p, *delta4, *delta5;

    length = LG(w);

    /* allocate delta tables */
    if (!(*delta2 = (int *)malloc (length * sizeof (**delta2))))
	return 0;
    if (!(*delta3 = (int *)malloc (ORD(alph) * sizeof (**delta3))))
	return 0;

    if (!(delta4 = (int *)malloc (ORD(alph) * sizeof (*delta4))))
	return 0;
    if (!(delta5 = (int *)malloc (length * sizeof (*delta5))))
	return 0;

    /* compute delta3 */
    p = *delta3;
    for (i = 0; i < ORD(alph); i++)
	p[i] = length;
    for (i = 0; i < length; i++)
	p[w->w[i]] = length - i - 1;

    p[w->w[length-1]] += LARGE(w);

    /* compute delta5 */
    for (i = 0; i < length; i++)
	delta4[w->w[i]] = 0;
    for (i = 0; i < length; i++) {
	c = w->w[i];
	delta5[i] = i + 1 - delta4[c];
	delta4[c] = i + 1;
    }

    /* compute delta2 */
    p = *delta2;
    for (i = 0; i < length; i++)
	p[i] = length - rpr (i, w->w, length, delta5);

    free ((char *)delta4);
    free ((char *)delta5);
    return 1;
}
