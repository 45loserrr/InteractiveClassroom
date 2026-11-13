-- =============================================================
-- 移动互动课堂系统 · 基础表结构（MySQL 8.0）
-- 说明：仅建表，不含演示数据。执行方式：
--   mysql -u root -p classroom < schema.sql
-- 字符集统一 utf8mb4，支持中文与 emoji。
-- =============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------
-- 用户表：教师 / 学生
-- ---------------------------
CREATE TABLE IF NOT EXISTS `user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`    VARCHAR(64)  NOT NULL COMMENT '登录账号（教师账号 / 学生学号）',
  `password`    VARCHAR(100) DEFAULT NULL COMMENT '密码（BCrypt），学生免注册可为空',
  `real_name`   VARCHAR(64)  NOT NULL COMMENT '真实姓名',
  `role`        VARCHAR(16)  NOT NULL DEFAULT 'student' COMMENT '角色：teacher / student',
  `class_name`  VARCHAR(64)  DEFAULT NULL COMMENT '班级',
  `student_no`  VARCHAR(32)  DEFAULT NULL COMMENT '学号',
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 正常 1 删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`),
  KEY `idx_user_role` (`role`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表';

-- ---------------------------
-- 课堂表：6 位课堂码
-- ---------------------------
CREATE TABLE IF NOT EXISTS `classroom` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`           VARCHAR(128) NOT NULL COMMENT '课堂名称',
  `class_name`     VARCHAR(64)  DEFAULT NULL COMMENT '班级',
  `code`           CHAR(6)      NOT NULL COMMENT '6 位数字课堂码',
  `teacher_id`     BIGINT       NOT NULL COMMENT '教师用户 ID',
  `expected_count` INT          NOT NULL DEFAULT 0 COMMENT '应到人数',
  `status`         VARCHAR(16)  NOT NULL DEFAULT 'pending' COMMENT '状态：pending / ongoing / closed',
  `checkin_open`   TINYINT      NOT NULL DEFAULT 1 COMMENT '签到窗口是否开放：1 是 0 否',
  `started_at`     DATETIME     DEFAULT NULL COMMENT '开始时间',
  `ended_at`       DATETIME     DEFAULT NULL COMMENT '结束时间',
  `created_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_classroom_code` (`code`),
  KEY `idx_classroom_teacher` (`teacher_id`),
  KEY `idx_classroom_status` (`status`),
  CONSTRAINT `fk_classroom_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '课堂表';

-- ---------------------------
-- 签到表：同一课堂同一学生仅一条
-- ---------------------------
CREATE TABLE IF NOT EXISTS `checkin` (
  `id`           BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `classroom_id` BIGINT   NOT NULL COMMENT '课堂 ID',
  `student_id`   BIGINT   NOT NULL COMMENT '学生用户 ID',
  `checked_at`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
  `client_ip`    VARCHAR(64) DEFAULT NULL COMMENT '客户端 IP（审计用）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_checkin_class_student` (`classroom_id`, `student_id`),
  KEY `idx_checkin_student` (`student_id`),
  CONSTRAINT `fk_checkin_classroom` FOREIGN KEY (`classroom_id`) REFERENCES `classroom` (`id`),
  CONSTRAINT `fk_checkin_student` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '签到表';

-- ---------------------------
-- 题目表：单选 / 多选
-- ---------------------------
CREATE TABLE IF NOT EXISTS `exercise` (
  `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `classroom_id`     BIGINT       NOT NULL COMMENT '所属课堂 ID',
  `type`             VARCHAR(16)  NOT NULL DEFAULT 'single' COMMENT '题型：single / multiple',
  `stem`             TEXT         NOT NULL COMMENT '题干',
  `options`          JSON         DEFAULT NULL COMMENT '选项，如 [{"key":"A","text":"..."}]',
  `answer`           VARCHAR(16)  NOT NULL COMMENT '正确答案，多选逗号分隔，如 A,C',
  `duration_seconds` INT          NOT NULL DEFAULT 60 COMMENT '答题时长（秒），0 不限时',
  `status`           VARCHAR(16)  NOT NULL DEFAULT 'draft' COMMENT '状态：draft / published / closed',
  `published_at`     DATETIME     DEFAULT NULL COMMENT '发布时间',
  `created_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`          TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_exercise_classroom` (`classroom_id`),
  KEY `idx_exercise_status` (`status`),
  CONSTRAINT `fk_exercise_classroom` FOREIGN KEY (`classroom_id`) REFERENCES `classroom` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '题目表';

-- ---------------------------
-- 作答表：同一题同一学生仅一条
-- ---------------------------
CREATE TABLE IF NOT EXISTS `answer` (
  `id`           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
  `exercise_id`  BIGINT      NOT NULL COMMENT '题目 ID',
  `student_id`   BIGINT      NOT NULL COMMENT '学生用户 ID',
  `content`      VARCHAR(32) NOT NULL COMMENT '作答内容，多选逗号分隔，如 A,C',
  `is_correct`   TINYINT     NOT NULL DEFAULT 0 COMMENT '是否正确：1 是 0 否',
  `submitted_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_answer_exercise_student` (`exercise_id`, `student_id`),
  KEY `idx_answer_student` (`student_id`),
  CONSTRAINT `fk_answer_exercise` FOREIGN KEY (`exercise_id`) REFERENCES `exercise` (`id`),
  CONSTRAINT `fk_answer_student` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '作答表';

-- ---------------------------
-- 抢答表：以服务器毫秒时间戳判定首答（is_first=1 唯一）
-- ---------------------------
CREATE TABLE IF NOT EXISTS `race` (
  `id`          BIGINT     NOT NULL AUTO_INCREMENT COMMENT '主键',
  `exercise_id` BIGINT     NOT NULL COMMENT '题目 ID',
  `student_id`  BIGINT     NOT NULL COMMENT '学生用户 ID',
  `is_first`    TINYINT    NOT NULL DEFAULT 0 COMMENT '是否首答：1 是 0 否',
  `raced_at`    DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '抢答时间（毫秒精度，服务端时间）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_race_exercise_student` (`exercise_id`, `student_id`),
  KEY `idx_race_exercise_time` (`exercise_id`, `raced_at`),
  CONSTRAINT `fk_race_exercise` FOREIGN KEY (`exercise_id`) REFERENCES `exercise` (`id`),
  CONSTRAINT `fk_race_student` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '抢答表';

SET FOREIGN_KEY_CHECKS = 1;
