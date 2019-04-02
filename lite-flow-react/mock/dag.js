var Mock = require('mockjs');
var qs = require('qs');

module.exports = {
    'GET /console/flow/viewDag': {
        status: 0,
        data: {
            "links": [
                {
                    "id": null,
                    "taskId": 2,
                    "upstreamTaskId": 1,
                    "type": 0,
                    "status": 0
                },
                {
                    "id": null,
                    "taskId": 3,
                    "upstreamTaskId": 1,
                    "type": 0,
                    "status": 0
                },
                {
                    "id": null,
                    "taskId": 4,
                    "upstreamTaskId": 2,
                    "type": 0,
                    "status": 0
                },
                {
                    "id": null,
                    "taskId": 4,
                    "upstreamTaskId": 3,
                    "type": 1,
                    "config": -1,
                    "status": 0
                },
                {
                    "id": null,
                    "taskId": 4,
                    "upstreamTaskId": 5,
                    "type": 2,
                    "config": {
                        "startTime": "${yesterday}",
                        "endTime": "${today}",
                    },
                    "status": 0
                }
            ],
            "nodes": [
                {
                    "id": 1,
                    "name": "t1",
                    "period": 2,
                    "status": 1,
                },
                {
                    "id": 2,
                    "name": "t2",
                    "scheduleType": 0,
                    "period": 2,
                    "cronExpression": "45 4 * * ?",
                    "status": 1
                },
                {
                    "id": 3,
                    "name": "t3",
                    "scheduleType": 0,
                    "period": 2,
                    "cronExpression": "45 4 * * ?",
                    "status": 0
                },
                {
                    "id": 4,
                    "name": "t4",
                    "scheduleType": 0,
                    "period": 2,
                    "cronExpression": "45 4 * * ?",
                    "status": -1
                },
                {
                    "id": 5,
                    "name": "t5",
                    "scheduleType": 0,
                    "period": 2,
                    "cronExpression": "45 4 * * ?",
                    "status": 0
                }
            ]
        }
    }
};
