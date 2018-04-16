DROP TABLE IF EXISTS orders ;
CREATE TABLE `orders` (
 `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
 `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `price` double not NULL COMMENT '价格',
   PRIMARY KEY (`id`)
   ) ;
