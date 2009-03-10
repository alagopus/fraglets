#
# $Id: Makefile,v 1.2 2000-05-01 15:24:56 marion Exp $
# 
# tryst/Makefile - 
# Jul 18 1994 by marion
# (C) Copyright 1994 Klaus Rennecke all rights reserved.
#
# Permission is hereby granted, free of charge, to any person
# obtaining a copy of this software and associated documentation
# files (the "Software"), to deal in the Software without
# restriction, including without limitation the rights to use, copy,
# modify, merge, publish, distribute, sublicense, and/or sell copies
# of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be
# included in all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
# EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
# MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
# NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
# HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
# WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
# DEALINGS IN THE SOFTWARE.
#

# $Log: Makefile,v $
# Revision 1.2  2000-05-01 15:24:56  marion
# Port to linux using portable control message header.
#
# Revision 1.1  2000/05/01 13:19:39  marion
# Shared memory and unix domain socket group IPC
#
# Revision 1.2  1994/07/20 00:38:00  marion
# First run.
#
# Revision 1.1  1994/07/19  13:29:41  marion
# Initial revision.
#
#

CC	= gcc
CFLAGS	= -Wall -O2 -g
CPPFLAGS= 

# Don't use sysV ar, it truncates file names.
AR	= ar
RANLIB	= : ranlib

PARTS	= accept alloc data fileno free offer receive send size
SOURCE	= $(PARTS:%=%.c)

all: libtryst.a

libtryst.a: $(PARTS:%=%.o)
	$(AR) cr $@ $(PARTS:%=%.o)
	$(RANLIB) libtryst.a
	

test: test-tryst

test-tryst: test-tryst.o libtryst.a
	$(CC) $(CFLAGS) -o $@ test-tryst.o libtryst.a

test-tryst.o: tryst.h

depend: $(SOURCE) Makefile
	$(CC) $(CFLAGS) $(CPPFLAGS) -M $(SOURCE) | \
	  sed -e 's/^\([^.]*\)\.o/depend \1.o/' > $@

clean:
	rm -f $(PARTS:%=%.o) test-tryst.o *~

clobber: clean
	rm -f libtryst.a depend

.PHONY: all test clean clobber

include depend
