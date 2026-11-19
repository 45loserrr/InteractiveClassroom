-- ============================================================
-- 移动互动课堂系统 初始化脚本（极简版）
-- 用法：mysql -u root -p < V1__init.sql
-- 说明：只有主键和普通索引，没有外键，方便大家各写各的
-- ============================================================

CREATE DATABASE IF NOT EXISTS classroom DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE classroom;

-- 先删旧表，保证脚本可以重复执行
DROP TABLE IF EXISTS `answer`;
DROP TABLE IF EXISTS `exercise`;
DROP TABLE IF EXISTS `checkin`;
DROP TABLE IF EXISTS `classroom`;
DROP TABLE IF EXISTS `user`;

-- 1. 用户表（PM 负责）
CREATE TABLE `user` (
  `id`        BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`  VARCHAR(50)  NOT NULL COMMENT '登录账号',
  `password`  VARCHAR(50)  NOT NULL COMMENT '密码（大作业用明文，不加密）',
  `role`      VARCHAR(20)  NOT NULL DEFAULT 'student' COMMENT '角色：teacher / student',
  `real_name` VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 课堂表（M2 负责：建课）
CREATE TABLE `classroom` (
  `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code`       VARCHAR(20) NOT NULL COMMENT '课堂口令/邀请码，学生用它签到',
  `name`       VARCHAR(100) NOT NULL COMMENT '课程名称',
  `teacher_id` BIGINT      NOT NULL COMMENT '老师 user.id',
  PRIMARY KEY (`id`),
  KEY `idx_code` (`code`),
  KEY `idx_teacher` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课堂表';

-- 3. 签到表（M1 负责）
CREATE TABLE `checkin` (
  `id`           BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `classroom_id` BIGINT   NOT NULL COMMENT '课堂 id',
  `student_id`   BIGINT   NOT NULL COMMENT '学生 user.id',
  `create_time`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
  PRIMARY KEY (`id`),
  KEY `idx_classroom` (`classroom_id`),
  KEY `idx_student` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到表';

-- 4. 题目表（M2 负责：发题）
CREATE TABLE `exercise` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `classroom_id` BIGINT       NOT NULL COMMENT '课堂 id',
  `title`        VARCHAR(500) NOT NULL COMMENT '题干',
  `options`      TEXT         COMMENT '选项，用 | 分隔，例如：A.苹果|B.香蕉|C.橘子',
  `answer`       VARCHAR(10)  NOT NULL COMMENT '正确答案，例如：A',
  PRIMARY KEY (`id`),
  KEY `idx_classroom` (`classroom_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- 5. 答题表（M4 负责）
CREATE TABLE `answer` (
  `id`            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
  `exercise_id`   BIGINT      NOT NULL COMMENT '题目 id',
  `student_id`    BIGINT      NOT NULL COMMENT '学生 user.id',
  `choose_option` VARCHAR(10) NOT NULL COMMENT '学生选的答案，例如：A',
  PRIMARY KEY (`id`),
  KEY `idx_exercise` (`exercise_id`),
  KEY `idx_student` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题表';

-- ============================================================
-- 初始账号：密码全部是 123456（明文）
-- 1 个老师 + 3 个学生
-- ============================================================
INSERT INTO `user` (`id`, `username`, `password`, `role`, `real_name`) VALUES
(1, 'teacher', '123456', 'teacher', '王老师'),
(2, 'stu01',   '123456', 'student', '张三'),
(3, 'stu02',   '123456', 'student', '李四'),
(4, 'stu03',   '123456', 'student', '王五');
