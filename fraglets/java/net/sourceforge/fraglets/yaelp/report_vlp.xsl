<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="*"/>

<xsl:param name="background">#fbe3b3</xsl:param>
<xsl:param name="border">#993300</xsl:param>
<xsl:param name="highlight0">#cc9966</xsl:param>
<xsl:param name="highlight1">#e3be8c</xsl:param>

<xsl:template match="roster"><TABLE WIDTH="100%" STYLE="background-color: {$border}; horizontal-align: left; vertical-align: middle;" BORDER="0" CELLSPACING="1" CELLPADDING="2"><xsl:apply-templates/></TABLE></xsl:template>
<xsl:template match="heading"><TR STYLE="background-color: {$highlight0};"><xsl:apply-templates/></TR></xsl:template>
<xsl:template match="column[@id = 'name']|column[@id = 'points']"><TD class="headings"><B><xsl:apply-templates/></B></TD></xsl:template>
<xsl:template match="avatar[position() mod 2 = 0]"><TR style="background-color: {$highlight1};"><xsl:apply-templates/></TR></xsl:template>
<xsl:template match="avatar"><TR style="background-color: {$background};"><xsl:apply-templates/></TR></xsl:template>
<xsl:template match="name|points"><TD class="m"><xsl:value-of select="."/>&#xa0;</TD></xsl:template>
<!-- ignore all else --><xsl:template match="*" />
</xsl:stylesheet>