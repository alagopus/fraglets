/*
 * $Id: alloc.c,v 1.2 2000-05-01 15:24:56 marion Exp $
 * 
 * tryst/alloc.c - 
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
 * $Log: alloc.c,v $
 * Revision 1.2  2000-05-01 15:24:56  marion
 * Port to linux using portable control message header.
 *
 * Revision 1.1  2000/05/01 13:19:39  marion
 * Shared memory and unix domain socket group IPC
 *
 * Revision 1.2  1994/07/20 00:38:06  marion
 * First run.
 *
 * Revision 1.1  1994/07/19  13:29:48  marion
 * Initial revision.
 *
 */

#include "internal.h"

#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <limits.h>
#include <sys/mman.h>
#include <sys/param.h>

Subject *
TrystAlloc (int size)
{
  static char	path[MAXPATHLEN];
  Subject	*result;
  
  result = NEW(Subject,1);
  if (!result)
    {
      errno = ENOMEM;
      return 0;
    }
  
  sprintf (path, TMPPAT, getpid ());
  result->len = size;
  result->fd = open (path, O_RDWR|O_CREAT, 0666);
  if (result->fd < 0)
    {
      DELETE(result);
      return 0;
    }
  unlink (path);
  ftruncate (result->fd, (off_t)size);

  result->address = mmap ((caddr_t)0, size, PROT_READ|PROT_WRITE, MAP_SHARED,
			  result->fd, (off_t)0);
  if (result->address == (caddr_t)-1)
    {
      close (result->fd);
      DELETE(result);
      return 0;
    }
  
  return result;
}
