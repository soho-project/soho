# 脚本中 t 数据结构

    {
        "name": "example",  //数据库表名
        "title": "自动化样例", //表显示名称
        "comment": "自动化样例", //表备注嘻嘻你
        "upperCamelCase": "Example",  //表名大驼峰式"
        “camelCase": "example", //小坨峰表名
        "frontHome": "list", //前端首页显示形式   list: 列表页，tree: 树形列表页
        ”option": {1:test, 2:test2}, //选项
        "tree": "id~title~parent_id", //树形结构定义
        "approval": "4~id",  //审批定义
        "roleActions": [
            "admin.details/list/create/update/delete/option",
            "user.details/list/create/update/delete/option",
            "open.details/list/create/update/delete/option"
        ],
        "apis": [
            {
                "name": "admin",  //对应的角色名称
                "targetName": "admin_id",  //对应角色用户ID字段名称
                "apis": [  //对应角色的API
                    "details",  //详情页
                    "list", //列表页
                    "create", //创建页面
                    "update", //修改页面
                    "delete", //删除
                    "option", // 选项接口
                    "exportExcel", //导出接口
                    "importExcel" //导入接口
                ]
            },
            ... //其他角色 例如: user, open
        ],
        "fields": [
            {
                "name": "title",
                "dataType": "varchar",
                "isPk": 0,
                "title": "标题",
                "isNotNull": 0,
                "isUnique": 0,
                "isAutoIncrement": 0,
                "isZeroFill": 0,
                "defaultValue": null,
                "length": 45,
                "scale": 0,
                "comment": "标题",
                "upperCamelCase": "Title",
                "camelCase": "title",
                "frontName": "Title",
                "frontType": "text",
                "frontMax": 45,
                "frontStep": 1,
                "isFilter": true,
                "isEnum": false,
                "isApproval": true,
                "editReadOnly": false,
                "options": [],
                "foreignTableName": "",
                "foreignTableUpperCamelCaseName": "",
                "foreignTableLowerCamelCaseName": "",
                "javaType": "String"
            }
        ]
    }