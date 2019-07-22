var Mock = require('mockjs');
var qs = require('qs');

let mockData = Mock.mock({
    'data|10': [
        {
            'value|+1': 1,
            key: '@cname'
        }
    ]
});

module.exports = {
    'GET /test/remote/option': {
        status: 0,
        data: mockData.data
    }
};
