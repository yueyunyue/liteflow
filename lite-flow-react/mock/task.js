var Mock = require('mockjs');
var qs = require('qs');

let tasksData = Mock.mock({
    'data|20': [
        {
            'id|+1': 1,
            name: '@cname',
            cronExpression: "0 1 * * ?",
            status: 1,
            period: 3,
            createTime: 1540126236000,
            updateTime: 1540126236000,
            description: '@cname',
            user: {
                    id:1,
                    name: "lite"
                }
        }
    ]
});
module.exports = {
    'GET /console/task/list': function (req, res) {
        const page = qs.parse(req.query);

        let pageSize = page.pageSize;
        let pageNum = page.pageNum;
        if (!pageSize) {
            pageSize = 10;
        }
        if (!pageNum) {
            pageNum = 1;
        }
        let data = tasksData.data;
        let result = data.length > pageSize? data.slice((pageNum - 1) * pageSize, pageNum * pageSize):data.slice();
        res.json({
            status: 0,
            data: result,
            total: data.length
        })
    },
    'GET /console/task/getAllAuthTask': {
        status:0,
        data: tasksData.data
    },
    'POST /console/task/add': {
        status: 0,
    },
    'POST /console/task/delete': {
        status: 0
    },
    'POST /console/task/run': {
        status: 0
    },

    'POST /console/task/edit': {
        status: 0
    },
    'GET /console/task/getRelation': {
        status: 0,
        data: {
            "upstream": [
                {
                    "id": 1,
                    "name": "t1",
                    "scheduleType": 0,
                    "period": 2,
                    "maxRunningtime": -1,
                    "startOvertime": 0,
                    "queueName": null,
                    "status": 1,
                },
                {
                    "id": 2,
                    "name": "t2",
                    "scheduleType": 0,
                    "period": 2,
                    "cronExpression": "45 4 * * ?",
                    "status": 2
                },
                {
                    "id": 3,
                    "name": "t3",
                    "scheduleType": 0,
                    "period": 2,
                    "cronExpression": "45 4 * * ?",
                    "status": 2,
                    "createdTime": 1510282709000,
                    "modifiedTime": 1510361752000
                },
                {
                    "id": 4,
                    "name": "t4",
                    "scheduleType": 0,
                    "period": 2,
                    "cronExpression": "45 4 * * ?",
                    "status": 2,
                    "createTime": 1510282709000,
                    "updateTime": 1510361752000
                },
                {
                    "id": 5,
                    "name": "t5",
                    "scheduleType": 0,
                    "period": 2,
                    "cronExpression": "45 4 * * ?",
                    "status": 2
                }
            ]
        }
    },
    'GET /console/task/getRelatedFlow': {
        status: 0,
        data: [{
            "id": 1,
            "name": "1",
            "status": 1
        }]
    }
};
