/*
    $Id: inetd.c,v 1.3 2000-05-01 16:06:47 marion Exp $
    Internet dispatch server. (replacement)

    Sep 03 1995 by marion@marble.sax.de
    (C) Copyright 1991 AbStuRz
    (C) Copyright 1995 Klaus Rennecke (marion@marble.sax.de)
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
 * $Log: inetd.c,v $
 * Revision 1.3  2000-05-01 16:06:47  marion
 * Adapted to new RCS log message algorithm.
 *
 * Revision 1.2  2000/04/30 09:14:17  marion
 * Silly indirection error.
 *
 * Revision 1.1  2000/04/30 06:27:22  marion
 * Converted to MIT license.
 *
 * Revision 1.3  1995/09/06 00:04:07  marion
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
 * Revision 1.2  1995/09/03  23:48:01  marion
 * Put under GPL. Added README. Added check for getdtablesize
 *
 * Revision 1.1.1.1  1995/09/03  20:19:22  marion
 * Relay sources as on cs.tu-berlin.de ftp server.
 *
 * Revision 1.2  91/02/01  04:57:57  small21
 * some socket handling bugs removed
 * 
 * Revision 1.1  91/02/01  04:10:03  small21
 * Initial revision
 * 
 */

#include <sys/types.h>
#if HAVE_SOCKET
#include <sys/socket.h>
#endif
#include <netinet/in.h>
#include <stdio.h>
#include <ctype.h>

#include "config.h"

#ifndef lint
char copyright[] =
"@(#) Copyright (c) 1991 AbStuRz, All rights reserved.\n\
@(#) Copyright (C) 1991 1995 Klaus Rennecke.\n";
#endif /* not lint */

extern char *strtok();

#ifndef lint
static char rcsid[] = "$Id: inetd.c,v 1.3 2000-05-01 16:06:47 marion Exp $";
#endif /* not lint */

suicide(prog, s) char *prog, *s; {
	fprintf(stderr, "%s: ", prog);
	perror(s);
	exit(1);
}

#if !HAVE_MEMCPY
#define memcpy(s1,s2,len) bcopy(s2,s1,len)
#endif

#if !HAVE_STRDUP
char *
strdup( str )
{
  char *result;
  int len;

  len = strlen( str );
  result = malloc( len + 1 );
  memcpy( result, str, len + 1 );

  return result;
}
#else
extern char *strdup();
#endif

struct server_config {
  int	line;
  int	port;
  char	*argv[MAXARGCOUNT+1];
} server;

int
become_server( int config )
{
	int fd = 0;

	if( config < 0 )
	  {
	    perror( "config" );
	    return 0;
	  }
	else
	  {
	    char buffer[8192];
	    char *s;

	    server.port = 0;
	    fd = read( config, buffer, sizeof( buffer ) - 1);
	    if( fd <= 0 )
	      {
		if( fd < 0 )
		  {
		    perror( "config" );
		  }
		return 0;
	      }
	    buffer[fd] = 0;
	    if( !(s = strtok( buffer, "\r\n" ))
		|| !s[0])
	      {
		return 0; /* end of config file */
	      }
	    server.line += 1;
	    lseek( config, -(long)(fd - (s - buffer) - strlen( s )), 1 );
	    if( !(s = strtok( s, " \t" )) )
	      {
		/* empty line containing white space */
		return 1;
	      }
	    if( s[0] == '#' )
	      {
		return 1; /* comment */
	      }
	    server.port = atoi( s );
	    if( server.port <= 0 )
	      {
		fprintf( stderr, "error: config line %d: syntax error\n",
			 server.line );
		return 1;
	      }
	    if( server.port < 5120 )
	      {
		if( server.port < 1024 )
		  {
		    fprintf( stderr,
"warning: config line %d: port numbers below 1024 are privileged.\n",
			     server.line );
		  }
		else
		  {
		    fprintf( stderr,
"warning: config line %d: port numbers below 5120 are dynamically assigned.\n",
			     server.line );
		  }
	      }
	    for( fd = 0; fd < MAXARGCOUNT; fd++ )
	      {
		if( server.argv[fd] )
		  {
		    free( server.argv[fd] );
		  }
		if( (s = strtok( (char*)0, " \t" )) && s[0] != '#')
		  {
		    server.argv[fd] = strdup( s );
		  }
		else
		  {
		    server.argv[fd++] = 0;
		    while( fd < MAXARGCOUNT )
		      {
			if( server.argv[fd] )
			  {
			    free( server.argv[fd] );
			    server.argv[fd] = 0;
			    fd += 1;
			  }
			else
			  {
			    fd = MAXARGCOUNT;
			  }
		      }
		  }
	      }
	    server.argv[MAXARGCOUNT] = 0;
	    if( !server.argv[0] )
	      {
		server.port = 0;
		fprintf( stderr, "error: config line %d: syntax error\n",
			 server.line );
		return 1;
	      }
	  }
	if (fork()) return 1;
	close( config );
#if HAVE_GETDTABLESIZE
	for (fd = getdtablesize()-1; fd >= 0; fd--) {
		if (fd != 2) close (fd);
		else if (isatty (fd)) {
			int null;

			close (fd);
			if (fd != (null = open ("/dev/null", 1))) {
				dup2 (null, fd);
				close (null);
			}
		}
	}
#else
	for (fd = 0;; fd++)
	  {
	    if (fd != 2)
	      {
		/* kludgy: if getdtablesize is not available,
		   rely on the close exit status instead */
		if ( close (fd) && fd > 16 )
		  {
		    break;
		  }
	      }
	    else if (isatty (fd))
	      {
		int null;

		close (fd);
		if (fd != (null = open ("/dev/null", 1)))
		  {
		    dup2 (null, fd);
		    close (null);
		  }
	      }
	  }
#endif /* HAVE_GETDTABLESIZE */
	setpgid (0, 0);
#if HAVE_SETSID
	setsid();
#endif
  
  return 0;
}

main(argc, argv) int argc; char **argv; {
	struct sockaddr_in sin;
	int i, incoming, partner, pid, status;

	if( argc == 2 )
	  {
	    i = open( argv[1], 0, 0 );
	  }
	else
	  {
	    i = open( CONFIG_INETD, 0, 0 );
	  }

	while( become_server(i) );

	if(!server.port)
	  {
	    return 0;
	  }

#if HAVE_SOCKET
	if ((incoming = socket (PF_INET, SOCK_STREAM, 0)) < 0)
	    suicide (*argv, "open listen socket");
#else
#error find a socket equivalent on your system
#endif

#if defined(SO_REUSEADDR)
	i = !0;
	if (setsockopt (incoming, SOL_SOCKET, SO_REUSEADDR, &i, sizeof (i)))
	    suicide (*argv, "setsockopt listen socket");
#endif

	memset (&sin, sizeof (sin), 0);
	sin.sin_family = AF_INET;
	sin.sin_port = htons (server.port);
	/* FIX: Using *.port address for listen is unsafe on BSD4.3 derived
	   systems */
	sin.sin_addr.s_addr = INADDR_ANY;

	i = sizeof (sin);
	if (bind (incoming, &sin, i))
	    suicide (*argv, "bind listen socket");

	if (listen (incoming, 5))
	    suicide (*argv, "listen listen socket");

	while ((i = sizeof (sin), partner = accept (incoming, &sin, &i)) >= 0) {
	    switch (pid = fork ()) {
	    case  0: /* child */
		dup2 (partner, 0);
		execvp(server.argv[0], server.argv);
		suicide (server.argv[0], "execvp");
		_exit(1);
		break;
	    case -1: /* bad father */
		suicide (*argv, "fork");
		break;
	    default: /* lucky parent, wait for child to die */
		close (partner);
		while ((i = wait(&status)) != pid && i != -1) ;
		break;
	    }
	}

	close (incoming);
	suicide (*argv, "accept listen socket");
}
