<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xt="http://www.jclark.com/xt"
                version="1.0">

<xsl:output doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
            doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
            indent="yes"
            method="html"/>

<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Base Nano List</title>
</head>
<body style="font-family: arial, helvetica, sans-serif;">
<xsl:apply-templates/>
</body>
</html>
</xsl:template>

<xsl:template match="base-nano-list">
<center>
    <table border="0" cellpadding="0" cellspacing="0" width="95%">
    <tr><td bgcolor="black">
        <table border="0" width="100%" cellpadding="4" cellspacing="1" align="left" valign="middle">
        <tr>
            <th bgcolor="white">Name</th>
            <th bgcolor="white">Body</th>
            <th bgcolor="white">Skill</th>
            <th bgcolor="white">Type</th>
        </tr>
        <xsl:apply-templates/>
        </table>
    </td></tr></table>
</center>
</xsl:template>

<xsl:template match="base-nano-cluster">
<tr>
<td bgcolor="white"><xsl:value-of select="@name"/></td>
<xsl:apply-templates/>
</tr>
</xsl:template>

<xsl:template match="property">
<td bgcolor="white"><xsl:value-of select="."/></td>
</xsl:template>

</xsl:stylesheet>

