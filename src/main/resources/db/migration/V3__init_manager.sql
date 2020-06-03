/*==============================================================*/
/* DBMS name:      PostgreSQL 9.x                               */
/* Created on:     2020/5/26 20:57:03                           */
/*==============================================================*/

--模式创建
CREATE SCHEMA IF NOT EXISTS cmiep;


/*==============================================================*/
/* Table: manage_log                                        */
/*==============================================================*/
CREATE TABLE IF NOT EXISTS cmiep.manage_log (
   log_id               uuid                 not null,
   school_id            INT4                 null,
   create_time          TIMESTAMP            null,
   description          VARCHAR(255)         null,
   method               VARCHAR(255)         null,
   source               VARCHAR(255)         null,
   user_name            VARCHAR(255)         null,
   rule                 VARCHAR(50)          null,
   constraint PK_MANAGE_LOG primary key (log_id)
);

comment on table cmiep.manage_log is
'管理操作日志：manage_log';

comment on column cmiep.manage_log.log_id is
'日志ID：log_id';

comment on column cmiep.manage_log.school_id is
'学校ID：school_id';

comment on column cmiep.manage_log.create_time is
'操作时间：create_time';

comment on column cmiep.manage_log.description is
'描述：description';

comment on column cmiep.manage_log.method is
'操作类型：method';

comment on column cmiep.manage_log.source is
'来源：source';

comment on column cmiep.manage_log.user_name is
'操作用户姓名：user_name';

comment on column cmiep.manage_log.rule is
'操作用户角色：rule';

/*==============================================================*/
/* Table: manage_resource                                   */
/*==============================================================*/
CREATE TABLE IF NOT EXISTS cmiep.manage_resource (
   authority_id         SERIAL               not null,
   parent_id            INT4                 null,
   content              VARCHAR(255)         null,
   name                 VARCHAR(255)         null,
   route                VARCHAR(255)         null,
   type                 VARCHAR(255)         null,
   icon                 TEXT                 null,
   enabled              BOOL                 null,
   http_method          TEXT                 null,
   file_path            TEXT                 null,
   target_user_role     VARCHAR(64)[]        null,
   specify_user_id      VARCHAR(64)[]        null,
   manage               BOOL                 null,
   constraint PK_MANAGE_RESOURCE primary key (authority_id)
);

comment on table cmiep.manage_resource is
'管理资源信息：manage_resource';

comment on column cmiep.manage_resource.parent_id is
'父级ID：parent_id';

comment on column cmiep.manage_resource.authority_id is
'资源ID：authority_id';

comment on column cmiep.manage_resource.content is
'英文名称：content';

comment on column cmiep.manage_resource.name is
'中文名称：name';

comment on column cmiep.manage_resource.route is
'路由路径：route';

comment on column cmiep.manage_resource.type is
'资源类型：type';

comment on column cmiep.manage_resource.icon is
'图标：icon';

comment on column cmiep.manage_resource.enabled is
'是否可用：enabled';

comment on column cmiep.manage_resource.http_method is
'http请求方式：http_method';

comment on column cmiep.manage_resource.file_path is
'文件路径：file_path';

comment on column cmiep.manage_resource.target_user_role is
'面向角色：target_user_role';

comment on column cmiep.manage_resource.specify_user_id is
'指定用户：specify_user_id';

comment on column cmiep.manage_resource.manage is
'是否有管理系统：manage';

/*==============================================================*/
/* Table: manage_role                                       */
/*==============================================================*/
CREATE TABLE IF NOT EXISTS cmiep.manage_role (
   rule_id              SERIAL               not null,
   school_id            INT4                 null,
   content              VARCHAR(255)         null,
   name                 VARCHAR(255)         null,
   update_time          TIMESTAMP            null,
   constraint PK_MANAGE_ROLE primary key (rule_id)
);

comment on table cmiep.manage_role is
'管理角色：manage_role';

comment on column cmiep.manage_role.rule_id is
'角色ID：rule_id';

comment on column cmiep.manage_role.school_id is
'学校ID：school_id';

comment on column cmiep.manage_role.content is
'英文名：content';

comment on column cmiep.manage_role.name is
'中文名：name';

comment on column cmiep.manage_role.update_time is
'更新时间：update_time';

/*==============================================================*/
/* Table: manage_role_resource                              */
/*==============================================================*/
CREATE TABLE IF NOT EXISTS cmiep.manage_role_resource (
   rule_id              INT4                 not null,
   authority_id         INT4                 not null,
   constraint PK_MANAGE_ROLE_RESOURCE primary key (rule_id, authority_id)
);

comment on table cmiep.manage_role_resource is
'管理资源-角色关联表：manage_role_resource';

comment on column cmiep.manage_role_resource.rule_id is
'角色ID：rule_id';

comment on column cmiep.manage_role_resource.authority_id is
'资源ID：authority_id';

/*==============================================================*/
/* Table: manage_user                                       */
/*==============================================================*/
CREATE TABLE IF NOT EXISTS cmiep.manage_user (
   user_id              SERIAL               not null,
   school_id            INT4                 null,
   open_id              VARCHAR(255)         null,
   user_code            VARCHAR(255)         null,
   pass_word            VARCHAR(255)         null,
   cas_ticket           VARCHAR(255)         null,
   user_group           VARCHAR(255)         null,
   is_admin             BOOL                 null,
   head_path            VARCHAR(255)         null,
   head_url             VARCHAR(255)         null,
   user_name            VARCHAR(64)          null,
   update_time          TIMESTAMP            null,
   constraint PK_MANAGE_USER primary key (user_id)
);

comment on table cmiep.manage_user is
'管理用户表：manage_user';

comment on column cmiep.manage_user.user_id is
'用户ID：user_id';

comment on column cmiep.manage_user.school_id is
'学校ID：school_id';

comment on column cmiep.manage_user.open_id is
'微信openId：open_id';

comment on column cmiep.manage_user.user_code is
'用户账号：user_code';

comment on column cmiep.manage_user.pass_word is
'密码：pass_word';

comment on column cmiep.manage_user.cas_ticket is
'认证ticket：cas_ticket';

comment on column cmiep.manage_user.user_group is
'用户组：user_group';

comment on column cmiep.manage_user.is_admin is
'是否是管理员：is_admin';

comment on column cmiep.manage_user.head_path is
'头像保存路径：head_path';

comment on column cmiep.manage_user.head_url is
'头像访问路径：head_url';

comment on column cmiep.manage_user.user_name is
'用户姓名：user_name';

comment on column cmiep.manage_user.update_time is
'更新时间：update_time';


/*==============================================================*/
/* Table: manage_user_role                                  */
/*==============================================================*/
CREATE TABLE IF NOT EXISTS cmiep.manage_user_role (
   rule_id              INT4                 not null,
   user_id              INT4                 not null,
   constraint PK_MANAGE_USER_ROLE primary key (rule_id, user_id)
);

comment on table cmiep.manage_user_role is
'管理用户-角色关联表：manage_user_role';

comment on column cmiep.manage_user_role.rule_id is
'角色ID：rule_id';

comment on column cmiep.manage_user_role.user_id is
'用户ID：user_id';


alter table cmiep.manage_role_resource
   add constraint FK_MANAGE_MRR_REF_RESOURCE foreign key (authority_id)
      references cmiep.manage_resource (authority_id)
      on delete cascade on update cascade;

alter table cmiep.manage_role_resource
   add constraint FK_MANAGE_MRR_REF_ROLE foreign key (rule_id)
      references cmiep.manage_role (rule_id)
      on delete cascade on update cascade;

alter table cmiep.manage_user_role
   add constraint FK_MANAGE_MUR_REF_ROLE foreign key (rule_id)
      references cmiep.manage_role (rule_id)
      on delete cascade on update cascade;

alter table cmiep.manage_user_role
   add constraint FK_MANAGE_MUR_REF_USER foreign key (user_id)
      references cmiep.manage_user (user_id)
      on delete cascade on update cascade;

INSERT INTO cmiep.manage_role (school_id, content, name, update_time) VALUES (NULL,'admin','超级管理员',now());

INSERT INTO cmiep.manage_user (user_code,update_time,user_group,is_admin,user_name,pass_word) VALUES ('admin', now(),'teacher_staff',TRUE,'超级管理员','$2a$10$M1NlxQ9.op.rWRKJjZvIzuszqKh6Rt4jIevsqkIlmQkXl2oK5vIRS');

INSERT INTO cmiep.manage_user_role VALUES ((select last_value from cmiep.manage_user_user_id_seq),1);