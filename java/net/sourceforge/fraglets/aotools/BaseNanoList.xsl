<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<xsl:output doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
            doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
            indent="yes"/>

<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Base Nano List</title>
<style type="text/css">
body { font-family: sans-serif; background-color: #000066 }
      :link { color: #007d71 }
      .notice { text-align: left; color: black; background-color: #C88 }
      .top-banner { text-align: center; margin-bottom: .7cm; }
      .main-box { width: 99%; background-color: white; }
      .pagetitle { font-size: 24pt; padding: 2%}
      .intro { font-size: 80%; margin: 2% 5% 2% 5%}
      .left-content { float: left; font-size: 80%; width: 15%; 
                      padding: 1%; margin-right: 1%}
      .right-content { float: right; font-size: 80%; width: 20%; 
                        padding: 1%; margin-left: 1%}
      .side-item { width: 100%; border: solid #bbb thin; margin-bottom: .5em }
      .side-item-title { background-color: #bbb; font-weight: bold }
      .side-item-content { padding: 2%  }
      .centre-content { margin-left: 1%; margin-right: 1% }
      .date {color: #401; font-size: 80%; font-weight: bold }
      .bottom { font-style: italic; clear: both; margin: 1%; font-size: 80% }
    </style>
</head>
<body>
<xsl:apply-templates/>
</body>
</html>
</xsl:template>

<xsl:template match="base-nano-list">
<table border="2">
<tr><td>Name</td><td>Body</td><td>Skill</td><td>Type</td></tr>
<xsl:apply-templates/>
</table>
</xsl:template>

<xsl:template match="base-nano-cluster">
<tr><td><xsl:value-of select="@name"/></td><xsl:apply-templates/></tr>
</xsl:template>

<xsl:template match="property">
<td><xsl:value-of select="."/></td>
</xsl:template>

</xsl:stylesheet>

