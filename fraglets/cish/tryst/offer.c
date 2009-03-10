/*
 * $Id: offer.c,v 1.2 2000-05-01 15:24:56 marion Exp $
 * 
 * tryst/offer.c - Offer a tryst to other processes.
 * Jul 18 1994 by marion
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
 * $Log: offer.c,v $
 * Revision 1.2  2000-05-01 15:24:56  marion
 * Port to linux using portable control message header.
 *
 * Revision 1.1  2000/05/01 13:19:39  marion
 * Shared memory and unix domain socket group IPC
 *
 * Revision 1.2  1994/07/20 00:38:17  marion
 * First run.
 *
 * Revision 1.1  1994/07/19  13:30:02  marion
 * Initial revision.
 *
 */


#include "internal.h"

#include <unistd.h>
#include <memory.h>
#include <errno.h>
#include <unistd.h>
#include <sys/uio.h>

Tryst *
TrystOffer (const char *name)
{
  Tryst		*result;
  int		len;
  
  len = strlen (name);
  /* remember, result in the expression below is not evaluated. */
  if (len >= sizeof (result->address.sun_path))
    {
      errno = ENAMETOOLONG;
      return 0;
    }
    
  result = NEW(Tryst,1);
  if (!result)
    {
      errno = ENOMEM;
      return 0;
    }
    
  result->address.sun_family = AF_UNIX;
  memset (result->address.sun_path, 0, sizeof (result->address.sun_path));
  memcpy (result->address.sun_path, name, len);
  
  result->fd = socket (PF_UNIX, SOCK_DGRAM, 0);
  if (result->fd < 0)
    {
      DELETE(result);
      return 0;
    }
  
  if (bind (result->fd, (struct sockaddr *)&result->address,
  	    sizeof (result->address)) != 0)
    {
      close (result->fd);
      DELETE(result);
      return 0;
    }
  
  return result;
}

int
TrystPlace (Tryst *offer)
{
  struct iovec		iov[1];
  struct msghdr		msg;
  char			buf[256];
  int			fd;
#if !defined(__svr4__) || defined(_XPG4_2)
  struct cmsghdr *cmsg;
  char cmsgbuf[CMSG_SPACE(sizeof (fd))];
#endif
  
  fd = -1;
  memset (buf, 0, sizeof (buf));
  
  iov[0].iov_base = (caddr_t)buf;
  iov[0].iov_len = sizeof (buf);
  
  msg.msg_name = 0;
  msg.msg_namelen = 0;
  msg.msg_iov = iov;
  msg.msg_iovlen = 1;
#if !defined(__svr4__) || defined(_XPG4_2)
  msg.msg_control = cmsgbuf;
  msg.msg_controllen = sizeof (cmsgbuf);
#else
  msg.msg_accrights = (caddr_t)&fd;
  msg.msg_accrightslen = sizeof (fd);
#endif

  if (recvmsg (offer->fd, &msg, 0) < 0)
    {
      return -1;
    }

#if !defined(__svr4__) || defined(_XPG4_2)
  /* Search for control message with fd. */
  for (cmsg = CMSG_FIRSTHDR(&msg); cmsg != NULL; cmsg = CMSG_NXTHDR(&msg,cmsg))
    {
      if (cmsg->cmsg_level == SOL_SOCKET && cmsg->cmsg_type == SCM_RIGHTS)
	{
	  fd = *(int*)CMSG_DATA(cmsg);
	  break;
	}
    }
#endif
  
  if (msg.msg_iovlen == 1 && iov[0].iov_len > 0
      && memcmp (buf, HELO, MSGLEN) == 0 && fd != -1)
    {
      return fd;
    }
  
  if (fd != -1)
    {
      close (fd);
    }
    
  errno = EIO;
  return -1;
}

void
TrystResign (Tryst *old)
{
  unlink (old->address.sun_path);
  close (old->fd);
  DELETE(old);
}
