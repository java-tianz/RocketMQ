DROP TABLE IF EXISTS `t_transaction`;
CREATE TABLE `t_transaction` (
  `offset` bigint(20) NOT NULL,
  `producerGroup` varchar(64) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  PRIMARY KEY (`offset`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;