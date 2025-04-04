insert into beauty_care.member
    (id, login_id, name, password, role_id, is_use, created_date_time, updated_date_time, created_by, updated_by, last_login_date_time)
values
    (1, 'admin', '관리자', '$2a$10$NBx89slgit4rozmzdjtqSONMsiTEjLNgeV2eJYS9agGaNO1fq.NSK',
     'ADMIN', true,null, null, null, null, null);

insert into beauty_care.code(`name`,`id`,`upper_id`,`description`,`sort_number`,`is_use`,`created_date_time`,`updated_date_time`)VALUES
('시스템', 'sys', null, '시스템(최상위)', 1, true, NOW(), NOW()),
('동의 상태', 'sys:agree', 'sys', '동의 상태', 1, true, NOW(), NOW()),
('동의', 'sys:agree:Y', 'sys:agree', '동의', 1, true, NOW(), NOW()),
('미동의', 'sys:agree:N', 'sys:agree', '미동의', 2, true, NOW(), NOW());

insert into beauty_care.role (role_name, url_patterns) VALUES
('ADMIN', '{"pattern":["/admin/**", "/user/**"]}'),
('USER', '{"pattern":["/user/**"]}');