<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : selection_csv 
    Created on : July 19, 2002 
    Author     : kre
    Comment
        transforming intermediate XML selection to CSV output text.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format">
<xsl:output method="text" omit-xml-declaration="yes"/>
<xsl:preserve-space elements="*"/>

<!-- template rule matching row element-->
<xsl:template match="row"><xsl:apply-templates select="col"/><xsl:text>
</xsl:text></xsl:template>
<xsl:template match="col[1]">"<xsl:value-of select="."/>"</xsl:template>
<xsl:template match="col">,"<xsl:value-of select="."/>"</xsl:template>

</xsl:stylesheet>

