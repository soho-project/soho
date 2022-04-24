insert into dev.admin_resource (id, name, route, type, remarks, created_time, visible, sort, breadcrumb_parent_id, zh_name, icon_name)
values  (1, 'Admin', '/', 0, 'Admin', '2022-01-30 23:42:25', 1, 1, 0, '后台管理', 'dashboard'),
        (2, 'System Mange', '/', 1, '系统管理目录', '2022-04-06 23:00:40', 1, 3, 1, '系统管理', 'AppstoreOutlined'),
        (3, 'Users', '/user', 1, '用户管理', '2022-01-30 23:42:25', 1, 1, 2, '用户管理', 'user'),
        (4, 'Posts', '/post', 1, '文章管理', '2022-01-30 23:42:25', 1, 100, 2, '文章管理', 'shopping-cart'),
        (5, 'User Detail', '/user/:id', 1, '用户详情', '2022-01-30 23:42:25', 0, 100, 2, '用户详情', 'user'),
        (6, 'Request', '/request', 1, '接口模拟请求', '2022-01-30 23:42:25', 1, 100, 2, '接口模拟请求', 'api'),
        (7, 'UI Element', '/ui', 0, 'UI组件', '2022-01-30 23:42:25', 0, 100, 2, 'UI组件', 'camera-o'),
        (8, 'Editor', '/editor', 1, '编辑', '2022-01-30 23:42:25', 0, 100, 2, '编辑', null),
        (9, 'Charts', '/Charts', 1, '图标', '2022-01-30 23:42:25', 0, 100, 1, '图表', 'area-chart'),
        (10, 'ECharts', '/chart/ECharts', 1, 'ECharts', '2022-01-30 23:42:25', 0, 100, 9, 'ECharts', 'area-chart'),
        (11, 'HighCharts', '/chart/highCharts', 1, '/chart/highCharts', '2022-01-30 23:42:25', 0, 100, 9, 'Rechartst', 'area-chart'),
        (12, 'Rechartst', 'Rechartst', 1, '/chart/Recharts', '2022-01-30 23:42:25', 0, 100, 1, 'Rechartst', 'area-chart'),
        (13, 'Role', '/role', 1, '角色管理', '2022-01-30 23:42:25', 1, 1, 2, '角色管理', 'user'),
        (14, 'Resource', '/resource', 1, '资源管理', '2022-01-30 23:42:25', 1, 1, 2, '资源管理', 'api'),
        (15, 'tesaaccc', '/test', 0, 'test', '2022-04-05 16:31:51', 0, 1, 1, 'test', 'test'),
        (17, 't2', '/t2', 0, null, '2022-04-05 17:53:50', 0, 1, 15, 'taddfasd', 'ddd'),
        (18, 'Config', '/config', 1, '系统配置', '2022-04-06 00:06:26', 1, 20, 2, '系统配置', 'SettingOutlined'),
        (19, 'Dashboard', '/dashboard', 1, '首页', '2022-01-30 23:42:25', 1, 0, 1, '仪表盘', 'dashboard'),
        (21, 'notifications', '/admin_notification', 1, '系统通知', '2022-04-24 02:36:48', 1, 20, 2, '系统通知', 'MessageOutlined');