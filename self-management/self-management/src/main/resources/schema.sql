CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT PRIMARY KEY,
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

CREATE TABLE IF NOT EXISTS sys_plan (
  id INTEGER PRIMARY KEY,
  user_id INTEGER,
  title VARCHAR(255) NOT NULL,
  status VARCHAR(50) DEFAULT 'TODO',
  deadline VARCHAR(50),
  content VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS sys_finance (
  id INTEGER PRIMARY KEY,
  user_id INTEGER NOT NULL,
  type VARCHAR(20) NOT NULL,
  amount NUMERIC(10, 2) NOT NULL,
  category VARCHAR(50) NOT NULL,
  remark VARCHAR(255),
  record_date VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS sys_focus (
  id INTEGER PRIMARY KEY,
  user_id INTEGER,
  duration INTEGER,
  focus_date VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS sys_course (
  id INTEGER PRIMARY KEY,
  user_id INTEGER NOT NULL,
  course_name VARCHAR(100) NOT NULL,
  day_of_week INTEGER NOT NULL,
  section INTEGER NOT NULL,
  location VARCHAR(100),
  teacher VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS exam_plan (
  id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  exam_name VARCHAR(100) NOT NULL,
  exam_date DATE NOT NULL,
  remark VARCHAR(255),
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS weight_record (
  id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  weight NUMERIC(5, 2) NOT NULL,
  record_date DATE NOT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_user_date UNIQUE (user_id, record_date)
);

CREATE INDEX IF NOT EXISTS idx_finance_user_id ON sys_finance(user_id);
CREATE INDEX IF NOT EXISTS idx_plan_user_id ON sys_plan(user_id);
CREATE INDEX IF NOT EXISTS idx_focus_user_id ON sys_focus(user_id);
CREATE INDEX IF NOT EXISTS idx_course_user_id ON sys_course(user_id);
CREATE INDEX IF NOT EXISTS idx_exam_user_id ON exam_plan(user_id);
CREATE INDEX IF NOT EXISTS idx_weight_user_id ON weight_record(user_id);
