<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : selection_csv 
    Created on : July 19, 2002 
    Author     : kre
    Comment
        purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format">
<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="*"/>

<xsl:param name="background">#fbe3b3</xsl:param>
<xsl:param name="border">#330000;</xsl:param>
<xsl:param name="highlight0">#ccc0a7</xsl:param>
<xsl:param name="highlight1">#d8cbaf</xsl:param>
<xsl:param name="header">#f5f5dc</xsl:param>

<xsl:template match="selection">
    <table width="100%" style="background-color: {$border}; horizontal-align: left; vertical-align: middle;" border="0" cellspacing="1" cellpadding="2"><xsl:apply-templates/></table>
</xsl:template>

<xsl:template match="row[position() mod 2 = 1]">
    <tr style="background-color: {$highlight1}"><xsl:apply-templates/></tr>
</xsl:template>

<xsl:template match="row">
    <tr style="background-color: {$highlight0}"><xsl:apply-templates/></tr>
</xsl:template>

<xsl:template match="col">
    <td class="m"><xsl:value-of select="."/></td>
</xsl:template>

</xsl:stylesheet> 