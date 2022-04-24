-- we don't know how to generate root <with-no-name> (class Root) :(
create table admin_config
(
    id int unsigned auto_increment
        primary key,
    group_key varchar(200) collate utf8_bin null comment '配置文件分组名',
    `key` varchar(200) null comment '配置信息唯一识别key',
    value text null comment '配置信息值',
    `explain` varchar(500) null comment '说明',
    type tinyint null comment '配置信息类型',
    updated_time datetime null comment '更新时间',
    created_time datetime null comment '创建时间',
    constraint `key-unique`
        unique (`key`)
)
    comment '系统配置表';

create table admin_config_group
(
    id int auto_increment
        primary key,
    `key` varchar(500) not null comment 'group key',
    name varchar(500) null comment '组名',
    created_time datetime null comment '创建时间'
)
    comment '配置分组表';

create table admin_notification
(
    id int auto_increment
        primary key,
    admin_user_id int null comment '接收人',
    title varchar(500) null comment '标题',
    create_admin_user_id int not null comment '创建者 0 为系统发送',
    content text null comment '通知内容',
    cteated_time datetime null comment '创建时间',
    is_read tinyint default 0 null comment '是否已读 0 未读 1 已读',
    constraint admin_user_id_UNIQUE
        unique (admin_user_id)
)
    comment '管理员通知';

create index `index-user_id-is_read`
    on admin_notification (admin_user_id, is_read);

create table admin_resource
(
    id int auto_increment
        primary key,
    name varchar(500) null comment '英文名',
    route varchar(500) null,
    type tinyint(1) null comment '资源类型',
    remarks varchar(500) null,
    created_time datetime null,
    visible tinyint default 1 null comment '资源界面是否可见',
    sort int default 100 null comment '排序',
    breadcrumb_parent_id int default 1 null comment '父ID',
    zh_name varchar(500) null comment '中文名',
    icon_name varchar(500) null comment '菜单图标'
);

create table admin_role
(
    id int unsigned auto_increment
        primary key,
    name varchar(45) null,
    remarks varchar(255) null,
    created_time datetime null,
    enable tinyint default 1 null comment '是否启用'
);

create table admin_role_resource
(
    id int unsigned auto_increment
        primary key,
    role_id int unsigned null,
    resource_id int unsigned null,
    created_time datetime null
);

create table admin_role_user
(
    id int unsigned auto_increment
        primary key,
    role_id int unsigned null,
    user_id int unsigned null,
    status tinyint null,
    created_time datetime null
);

create table admin_user
(
    id bigint auto_increment,
    username varchar(128) not null,
    phone varchar(11) not null,
    nick_name varchar(45) null,
    real_name varchar(45) null,
    avatar varchar(500) default '//image.zuiidea.com/photo-1519336555923-59661f41bb45.jpeg?imageView2/1/w/200/h/200/format/webp/q/75|imageslim' null,
    email varchar(255) null,
    password varchar(300) null,
    updated_time datetime null,
    created_time datetime null,
    sex tinyint null comment '性别',
    age int null comment '年龄',
    is_deleted tinyint default 0 null comment '软删除标记',
    primary key (id, username),
    constraint `unique-phone`
        unique (phone),
    constraint `unique-username`
        unique (username)
);

create table admin_user_login_log
(
    id int auto_increment comment 'ID'
        primary key,
    admin_user_id int null comment '后台用户ID',
    client_ip varchar(50) null comment '客户端IP地址考虑IPv6字段适当放宽',
    token varchar(3000) null comment '给用户发放的token',
    created_time datetime null comment '创建时间'
)
    comment '用户登录日志';

create table hello
(
    id int auto_increment
        primary key,
    name varchar(45) null,
    value varchar(45) null,
    updated_time datetime null comment '更新时间',
    created_time datetime null comment '创建时间'
);

