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
<title>Joined Nano List</title>
    <style type="text/css">
        body { font-family: sans-serif; background-color: white; }
    </style>
</head>
<body>
<xsl:apply-templates/>
</body>
</html>
</xsl:template>

<xsl:template match="present-nano-list">
<table border="1" cellpadding="2" cellspacing="0" align="left" valign="middle">
<tr>
    <th>Name</th>
    <th>Skill</th>
    <th>Faded</th>
    <th>Bright</th>
    <th>Shining</th>
</tr>
<xsl:apply-templates/>
</table>
</xsl:template>

<xsl:template match="present-nano-cluster">
<tr>
<td><xsl:value-of select="@name"/><br/></td>
<td><xsl:value-of select="@skill"/><br/></td>
<td><xsl:apply-templates select="present-nano-type[@name='Faded']"/><br/></td>
<td><xsl:apply-templates select="present-nano-type[@name='Bright']"/><br/></td>
<td><xsl:apply-templates select="present-nano-type[@name='Shining']"/><br/></td>
</tr>
</xsl:template>

</xsl:stylesheet>

