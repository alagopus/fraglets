/*
 * $Id: accept.c,v 1.2 2000-05-01 15:24:56 marion Exp $
 * 
 * tryst/accept.c - 
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
 * $Log: accept.c,v $
 * Revision 1.2  2000-05-01 15:24:56  marion
 * Port to linux using portable control message header.
 *
 * Revision 1.1  2000/05/01 13:19:39  marion
 * Shared memory and unix domain socket group IPC
 *
 * Revision 1.2  1994/07/20 00:38:04  marion
 * First run.
 *
 * Revision 1.1  1994/07/19  13:29:45  marion
 * Initial revision.
 *
 */

#include "internal.h"

#include <memory.h>
#include <errno.h>
#include <unistd.h>
#include <sys/uio.h>

int
TrystAccept (const char *name)
{
  struct sockaddr_un	address;
  struct msghdr		msg;
  struct iovec		iov[1];
  int			pair[2];
  int			fd;
  int			len;
#if !defined(__svr4__) || defined(_XPG4_2)
  struct cmsghdr *cmsg;
  char cmsgbuf[CMSG_SPACE(sizeof (pair[1]))];
#endif
  
  len = strlen (name);
  if (len >= sizeof (address.sun_path))
    {
      errno = ENAMETOOLONG;
      return -1;
    }
    
  /* Store the address of our partner. */
  address.sun_family = AF_UNIX;
  memset (address.sun_path, 0, sizeof (address.sun_path));
  memcpy (address.sun_path, name, len);
  
  fd = socket (PF_UNIX, SOCK_DGRAM, 0);
  if (fd < 0)
    {
      return -1;
    }

  if (socketpair (AF_UNIX, SOCK_DGRAM, 0, pair) < 0)
    {
      close (fd);
      return -1;
    }

  iov[0].iov_base = (caddr_t)HELO;
  iov[0].iov_len = MSGLEN;

  msg.msg_name = (caddr_t)&address;
  msg.msg_namelen = sizeof (address);
  msg.msg_iov = iov;
  msg.msg_iovlen = 1;
#if !defined(__svr4__) || defined(_XPG4_2)
  msg.msg_control = cmsgbuf;
  msg.msg_controllen = sizeof (cmsgbuf);

  cmsg = CMSG_FIRSTHDR(&msg);
  cmsg->cmsg_level = SOL_SOCKET;
  cmsg->cmsg_type = SCM_RIGHTS;
  cmsg->cmsg_len = CMSG_LEN(sizeof (pair[1]));
  memcpy (CMSG_DATA(cmsg), &pair[1], sizeof (pair[1]));

  /* recaclulate the correct length. */
  msg.msg_controllen = cmsg->cmsg_len;
#else
  msg.msg_accrights = (caddr_t)&pair[1];
  msg.msg_accrightslen = sizeof (pair[1]);
#endif

  if (sendmsg (fd, &msg, 0) < 0)
    {
      close (fd);
      close (pair[0]);
      close (pair[1]);
      return -1;
    }

  close (fd);
  close (pair[1]);
  
  return pair[0];
}
