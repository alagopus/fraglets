/*
 * $Id: send.c,v 1.1 2000-05-01 13:19:39 marion Exp $
 * 
 * tryst/send.c - 
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

/* $Log: send.c,v $
/* Revision 1.1  2000-05-01 13:19:39  marion
/* Shared memory and unix domain socket group IPC
/*
 * Revision 1.2  1994/07/20 00:38:24  marion
 * First run.
 *
 * Revision 1.1  1994/07/19  13:30:24  marion
 * Initial revision.
 *
 */

#include "internal.h"

#include <memory.h>
#include <sys/uio.h>

extern int sendmsg (int, struct msghdr *, int);

int
TrystSend (int fd, Subject *data)
{
  struct msghdr		msg;
  struct iovec		iov[1];
  char			buf[MSGLEN + sizeof (data->len)];
  
  memcpy (buf, DATA, MSGLEN);
  memcpy (buf + MSGLEN, &data->len, sizeof (data->len));
  
  iov[0].iov_base = (caddr_t)buf;
  iov[0].iov_len = sizeof (buf);

  msg.msg_name = (caddr_t)0;
  msg.msg_namelen = 0;
  msg.msg_iov = iov;
  msg.msg_iovlen = 1;
  msg.msg_accrights = (caddr_t)&data->fd;
  msg.msg_accrightslen = sizeof (data->fd);

  if (sendmsg (fd, &msg, 0) < 0)
    {
      return -1;
    }

  return data->len;
}
