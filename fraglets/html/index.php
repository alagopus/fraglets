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
href="http://cvs.sourceforge.net/cgi-bin/cvsweb.cgi/fraglets/?cvsroot=fraglets"
>CVSWeb interface</a>.</P>

<P>Automatically generated daily snapshots are provided as
<A HREF="http://cvs.sourceforge.net/cvstarballs/fraglets-cvsroot.tar.gz">tarballs</A>.</P>

<h2>C/C++ programs</h2>

<p>Somewhat redundantly, there is a fast literal string search program <a
href="http://cvs.sourceforge.net/cgi-bin/cvsweb.cgi/fraglets/cish/dgrep/?cvsroot=fraglets"
>dgrep</a> from my C development times. That software is comparatively old,
but since it is small and fast, I still use it as a starting point if fast
string match in large data is needed. It is in dire need of documentation
though.</p>

<h2>Java Beans</h2>

<p>There is a <a href="beans.php">separate page</a> showcasing beans online.
You will need a java-able browser or, even better now, the
<a href="http://java.sun.com/products/plugin/">java plugin from
Sun Microsystems</a>.</p>

<h2>Shell scripts</h2>

<p>As with many temporary solutions, my <a
href="http://cvs.sourceforge.net/cgi-bin/cvsweb.cgi/fraglets/bash/javadepend?cvsroot=fraglets"
>javadepend</a> shell script now lives in most of my java projects. Since
conception it serves to generate Java dependencies for a set of java source
files. It is sort of obscure in its implementation, does depend on standard
*NIX tools and a ready installation of the GNU C++ preprocessor, but is
really fast compared to all other solutions I have seen so far.

<hr noshade><p align="right">$Id: index.php,v 1.4 2000-04-29 14:53:02 marion Exp $</p>
</BODY>
</HTML>
