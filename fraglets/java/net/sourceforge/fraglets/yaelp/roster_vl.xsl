<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="*"/>
<xsl:template match="roster"><TABLE WIDTH="100%" STYLE="background-color: black; horizontal-align: left; vertical-align: middle;" BORDER="0" CELLSPACING="1" CELLPADDING="2"><xsl:apply-templates/></TABLE></xsl:template>
<xsl:template match="heading"><TR STYLE="background-color: #5F9EA0; color: #F9E6FD;"><xsl:apply-templates/></TR></xsl:template>
<xsl:template match="column[@id = 'guild']|column[@id = 'zone']"/>
<xsl:template match="column"><TD class="headings"><B><xsl:apply-templates/></B></TD></xsl:template>
<xsl:template match="avatar"><TR style="background-color: #6A74B4;"><xsl:apply-templates/></TR></xsl:template>
<xsl:template match="guild|zone"/>
<xsl:template match="name|culture|class|level|time"><TD class="m"><xsl:apply-templates/>&#xa0;</TD></xsl:template>
</xsl:stylesheet>