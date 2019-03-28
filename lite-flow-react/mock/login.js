var Mock = require('mockjs');
var qs = require('qs');

let codesData;
codesData = Mock.mock({
    data: [
        {
            "key": "lite-menu-01",
            "name": "Dashboard",
            "url": "/console/dashboard",
            "icon": "laptop",
            "order": 1,
            "children": [

            ]
        },
        {
            "key": "lite-menu-02",
            "name": "任务",
            "url": "",
            "icon": "idcard",
            "order": 2,
            "children": [
                {
                    "key": "lite-menu-02-01",
                    "name": "任务流",
                    "url": "/console/flow",
                    "icon": null,
                    "order": 1,
                    "children": [

                    ]
                },
                {
                    "key": "lite-menu-02-02",
                    "name": "任务",
                    "url": "/console/task",
                    "icon": null,
                    "order": 1,
                    "children": [

                    ]
                },
                {
                    "key": "lite-menu-02-03",
                    "name": "任务版本",
                    "url": "/console/version",
                    "icon": null,
                    "order": 1,
                    "children": [

                    ]
                },
                {
                    "key": "lite-menu-02-04",
                    "name": "任务初始化",
                    "url": "/console/dailyInit",
                    "icon": null,
                    "order": 1,
                    "children": [

                    ]
                }
            ]
        },
        {
            "key": "lite-menu-03",
            "name": "执行引擎",
            "url": null,
            "icon": "deployment-unit",
            "order": 2,
            "children": [
                {
                    "key": "lite-menu-03-01",
                    "name": "执行者",
                    "url": "/executor/server",
                    "icon": null,
                    "order": 2,
                    "children": [

                    ]
                },
                {
                    "key": "lite-menu-03-02",
                    "name": "执行容器",
                    "url": "/executor/container",
                    "icon": null,
                    "order": 1,
                    "children": [

                    ]
                },
                {
                    "key": "lite-menu-03-03",
                    "name": "执行插件",
                    "url": "/executor/plugin",
                    "icon": null,
                    "order": 2,
                    "children": [

                    ]
                },
                {
                    "key": "lite-menu-03-04",
                    "name": "执行任务",
                    "url": "/executor/exeJob",
                    "icon": null,
                    "order": 2,
                    "children": [

                    ]
                }
            ]
        },
        {
            "key": "lite-menu-04",
            "name": "系统管理",
            "url": null,
            "icon": "setting",
            "order": 2,
            "children": [
                {
                    "key": "lite-menu-04-01",
                    "name": "用户管理",
                    "url": "/console/system/user",
                    "icon": null,
                    "order": 1,
                    "children": [

                    ]
                },
                {
                    "key": "lite-menu-04-02",
                    "name": "用户组管理",
                    "url": "/console/system/ugroup",
                    "icon": null,
                    "order": 1,
                    "children": [

                    ]
                },
                {
                    "key": "lite-menu-04-03",
                    "name": "角色管理",
                    "url": "/console/system/role",
                    "icon": null,
                    "order": 2,
                    "children": [

                    ]
                },
                {
                    "key": "lite-menu-04-04",
                    "name": "操作日志",
                    "url": "/console/system/role",
                    "icon": null,
                    "order": 2,
                    "children": [

                    ]
                }
            ]
        }
    ]
});


module.exports = {
    'POST /console/login': function (req, res) {
        let result = 0;
        const response = {
            status: result,
            data: {
                name: 'demo',
                userId: 1,
                menus: codesData.data
            },
        };
        res.json(response)
    },
    'GET /console/userInfo': {
        status: 0,
        data: {
            name: 'demo',
            userId: 1,
            menus: codesData.data
        }
    },
    'GET /console/logout': {status: 0, data: '退出成功'}
};
