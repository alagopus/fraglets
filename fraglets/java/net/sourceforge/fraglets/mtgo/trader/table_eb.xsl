<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="*"/>

<xsl:param name="heading-color">#909098</xsl:param>
<xsl:param name="odd-color">#d8cbaf</xsl:param>
<xsl:param name="even-color">#ccc0a7</xsl:param>
<xsl:template match="table"><TABLE WIDTH="100%" STYLE="horizontal-align: left; vertical-align: middle;" BORDER="0" CELLSPACING="0" CELLPADDING="1"><xsl:apply-templates/></TABLE></xsl:template>
<xsl:template match="heading"><TR STYLE="background-color: {$heading-color}; color: white;"><xsl:apply-templates/></TR></xsl:template>
<xsl:template match="column"><TD><B><xsl:apply-templates/></B></TD></xsl:template>
<xsl:template match="row[position() mod 2 = 1]"><TR style="background-color: {$odd-color};"><xsl:apply-templates/></TR></xsl:template>
<xsl:template match="row"><TR style="background-color: {$even-color};"><xsl:apply-templates/></TR></xsl:template>
<xsl:template match="row/*"><TD><xsl:apply-templates/></TD></xsl:template>

</xsl:stylesheet>