var Mock = require('mockjs');
var qs = require('qs');

let flowsData = Mock.mock({
    'data|20': [
        {
            'id|+1': 1,
            name: '@cname',
            description: '@cname',
            status: 1,
            user: {
                id: 1,
                userName: "lite"
            },
            createTime: 1540126236000,
            updateTime: 1540126236000
        }
    ]
});
module.exports = {
    'GET /console/flow/list': function (req, res) {
        const page = qs.parse(req.query);

        let pageSize = page.pageSize;
        let pageNum = page.pageNum;
        if (!pageSize) {
            pageSize = 10;
        }
        if (!pageNum) {
            pageNum = 1;
        }
        let data = flowsData.data;
        let result = data.length > pageSize ? data.slice((pageNum - 1) * pageSize, pageNum * pageSize) : data.slice();
        res.json({
            status: 0,
            data: result,
            total: data.length
        })
    },
    'POST /console/flow/addOrUpdate': {
        status: 0,
        data: 101
    },
    'POST /console/flow/editLinks': {
        status: 0,
        data: 101
    },
    'POST /console/flow/delete': {
        status: 0
    }
};
