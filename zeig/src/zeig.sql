
CREATE TABLE `nm` (
  `id` int(11) NOT NULL auto_increment,
  `ns` int(11) NOT NULL default '0',
  `v` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `nm_sk` (`ns`,`v`)
) TYPE=MyISAM COMMENT='XML Name'; 

CREATE TABLE `ns` (
  `id` int(11) NOT NULL auto_increment,
  `uri` int(11) NOT NULL default '0',
  `v` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ns_uri` (`uri`),
  UNIQUE KEY `ns_v` (`v`)
) TYPE=MyISAM COMMENT='XML Namespace'; 

CREATE TABLE `pt` (
  `id` int(11) NOT NULL auto_increment,
  `hc` int(11) NOT NULL default '0',
  `v` text NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `pt_hc` (`hc`)
) TYPE=MyISAM COMMENT='Plain text'; 

CREATE TABLE `ve` (
  `id` int(11) NOT NULL auto_increment,
  `g` int(11) NOT NULL default '0',
  `v` int(11) NOT NULL default '0',
  `co` int(11) NOT NULL default '0',
  `ts` timestamp(14) NOT NULL,
  PRIMARY KEY  (`id`,`g`),
  KEY `ve_id` (`id`),
  KEY `ve_fk` (`v`)
) TYPE=MyISAM COMMENT='Version'; 

CREATE TABLE `xn` (
  `xt` int(11) NOT NULL default '0',
  `pos` int(11) NOT NULL default '0',
  `nm` int(11) NOT NULL default '0',
  `v` int(11) NOT NULL default '0',
  PRIMARY KEY  (`xt`,`pos`),
  KEY `xn_fk` (`nm`,`v`),
  KEY `xn_xt` (`xt`)
) TYPE=MyISAM COMMENT='XML Node'; 

CREATE TABLE `xt` (
  `id` int(11) NOT NULL auto_increment,
  `hc` int(11) NOT NULL default '0',
  `nm` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `xt_sk` (`hc`,`nm`)
) TYPE=MyISAM COMMENT='XML text'; 

CREATE TABLE `me` (
  `id` int(11) NOT NULL auto_increment,
  `hc` int(11) NOT NULL default '0',
  `t` varchar(200) NOT NULL default 'application/octet-stream',
  `v` longblob NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `me_hc` (`hc`),
  KEY `me_t` (`t`)
) TYPE=MyISAM COMMENT='Media'; 
