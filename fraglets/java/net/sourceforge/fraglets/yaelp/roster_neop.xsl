<?xml version='1.0'?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:date="http://www.jclark.com/xt/java/java.util.Date">

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="*"/>

<xsl:param name="border-color">#000000</xsl:param>
<xsl:param name="header-color">#004600</xsl:param>
<xsl:param name="class-color">#000066</xsl:param>
<xsl:param name="row-color">#000033</xsl:param>
<xsl:param name="text-color">#FFFFFF</xsl:param>
<xsl:param name="link-color">#00FF00</xsl:param>
<xsl:param name="star-color">#00FF00</xsl:param>
<xsl:param name="rank-color">#FFFF00</xsl:param>

<xsl:template match="/">
    <xsl:comment>Generated by Yaelp: <xsl:value-of select="date:to-string(date:new())"/></xsl:comment>
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="roster">
    <TABLE WIDTH="500" BORDER="0" CELLSPACING="2" CELLPADDING="2" ALIGN="center">
    <xsl:attribute name="style">background-color: <xsl:value-of select="$border-color"/>; color: <xsl:value-of select="$text-color"/>; vertical-align: middle; text-align: center</xsl:attribute>
    <xsl:for-each select="avatar[not(class = preceding-sibling::avatar/class)]">
        <xsl:sort select="class"/>
        <xsl:call-template name="classheader">
            <xsl:with-param name="class" select="class"/>
        </xsl:call-template>
        <xsl:apply-templates select="/roster/heading"/>
        <xsl:apply-templates select="/roster/avatar[class = current()/class]">
            <xsl:sort select="level" data-type="number" order="descending"/>
            <xsl:sort select="name"/>
        </xsl:apply-templates>
    </xsl:for-each>
    </TABLE>
</xsl:template>

<xsl:template match="heading">
    <TR>
    <xsl:attribute name="STYLE">background-color: <xsl:value-of select="$header-color"/>; text-align: center; font-weight: bold</xsl:attribute>
    <xsl:call-template name="columnheader">
        <xsl:with-param name="name" select="column[@id='name']"/>
    </xsl:call-template>
    <xsl:call-template name="columnheader">
        <xsl:with-param name="name" select="column[@id='level']"/>
    </xsl:call-template>
    <xsl:call-template name="columnheader">
        <xsl:with-param name="name" select="'AAxp'"/>
    </xsl:call-template>
    <xsl:call-template name="columnheader">
        <xsl:with-param name="name" select="column[@id='culture']"/>
    </xsl:call-template>
    <xsl:call-template name="columnheader">
        <xsl:with-param name="name" select="'Main Character'"/>
    </xsl:call-template>
    </TR>
</xsl:template>

<xsl:template match="avatar">
    <xsl:variable name="main" select="property[@name = 'Main']/@value"/>
    <xsl:variable name="aaxp" select="property[@name = 'AA']/@value"/>
    <TR BGCOLOR="{$row-color}">
        <xsl:apply-templates select="name">
        <xsl:with-param name="rank" select="property[@name = 'Rank']/@value"/>
        <xsl:with-param name="magelo" select="property[@name = 'Magelo']/@value"/>
        </xsl:apply-templates>
        <xsl:apply-templates select="level"/>
        <TD><xsl:value-of select="$aaxp"/></TD>
        <xsl:apply-templates select="culture"/>
        <TD><xsl:choose>
            <xsl:when test="not($main) or $main=''">/na</xsl:when>
            <xsl:when test="$main != name"><a href="#{$main}" style="color: #ffff00;"><xsl:value-of select="$main"/>'s</a></xsl:when>
            <xsl:otherwise>Main</xsl:otherwise>
        </xsl:choose></TD>
    </TR>
</xsl:template>

<xsl:template match="culture">
    <TD><xsl:value-of select="."/></TD>
</xsl:template>

<xsl:template match="level">
    <TD>
    <xsl:choose>
        <xsl:when test=". = '0'">&#xa0;</xsl:when>
        <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
    </xsl:choose>
    </TD>
</xsl:template>

<xsl:template match="name">
    <xsl:param name="magelo"/>
    <xsl:param name="rank"/>
    <TD ALIGN="left">
        <A NAME="{.}">
            <xsl:if test="$magelo">
                <xsl:attribute name="HREF">http://www.magelo.com/eq_view_profile.html?num=<xsl:value-of select="$magelo"/></xsl:attribute>
                <xsl:attribute name="TARGET">magelo</xsl:attribute>
                <xsl:attribute name="STYLE">color: <xsl:value-of select="$link-color"/></xsl:attribute>
            </xsl:if>
            <xsl:value-of select="."/>
        </A>
        <xsl:choose>
            <xsl:when test="$rank = 'an officer'">&#xa0;<SPAN STYLE="color: {$rank-color}">(officer)</SPAN></xsl:when>
            <xsl:when test="$rank = 'leader'">&#xa0;<SPAN STYLE="color: {$rank-color}"><b>(leader)</b></SPAN></xsl:when>
        </xsl:choose>
    </TD>
</xsl:template>

<xsl:template name="columnheader">
    <xsl:param name="name" select="'--'"/>
    <TD>
        <xsl:value-of select="$name"/>
        <xsl:if test="$name='Level'"><A HREF="#star" STYLE="color: {$star-color}">*</A></xsl:if>
   </TD>
</xsl:template>

<xsl:template name="classheader">
    <xsl:param name="class"/>
    <TR>
    <xsl:attribute name="STYLE">background-color: <xsl:value-of select="$border-color"/>; font-size: larger</xsl:attribute>
        <TD colspan="5">&#xa0;</TD>
    </TR>
    <TR>
    <xsl:attribute name="STYLE">background-color: <xsl:value-of select="$class-color"/>; text-align: center; font-weight: bold; font-size: larger</xsl:attribute>
        <TD colspan="5"><xsl:choose>
            <xsl:when test="$class=''">Unknown</xsl:when>
            <xsl:otherwise><xsl:value-of select="$class"/>s</xsl:otherwise>
        </xsl:choose></TD>
    </TR>
</xsl:template>

</xsl:stylesheet>