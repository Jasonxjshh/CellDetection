DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
                             `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
                             `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '姓名',
                             `username` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '用户名',
                             `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '密码',
                             `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '手机号',
                             `email` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '电子邮箱',
                             `sex` varchar(2) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '性别',
                             `age` int(0) NOT NULL  COMMENT '年龄',
                             `avatar` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '头像',
                             `role` int(0) NOT NULL  COMMENT '用户角色: 0管理员, 1医生, 2患者',
                             `create_at` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                             `update_at` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                             `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建人',
                             `update_by` bigint(0) NULL DEFAULT NULL COMMENT '修改人',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '用户表' ROW_FORMAT = Dynamic;
