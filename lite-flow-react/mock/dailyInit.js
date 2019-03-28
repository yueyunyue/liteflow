var Mock = require('mockjs');
var qs = require('qs');

let exeJobsData = Mock.mock({
    'data|20': [
        {
            'id|+1': 1,
            'taskId|+10': 1,
            taskName: '@cname',
            status: 1,
            day: 20190328,
            msg: "",
            createTime: 1540126236000,
            updateTime: 1540126236000
        }
    ]
});
module.exports = {
    'GET /console/dailyInit/list': function (req, res) {
        const page = qs.parse(req.query);

        let pageSize = page.pageSize;
        let pageNum = page.pageNum;
        if (!pageSize) {
            pageSize = 10;
        }
        if (!pageNum) {
            pageNum = 1;
        }
        let data = exeJobsData.data;
        let result = data.length > pageSize ? data.slice((pageNum - 1) * pageSize, pageNum * pageSize) : data.slice();
        res.json({
            status: 0,
            data: result,
            total: data.length
        })
    }
};
