/*
 * $Id: test-tryst.c,v 1.2 2000-05-01 15:24:56 marion Exp $
 * 
 * tryst/test-tryst.c - 
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
 * $Log: test-tryst.c,v $
 * Revision 1.2  2000-05-01 15:24:56  marion
 * Port to linux using portable control message header.
 *
 * Revision 1.1  2000/05/01 13:19:39  marion
 * Shared memory and unix domain socket group IPC
 *
 * Revision 1.1  1994/07/20 00:38:30  marion
 * First run.
 *
 */

#include <stdio.h>
#include <unistd.h>
#include <assert.h>
#include "tryst.h"

#if 0
extern int printf (const char *, ...);
extern int sleep (unsigned int);
extern char *sprintf (char *, const char *, ...);
#endif

#define NAME "/tmp/tt"

void server();
void client (const char *message);

int
main (int argc, char **argv)
{
  switch (argc)
    {
    case 1:
      server ();
      break;
    case 2:
      client (argv[1]);
      break;
    default:
      fprintf (stderr, "usage: %s [message]\n", argv[0]);
      exit (1);
    }
  
  return 0;
}

void
server ()
{
  Tryst *t;
  Subject *s;
  void *d;
  int p;
  
  t = TrystOffer (NAME);
  assert (t);
  
  printf ("Server: waiting for client...\n");
  p = TrystPlace (t);
  assert (p >= 0);
  
  s = TrystReceive (p);
  assert (s);
  
  d = TrystData (s);
  assert (d);
  
  printf ("Server: received \"%s\"\n", (char *)d);
  
  TrystResign (t);
  
  TrystFree (s);
}

void
client (const char *message)
{
  Subject *s;
  void *d;
  int t;
  
  sleep (1);
  t = TrystAccept (NAME);
  assert (t >= 0);
  
  s = TrystAlloc (strlen (message) + 1);
  assert (s);
  
  d = TrystData (s);
  assert (d);
  
  strcpy ((char *)d, message);
  
  assert (TrystSend (t, s) != -1);
  
  TrystFree (s);
}
