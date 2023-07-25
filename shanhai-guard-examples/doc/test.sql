CREATE TABLE `t_user` (
                          `name` varchar(100) DEFAULT NULL,
                          `create_time` datetime DEFAULT NULL,
                          `desc` text,
                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';