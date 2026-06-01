DROP TABLE IF EXISTS weight_record;
DROP TABLE IF EXISTS exam_plan;
DROP TABLE IF EXISTS sys_course;
DROP TABLE IF EXISTS sys_focus;
DROP TABLE IF EXISTS sys_finance;
DROP TABLE IF EXISTS sys_plan;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  nickname VARCHAR(50),
  avatar TEXT,
  major VARCHAR(100),
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  role VARCHAR(20) DEFAULT 'USER',
  status INTEGER DEFAULT 1
);

CREATE TABLE sys_plan (
  id SERIAL PRIMARY KEY,
  user_id INTEGER,
  title VARCHAR(255) NOT NULL,
  status VARCHAR(50) DEFAULT '未开始',
  deadline VARCHAR(50),
  content VARCHAR(255)
);

CREATE TABLE sys_finance (
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL,
  type VARCHAR(20) NOT NULL,
  amount NUMERIC(10, 2) NOT NULL,
  category VARCHAR(50) NOT NULL,
  remark VARCHAR(255),
  record_date VARCHAR(50)
);

CREATE TABLE sys_focus (
  id SERIAL PRIMARY KEY,
  user_id INTEGER,
  duration INTEGER,
  focus_date VARCHAR(20)
);

CREATE TABLE sys_course (
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL,
  course_name VARCHAR(100) NOT NULL,
  day_of_week INTEGER NOT NULL,
  section INTEGER NOT NULL,
  location VARCHAR(100),
  teacher VARCHAR(50)
);

CREATE TABLE exam_plan (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  exam_name VARCHAR(100) NOT NULL,
  exam_date DATE NOT NULL,
  remark VARCHAR(255),
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE weight_record (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  weight NUMERIC(5, 2) NOT NULL,
  record_date DATE NOT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_user_date UNIQUE (user_id, record_date)
);

CREATE INDEX idx_finance_user_id ON sys_finance(user_id);
CREATE INDEX idx_plan_user_id ON sys_plan(user_id);
CREATE INDEX idx_focus_user_id ON sys_focus(user_id);
CREATE INDEX idx_course_user_id ON sys_course(user_id);
CREATE INDEX idx_exam_user_id ON exam_plan(user_id);
CREATE INDEX idx_weight_user_id ON weight_record(user_id);

INSERT INTO sys_user (id, username, password, nickname, role, status) VALUES
  (4, 'test01', '123456', '弓田宇', 'USER', 1),
  (5, '807968335', '12345', '弓田宇', 'USER', 1),
  (12, 'asd123', 'g021166', 'gfs', 'USER', 1);

INSERT INTO sys_plan (id, user_id, title, status, deadline, content) VALUES
  (5, 4, '打cf', '已完成', '2026-03-21', NULL),
  (6, 5, '打瓦', '已完成', '2026-03-12', 'czsczsc'),
  (9, 5, 'VS的VS', '进行中', '2026-03-15', NULL);

INSERT INTO sys_finance (id, user_id, type, amount, category, remark, record_date) VALUES
  (1, 1, '支出', 25.50, '餐饮美食', '中午吃黄焖鸡米饭', '2026-03-13'),
  (4, 5, '支出', 0.01, '餐饮美食', '达瓦', '2026-03-13'),
  (7, 5, '收入', 500.00, '奖学金', '', '2026-03-15');

INSERT INTO sys_focus (id, user_id, duration, focus_date) VALUES
  (1, 4, 25, '2026-04-02'),
  (2, 5, 1, '2026-05-08');

INSERT INTO sys_course (id, user_id, course_name, day_of_week, section, location, teacher) VALUES
  (53, 5, '高等数学', 1, 1, '一南401', '张亮钟'),
  (54, 5, '高等数学', 1, 2, '一南401', '张亮钟'),
  (55, 5, '大学英语', 2, 3, '主楼503', '李海燕');

INSERT INTO exam_plan (id, user_id, exam_name, exam_date, remark) VALUES
  (2, 5, '计算机二级', '2026-05-31', '加油');

INSERT INTO weight_record (id, user_id, weight, record_date) VALUES
  (1, 5, 65.20, '2026-05-27'),
  (2, 5, 65.30, '2026-05-28');

SELECT setval('sys_user_id_seq', COALESCE((SELECT MAX(id) FROM sys_user), 1), true);
SELECT setval('sys_plan_id_seq', COALESCE((SELECT MAX(id) FROM sys_plan), 1), true);
SELECT setval('sys_finance_id_seq', COALESCE((SELECT MAX(id) FROM sys_finance), 1), true);
SELECT setval('sys_focus_id_seq', COALESCE((SELECT MAX(id) FROM sys_focus), 1), true);
SELECT setval('sys_course_id_seq', COALESCE((SELECT MAX(id) FROM sys_course), 1), true);
SELECT setval('exam_plan_id_seq', COALESCE((SELECT MAX(id) FROM exam_plan), 1), true);
SELECT setval('weight_record_id_seq', COALESCE((SELECT MAX(id) FROM weight_record), 1), true);
