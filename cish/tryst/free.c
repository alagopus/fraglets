/*
 * $Id: free.c,v 1.1 2000-05-01 13:19:39 marion Exp $
 * 
 * tryst/free.c - 
 * Jul 19 1994 by marion
 * (C) Copyright 1994 Klaus Rennecke all rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

/* $Log: free.c,v $
/* Revision 1.1  2000-05-01 13:19:39  marion
/* Shared memory and unix domain socket group IPC
/*
 * Revision 1.2  1994/07/20 00:38:12  marion
 * First run.
 *
 * Revision 1.1  1994/07/19  13:29:56  marion
 * Initial revision.
 *
 */

#include "internal.h"

#include <errno.h>
#include <sys/mman.h>

extern int close (int);
extern void free (char *);
extern int munmap (caddr_t, int);

void
TrystFree (Subject *old)
{
  if (old->address)
    {
      munmap (old->address, old->len);
    }
  
  if (old->fd >= 0)
    {
      close (old->fd);
    }

  DELETE(old);
}
