/*
    $Id: ftprelay.c,v 1.2 2000-05-01 16:06:47 marion Exp $

    ftprelay.c - crude ftp relay process.
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
 *
 * The ftp relay version uses a listen(,,1) to forward data connections.
 * You probably have to use the ftp 'sendport' client command to disable
 * the use of the ftp PORT protocol.
 */

/*
 * $Log: ftprelay.c,v $
 * Revision 1.2  2000-05-01 16:06:47  marion
 * Adapted to new RCS log message algorithm.
 *
 * Revision 1.1  2000/04/30 06:27:22  marion
 * Converted to MIT license.
 *
 * Revision 1.1  1995/09/06 00:03:53  marion
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

extern int errno;

static char buf[BUFFER];
static int lasterr;

/*ARGSUSED*/
main(c,a) int c; char **a; {
	struct sockaddr_in sin;
	struct sockaddr_in back;
	struct sockaddr_in from;
	int n, pid, s, lpid;
	char *name, *port;

	if (fork()) exit (0);
	if ((s = socket (AF_INET, SOCK_STREAM, 0)) < 0) abort ();
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
	    write (1, "500 Can't relay request!\n", 21);
	    exit (1);
	}

	/*
	 * Fork a third relay to accept data connections.
	 */
	if( !(lpid = fork()) )
	  {
	    lpid = sizeof( back );
	    if( getpeername( 0, (struct sockaddr *)&back, &lpid) < 0 )
	      {
		abort();
	      }
	    close( 0 );
	    close( 1 );

	    lpid = sizeof( sin );
	    if( getsockname( s, (struct sockaddr *)&sin, &lpid) < 0 )
	      {
		abort();
	      }
	    close( s );
listenagain:
	    *a = "relay-accept";
	    n = 1;
	    if( (s = socket( AF_INET, SOCK_STREAM, 0 )) < 0
		|| setsockopt( s, SOL_SOCKET, SO_REUSEADDR,
			       (char*)&n, sizeof n ) < 0
		|| bind( s, (struct sockaddr *)&sin, sizeof sin ) < 0
		|| listen( s, 1 ) < 0 )
	      {
		abort();
	      }
	    lpid = sizeof( from );
	    if( (n = accept( s, &from, &lpid )) < 0 )
	      {
		abort();
	      }
	    close( s );
	    if( lpid = fork() )
	      {
		close( n );
		while ((n = wait(&s)) != lpid && n != -1) ;
		goto listenagain;
	      }
	    else
	      {
		if( n != 0 )
		  {
		    dup2( n, 0 );
		    if( n != 1 )
		      {
			dup2( n, 1 );
			close( n );
		      }
		  }
		else
		  {
		    dup2( n, 1 );
		  }
		if( (s = socket (AF_INET, SOCK_STREAM, 0)) < 0 )
		  {
		    lasterr = errno;
		    abort();
		  }
		if( connect( s, (struct sockaddr *)&back, sizeof back ) )
		  {
		    lasterr = errno;
		    abort();
		  }
		if( fork() ) exit ( 0 ); /* free parent */
		n = -1; /* we're transport */
		/* now fall through to forwarding relay */
	      }
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
		/* show what we're supposed to do */
		*a = "relay-out";
	} else {
		(void)dup2 (s, 1);
		(void)close (s);
		/* show what we're supposed to do */
		*a = "relay-in";
		if( n == -1 )
		  {
		    pid = getppid (); /* transport */
		  }
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
	if (lpid) (void)kill (lpid, 15);
	if (pid) (void)kill (pid, 15);

	write (2, "bye!\n", 5);
	return 0;
}
