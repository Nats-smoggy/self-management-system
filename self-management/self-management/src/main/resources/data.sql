INSERT INTO sys_user (id, username, password, nickname, role, status)
VALUES
  (1, 'admin', 'admin123', 'system-admin', 'ADMIN', 1),
  (4, 'test01', '123456', 'test01', 'USER', 1),
  (5, '807968335', '12345', 'user807968335', 'USER', 1),
  (12, 'asd123', 'g021166', 'gfs', 'USER', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_plan (id, user_id, title, status, deadline, content)
VALUES
  (5, 4, 'Task A', 'DONE', '2026-03-21', NULL),
  (6, 5, 'Task B', 'DONE', '2026-03-12', 'demo'),
  (9, 5, 'Task C', 'IN_PROGRESS', '2026-03-15', NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_finance (id, user_id, type, amount, category, remark, record_date)
VALUES
  (1, 1, 'expense', 25.50, 'food', 'lunch', '2026-03-13'),
  (4, 5, 'expense', 0.01, 'food', 'demo', '2026-03-13'),
  (7, 5, 'income', 500.00, 'award', '', '2026-03-15')
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_focus (id, user_id, duration, focus_date)
VALUES
  (1, 4, 25, '2026-04-02'),
  (2, 5, 1, '2026-05-08')
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_course (id, user_id, course_name, day_of_week, section, location, teacher)
VALUES
  (53, 5, 'Advanced Math', 1, 1, 'Room 401', 'Teacher Zhang'),
  (54, 5, 'Advanced Math', 1, 2, 'Room 401', 'Teacher Zhang'),
  (55, 5, 'College English', 2, 3, 'Room 503', 'Teacher Li')
ON CONFLICT (id) DO NOTHING;

INSERT INTO exam_plan (id, user_id, exam_name, exam_date, remark)
VALUES
  (2, 5, 'Computer Exam', '2026-05-31', 'good luck')
ON CONFLICT (id) DO NOTHING;

INSERT INTO weight_record (id, user_id, weight, record_date)
VALUES
  (1, 5, 65.20, '2026-05-27'),
  (2, 5, 65.30, '2026-05-28')
ON CONFLICT (id) DO NOTHING;
