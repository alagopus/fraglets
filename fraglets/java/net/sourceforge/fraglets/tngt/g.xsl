<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : g 
    Created on : October 5, 2002 
    Author     : kre
    Comment
        purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html" indent="yes"/>
    <xsl:strip-space elements="*"/>

    <xsl:param name="image-base">i/</xsl:param>
    <xsl:param name="thumb-base">t/</xsl:param>
    
<!--    <xsl:template match="/">-->
<!--        <HTML>-->
<!--        <HEAD><TITLE>EQ Image Gallery</TITLE></HEAD>-->
<!--        <BODY>-->
<!--        <xsl:apply-templates/>-->
<!--        </BODY>-->
<!--        </HTML>-->
<!--    </xsl:template>-->
    
    <xsl:template match="image">
        <A HREF="{$image-base}{@name}">
            <IMG SRC="{$thumb-base}{@name}" ALT="{@name}"/>
        </A>
        <xsl:text> </xsl:text>
    </xsl:template>

</xsl:stylesheet> 