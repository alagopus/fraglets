/*
    $Id: relay.c,v 1.1 2000-04-30 06:27:22 marion Exp $

    relay.c - crude relay process.
    Sep 04 1995 by marion@marble.sax.de
    (C) Copyright 1991 AbStuRz
    (C) Copyright 1991 1995 Klaus Rennecke (marion@marble.sax.de)
		       all rights reserved.

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

/*
 * This program is quite crude and minimalistic. It probably fails on
 * systems not exactly the same as SunOS.
 *
 * Since on most occasions the standard error is redidected to
 * /dev/null, this program does an abort on fatal errors to allow
 * the core to be examined. If you don't like core files, create
 * a directory named core in the working directory for this
 * program.
 */

/* $Log: relay.c,v $
/* Revision 1.1  2000-04-30 06:27:22  marion
/* Converted to MIT license.
/*
 * Revision 1.4  1995/09/06 00:04:18  marion
 * Added a configuration file for inetd.
 * The ancient header file relay.h vanished. All configuration
 *   previously defined therein is now in the inetd configuration
 *   file.
 * New header file config.h for overall configuration of default
 *   parameters.
 * New ftprelay program, which is able to relay ftp sessions if
 *   no use of PORT commands is made.
 * All three programs now accept one optional argument, the name
 *   of the respective configuration file.
 *
 * Revision 1.3  1995/09/04  05:32:29  marion
 * Added optional SYSV fcntl interface.
 * Added some comments.
 *
 * Revision 1.2  1995/09/03  23:48:09  marion
 * Put under GPL. Added README. Added check for getdtablesize
 *
 */

#include <sys/types.h>
#include <sys/socket.h>
#if HAVE_SYS_FILIO_H
#include <sys/filio.h>
#else
#include <fcntl.h>
#endif
#include <sys/signal.h>
#include <netinet/in.h>
#include <netdb.h>

#include "config.h"

/*
 * This defines two things: the maximum size of the relay.cnf
 * file and the maximum chunk size transfered when forwarding.
 */
#define BUFFER 8192

static char buf[BUFFER];

/*ARGSUSED*/
main(c,a) int c; char **a; {
	struct sockaddr_in sin;
	int n, pid, s;
	char *name, *port;

	if (fork()) exit (0);
	if ((s = socket (PF_INET, SOCK_STREAM, 0)) < 0) abort ();
	else {
		extern char *strtok (), *memset (), *memcpy ();
		struct hostent *h;

		if( c == 2)
		  {
		    if ((pid = open (a[1], 0)) < 0) abort ();
		  }
		else
		  {
		    if ((pid = open (CONFIG_RELAY, 0)) < 0) abort ();
		  }

		if ((n = read (pid, buf, BUFFER)) < 0) abort ();
		buf[n] = 0;
		(void)close (pid);

		/*
		 * This parses the regular expression:
		 * {WS}*\({NW}+\){WS}+\({NW}+\){WS}*
		 * with WS = [ \n\r\t] and NW = [^ \n\r\t].
		 */
		if (!(name = strtok (buf, " \n\r\t"))) abort ();
		(void)memset ((char *)&sin, sizeof (sin), 0);
		if (!(h = gethostbyname (name))) abort ();
		(void)memcpy ((char *)&sin.sin_addr.s_addr,
			h->h_addr_list[0], h->h_length);
		sin.sin_family = h->h_addrtype;
		if (!(port = strtok ((char *)0, " \n\r\t"))) abort ();
		sin.sin_port = htons ((unsigned short)atoi (port));
	}

	/*
	 * FIX: Should do a bind before connecting. For now, we
	 * rely on the tcp implementation doing that for us.
	 */
	if (connect (s, (struct sockaddr *)&sin, sizeof sin)) {
	    write (1, "Can't relay request!\n", 21);
	    exit (1);
	}

	/*
	 * What is cheaper to implement bidirectional forwarding?
	 * I think it's forking, rather than multiplexing (no matter
	 * if it's select(2) or poll(2V)). That saves us one
	 * system call per chunk transfered. But, it uses up process
	 * id's.
	 */
	if (pid = fork()) {
		if (pid < 0) abort ();
		(void)dup2 (s, 0);
		(void)close (s);
		write (1, "Connected to ", 13);
		write (1, name, strlen (name));
		write (1, ".\n", 2);
		/* show what we're supposed to do */
		*a = "relay-out";
	} else {
		(void)dup2 (s, 1);
		(void)close (s);
		/* show what we're supposed to do */
		*a = "relay-in";
	}
	signal (SIGPIPE, SIG_DFL);

	/*
	 * We definitely want to block on input, else we
	 * would busy wait, eating all CPU time available.
	 */
#if HAVE_SYS_FILIO_H
	n = 0;
	if (ioctl (0, FIONBIO, (caddr_t)&n)) abort ();
#else
	n = fcntl( 0, F_GETFL, 0 );
#if defined(FNDELAY)
	n &= ~FNDELAY;
#else
#if defined(O_NDELAY)
	n &= ~O_NDELAY;
#else
#error needed file flags definition missing
#endif
#endif /* FNDELAY */
	if( fcntl( 0, F_SETFL, n ) == -1 )
	  {
	    abort();
	  }
#endif
	(void)write (2, *a, strlen (*a));
	(void)write (2, "\n", 1);

	/* this is the main issue - the forwarding loop */
	while ((n = read (0, buf, BUFFER)) > 0) {
		(void)write (1, buf, n);
	}

	/*
	 * Failed reading? here we don't dump core, since
	 * that may happen on close from peer, depending
	 * on implementation of connection release in the
	 * OS.
	 */
	if (n < 0) perror ("read");
	if (pid) (void)kill (pid, 15);

	write (2, "bye!\n", 5);
	return 0;
}
