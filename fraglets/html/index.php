<HTML>
<HEAD>
<TITLE>Fraglets Project: Welcome</TITLE>
</HEAD>

<BODY bgcolor=#FFFFFF>

<!-- SourceForge logo at right -->
<A href="http://sourceforge.net/"><IMG
  src="http://sourceforge.net/sflogo.php?group_id=5068&type=1"
  width="88" height="31" border="0" align="right"
  alt="Hosted by SourceForge"></A> 
<!-- End SourceForge logo -->

<h1>Welcome to the Fraglets Project</h1><hr noshade>

<h2>Introduction</h2>

<p>The Fraglets project is a place for pieces of software which happen to be
created in the process of writing other software. It is a resting place to
allow algorithms to settle and mature and a forum for ideas and components.
See <a href="http://sourceforge.net/project/?group_id=5068">the project
page</a> for further information, but don't expect much for now.</P>

<p>Since the project consists of fragments, there is no particular
implementation goal and no complete product for this project.
Output from this project is expected to
become re-used in other projects. That is the reason why the license of
choice in the Fraglets project is the <a
href="http://www.opensource.org/licenses/mit-license.html">MIT/X License</a>.
It provides a good compromise for reuse in most projects, most convincingly
demonstrated by the X Windows software.

<P>Due to the fragmentary character of most software contained in this
project there will be few file releases. The easiest way to access the
software will in most cases be the <a
href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/fraglets/fraglets/"
>CVSWeb interface</a>.</P>

<P>Automatically generated daily snapshots are provided as
<A HREF="http://cvs.sourceforge.net/cvstarballs/fraglets-cvsroot.tar.gz">tarballs</A>.</P>

<h2>C/C++ programs</h2>
Some of the C programs are quite old already. Thus, although the algorithm
may still be useful, the software may use features no longer supported
on a modern system.

<h3><a href="http://cvs.sourceforge.net/cgi-bin/cvsweb.cgi/fraglets/cish/dgrep/?cvsroot=fraglets">dgrep</a></h3>
<p>Somewhat redundantly, there is a fast literal string search program <a
href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/fraglets/fraglets/cish/dgrep/"
>dgrep</a> from my C development times. That software is comparatively old,
but since it is small and fast, I still use it as a starting point if fast
string match in large data is needed. It is in dire need of documentation
though.</p>

<h3><a href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/fraglets/fraglets/cish/relay/">relay</a></h3>
<p><a href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/fraglets/fraglets/cish/relay/"
>Relay</a> contains a set of network relay/forwarding programs
which were created to migrate an active multiuser game.</p>

<h3><a href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/fraglets/fraglets/cish/tryst/">tryst</a></h3>
<p>The <a href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/fraglets/fraglets/cish/tryst/"
>tryst</a> package is an example of black *NIX art which will probably
never become very portable. It implements a fast inter-process communication
mechanism. Since it uses unix domain datagram sockets, memory mapping and
file descriptor passing it depends on quite a lot of unix features.</p>

<p>The current version works at least on old SunOSes and Linux. Note that
the expected cost of sending a message in tryst is <em>constant</em> and
does not depend on the message length. This is done by passing file
descriptors to mmap-ed memory blocks around.</p>

<h2>Java Beans</h2>

<p>There is a <a href="beans.php">separate page</a> showcasing beans online.
You will need a java-able browser or, even better now, the
<a href="http://java.sun.com/products/plugin/">java plugin from
Sun Microsystems</a>.</p>

<h2>Java Classes</h2>

<p><a href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/*checkout*/fraglets/fraglets/java/net/sourceforge/fraglets/swing/JMenuAction.java?rev=HEAD&content-type=text/plain"
>JMenuAction.java</a> is mostly obsolete now, only the effort spent on
avoiding memory leaks might make it useful in current Java releases. However,
users who are still stuck with version 1.2 might find the behaviour
provided by menu item of the later releases valuable.</p>

<p><a href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/*checkout*/fraglets/fraglets/java/net/sourceforge/fraglets/swing/SortedTableModel.java?rev=HEAD&content-type=text/plain"
>SortedTableModel.java</a> and <a href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/*checkout*/fraglets/fraglets/java/net/sourceforge/fraglets/swing/SortedTableProxy.java?rev=HEAD&content-type=text/plain"
>SortedTableProxy.java</a> provide a generic way to make table sortable
by column, as seen in many popular user interfaces. Its use can be seen in
action in the <a href="#MTGOTrader">MTGO Trader utility</a>.</p>

<h2>Java Utilities</h2>

<p><a href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/fraglets/fraglets/java/net/sourceforge/fraglets/yaelp/"
>YAELP</a> stands for Yet Another EverQuest Logfile Parser, and it is
just that. Other than the usual log file parser with damage output
calculation and whatnot, this one is only able to create user lists
from observed /who output. However, it is extremely fast at doing that
and it can parse gzipped files directly. I can parse my complete log
files from the past half year in under one minute. You can use the
<a href="http://sourceforge.net/project/showfiles.php?group_id=5068"
>pre-compiled jars</a> from the files section to give it a try.</p>

<p><a name="MTGOTrader" href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/fraglets/fraglets/java/net/sourceforge/fraglets/mtgo/trader/"
>MTGO Trader</a> is an unfinished attempt at creating a tool to organize
trading cards, geared toward the late online version. You can use the
<a href="http://sourceforge.net/project/showfiles.php?group_id=5068"
>pre-compiled jars</a> from the files section to have a look at what
it does so far.</p>

<h2>Shell scripts</h2>
Shell scripts contained here may often expect an installation of
<a href="http://www.gnu.org/">GNU</a> tools.

<h3><a href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/fraglets/fraglets/bash/javadepend">javadepend</a></h3>
<p>As with many temporary solutions, my <a
href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/*checkout*/fraglets/fraglets/bash/javadepend?rev=HEAD&content-type=text/plain"
>javadepend</a> shell script now lives in most of my java projects. Since
conception it serves to generate Java dependencies for a set of java source
files. It is sort of obscure in its implementation, does depend on standard
*NIX tools and a ready installation of the GNU C++ preprocessor, but is
really fast compared to all other solutions I have seen so far.

<hr noshade><p align="right">$Id: index.php,v 1.9 2002-06-09 14:15:39 marion Exp $</p>
</BODY>
</HTML>
