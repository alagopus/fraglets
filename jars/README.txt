This directory contains utilities used to build or deploy the shelf to
its web site.  All software here is open source and bears its own
copyright.  Please refer to the individual packages for more
information.

The jars in this directory are installed as extensions to the JRE,
this minimizes the touble of referring to them by file name.  That
makes portability much easier.  If you do not want to or cannot
install the jars as extensions, you can include them in the CLASSPATH.

Some packages use special handling for build or deploy procedures.
These are implemented using the JMK Make in Java, contained in
jmk.jar.  Documentation and source is included in the jar.  See
http://www.ccs.neu.edu/home/ramsdell/make/edu/neu/ccs/jmk/ for more
information.

To deploy jars from the shelf I use the Java SSH client MindTerm.
Since this software includes encryption, I save the Forge trouble by
just referring to it by URL: see http://www.mindbright.se/mindterm/
