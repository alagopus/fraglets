<?php
// Default Web Page for groups that haven't setup their page yet
// Please replace this file with your own website
//
// $Id: index.php,v 1.2 2000-04-28 09:03:54 marion Exp $
//
$headers = getallheaders();
?>
<HTML>
<HEAD>
<TITLE>SourceForge: Welcome</TITLE>
<LINK rel="stylesheet" href="http://sourceforge.net/sourceforge.css" type="text/css">
</HEAD>

<BODY bgcolor=#FFFFFF topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" marginheight="0" marginwidth="0">

<!-- top strip -->
<TABLE width="100%" border=0 cellspacing=0 cellpadding=2 bgcolor="737b9c">
  <TR>
    <TD><SPAN class=maintitlebar>&nbsp;&nbsp;
      <A class=maintitlebar href="http://sourceforge.net/"><B>Home</B></A> | 
      <A class=maintitlebar href="http://sourceforge.net/about.php"><B>About</B></A> | 
      <A class=maintitlebar href="http://sourceforge.net/partners.php"><B>Partners</B></a> |
      <A class=maintitlebar href="http://sourceforge.net/contact.php"><B>Contact Us</B></A></SPAN></TD>
    </TD>
  </TR>
</TABLE>
<!-- end top strip -->

<!-- top title table -->
<TABLE width="100%" border=0 cellspacing=0 cellpadding=0 bgcolor="" valign="center">
  <TR valign="top" bgcolor="#eeeef8">
    <TD>
      <A href="http://sourceforge.net/"><IMG src="http://sourceforge.net/images/sflogo2-steel.png" vspace="0" border=0 width="143" height="70"></A>
    </TD>
    <TD width="99%"><!-- right of logo -->
      <a href="http://www.valinux.com"><IMG src="http://sourceforge.net/images/va-btn-small-light.png" align="right" alt="VA Linux Systems" hspace="5" vspace="7" border=0 width="136" height="40"></A>
    </TD><!-- right of logo -->
  </TR>
  <TR><TD bgcolor="#543a48" colspan=2><IMG src="http://sourceforge.net/images/blank.gif" height=2 vspace=0></TD></TR>
</TABLE>
<!-- end top title table -->

<!-- center table -->
<TABLE width="100%" border="0" cellspacing="0" cellpadding="2" bgcolor="#FFFFFF" align="center">
  <TR>
    <TD>
      <CENTER><BR>
      <H1>Welcome to http://<?php print $headers[Host]; ?>/</H1>
      <P>I'm sorry but I had not the time to create a ready web page design.
      Thus I will just add the stuff here until I get the time to do it right.
      See <a href="http://sourceforge.net/project/?group_id=5068">the project
      page</a> for further information, but don't expect much for now.
      </P>
      </CENTER>
      <P>Here is the first contribution, a silly bean to edit integer values.
      The Applet below is a showcase for that bean. You may even add any
      other component to the bean creation list with "New Bean" from the
      file menu.<BR>
      Your browser will need Swing 1.1 installed for the java engine to
      be able to run this applet. The easiest way is to install a Java 2
      release from sun, these include the java plugin.<BR>

<OBJECT classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93"
     width="416" height="321" align="baseline"
     codebase="http://java.sun.com/products/plugin/1.2.2/jinstall-1_2_2-win.cab#Version=1,2,2,0">
 <PARAM NAME="archive" VALUE="fraglets-beans.jar">
 <PARAM NAME="code" VALUE="net.sourceforge.fraglets.beans.Showcase.class">
 <PARAM NAME="type" VALUE="application/x-java-applet;version=1.2.2">
 <PARAM NAME="scriptable" VALUE="false">
 <COMMENT>
     <EMBED type="application/x-java-applet;version=1.2.2"
       width="416" height="321" align="baseline"
       archive="fraglets-beans.jar"
       code="net.sourceforge.fraglets.beans.Showcase.class"
       pluginspage="http://java.sun.com/products/plugin/1.2/plugin-install.html">
     <NOEMBED>
	<APPLET WIDTH=416 HEIGHT=321 ARCHIVE="fraglets-beans.jar"
	  CODE="net.sourceforge.fraglets.beans.Showcase.class">
            No JDK 1.2 support for APPLET!!
	</APPLET>
     </NOEMBED></EMBED>
     </COMMENT>
 </OBJECT><BR>
	The code and jar for this applet can be found <a
href="http://cvs.sourceforge.net/cgi-bin/cvsweb.cgi/fraglets/java/net/sourceforge/fraglets/beans/?cvsroot=fraglets"
	>here in the CVS repository</a>, and standard
        JavaDoc is <a href="javadoc/">here</a>.

      </P>
    </TD>
  </TR>
</TABLE>
<!-- end center table -->

<!-- footer table -->
<TABLE width="100%" border="0" cellspacing="0" cellpadding="2" bgcolor="737b9c">
  <TR>
    <TD align="center"><FONT color="#ffffff"><SPAN class="titlebar">
      All trademarks and copyrights on this page are properties of their respective owners. Forum comments are owned by the poster. The rest is copyright ©1999-2000 VA Linux Systems, Inc.</SPAN></FONT>
    </TD>
  </TR>
</TABLE>

<!-- end footer table -->
</BODY>
</HTML>
