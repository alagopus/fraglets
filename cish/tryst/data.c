/*
 * $Id: data.c,v 1.2 2000-05-01 15:24:56 marion Exp $
 * 
 * tryst/data.c - 
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

/*
 * $Log: data.c,v $
 * Revision 1.2  2000-05-01 15:24:56  marion
 * Port to linux using portable control message header.
 *
 * Revision 1.1  2000/05/01 13:19:39  marion
 * Shared memory and unix domain socket group IPC
 *
 * Revision 1.2  1994/07/20 00:38:09  marion
 * First run.
 *
 * Revision 1.1  1994/07/19  13:29:50  marion
 * Initial revision.
 *
 */

#include "internal.h"

#include <sys/mman.h>

extern int close (int);

void *
TrystData (Subject *subj)
{
  if (!subj->address && subj->fd >= 0)
    {
      subj->address = mmap ((caddr_t)0, subj->len, PROT_READ, MAP_SHARED,
			    subj->fd, (off_t)0);
      if (subj->address == (caddr_t)-1)
	{
	  subj->address = 0;
	  return 0;
	}
    
      close (subj->fd);
      subj->fd = -1;
    }
    
  return subj->address;
}
