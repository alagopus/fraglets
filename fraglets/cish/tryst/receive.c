/*
 * $Id: receive.c,v 1.2 2000-05-01 15:24:56 marion Exp $
 * 
 * tryst/receive.c - 
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
 * $Log: receive.c,v $
 * Revision 1.2  2000-05-01 15:24:56  marion
 * Port to linux using portable control message header.
 *
 * Revision 1.1  2000/05/01 13:19:39  marion
 * Shared memory and unix domain socket group IPC
 *
 * Revision 1.2  1994/07/20 00:38:20  marion
 * First run.
 *
 * Revision 1.1  1994/07/19  13:30:13  marion
 * Initial revision.
 *
 */

#include "internal.h"

#include <unistd.h>
#include <memory.h>
#include <errno.h>
#include <sys/uio.h>

Subject *
TrystReceive (int fd)
{
  Subject		*result;
  struct msghdr		msg;
  struct iovec		iov[1];
  char			buf[32];
#if !defined(__svr4__) || defined(_XPG4_2)
  struct cmsghdr *cmsg;
  char cmsgbuf[CMSG_SPACE(sizeof (result->fd))];
#endif
  
  result = NEW(Subject,1);
  if (!result)
    {
      errno = ENOMEM;
      return 0;
    }
  
  result->address = 0;
  result->fd = -1;
  result->len = 0;
    
  memset (buf, 0, sizeof (buf));
  iov[0].iov_base = (caddr_t)buf;
  iov[0].iov_len = sizeof (buf);

  msg.msg_name = (caddr_t)0;
  msg.msg_namelen = 0;
  msg.msg_iov = iov;
  msg.msg_iovlen = 1;
#if !defined(__svr4__) || defined(_XPG4_2)
  msg.msg_control = cmsgbuf;
  msg.msg_controllen = sizeof (cmsgbuf);
#else
  msg.msg_accrights = (caddr_t)&result->fd;
  msg.msg_accrightslen = sizeof (result->fd);
#endif

  if (recvmsg (fd, &msg, 0) < 0)
    {
      DELETE(result);
      return 0;
    }

#if !defined(__svr4__) || defined(_XPG4_2)
  /* Search for control message with fd. */
  for (cmsg = CMSG_FIRSTHDR(&msg); cmsg != NULL; cmsg = CMSG_NXTHDR(&msg,cmsg))
    {
      if (cmsg->cmsg_level == SOL_SOCKET && cmsg->cmsg_type == SCM_RIGHTS)
	{
	  result->fd = *(int*)CMSG_DATA(cmsg);
	  break;
	}
    }
#endif
  
  if (memcmp (buf, DATA, MSGLEN) || result->fd < 0)
    {
      DELETE(result);
      return 0;
    }
  
  memcpy (&result->len, buf + MSGLEN, sizeof (result->len));

  return result;
}
