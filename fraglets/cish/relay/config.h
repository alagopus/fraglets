/*
    $Id: config.h,v 1.2 2000-05-01 16:06:47 marion Exp $
    Internet dispatch server. (replacement)

    Copyright (C) 1995 Klaus Rennecke, all rights reserved.

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
 * $Log: config.h,v $
 * Revision 1.2  2000-05-01 16:06:47  marion
 * Adapted to new RCS log message algorithm.
 *
 * Revision 1.1  2000/04/30 06:27:22  marion
 * Converted to MIT license.
 *
 * Revision 1.1  1995/09/06 00:03:34  marion
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
 * Revision 1.1.1.1  1995/09/03  20:19:22  marion
 * Relay sources as on cs.tu-berlin.de ftp server.
 *
 * Revision 1.1  91/02/01  04:10:00  small21
 * Initial revision
 * 
 */

#ifndef _CONFIG_h
#define _CONFIG_h

#define CONFIG_INETD	"inetd.cnf"
#define CONFIG_RELAY	"relay.cnf"

#define MAXARGCOUNT	12

#endif /* _CONFIG_h */
