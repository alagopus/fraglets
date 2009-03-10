<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

<xsl:template match="roster">
<HEAD>
 <TITLE><xsl:value-of select="@title"/></TITLE>
</HEAD>
<BODY STYLE="color:#330000;" BGCOLOR="white">
<TABLE WIDTH="100%" STYLE="horizontal-align: left; vertical-align: middle;"
       BORDER="0" CELLSPACING="0" CELLPADDING="1">
<xsl:apply-templates/>
</TABLE>
</BODY>
</xsl:template>

<xsl:template match="heading">
<TR STYLE="background-color: #909098; color: white;">
<xsl:apply-templates/>
</TR>
</xsl:template>

<xsl:template match="column">
<TD><B><xsl:apply-templates/></B></TD>
</xsl:template>

<xsl:template match="avatar[position() mod 2 = 1]">
<TR style="background-color: #d8cbaf;">
<xsl:apply-templates/>
</TR>
</xsl:template>

<xsl:template match="avatar">
<TR style="background-color: #ccc0a7;">
<xsl:apply-templates/>
</TR>
</xsl:template>

<xsl:template match="name|culture|class|level|guild|zone|time">
<TD><xsl:apply-templates/>&#xa0;</TD>
</xsl:template>

</xsl:stylesheet>