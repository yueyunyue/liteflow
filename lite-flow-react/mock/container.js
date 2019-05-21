var Mock = require('mockjs');
var qs = require('qs');

let containersData = Mock.mock({
    'data|20': [
        {
            'id|+1': 1,
            name: '@cname',
            description: '@cname',
            className: 'cn.lite.flow.executor.kernel.container.impl.NoopContainer',
            status: 1,
            logType: 1,
            user: {
                id: 1,
                name: "lite"
            },
            createTime: 1540126236000,
            updateTime: 1540126236000
        }
    ]
});
module.exports = {
    'GET /executor/container/list': function (req, res) {
        const page = qs.parse(req.query);

        let pageSize = page.pageSize;
        let pageNum = page.pageNum;
        if (!pageSize) {
            pageSize = 10;
        }
        if (!pageNum) {
            pageNum = 1;
        }
        let data = containersData.data;
        let result = data.length > pageSize ? data.slice((pageNum - 1) * pageSize, pageNum * pageSize) : data.slice();
        res.json({
            status: 0,
            data: result,
            total: data.length
        })
    },
    'GET /executor/common/getAllValidContainer':{
        status: 0,
        data: containersData.data
    },
    'POST /executor/container/addOrUpdate': {
        status: 0,
        data: 101
    },
    'POST /executor/container/on': {
        status: 0
    },
    'POST /executor/container/off': {
        status: 0
    }
};
