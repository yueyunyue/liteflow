export default {
    hash: true,
    entry: 'src/index.tsx',
    disableCSSModules: false,
    theme: './src/theme.js',
    html: {
        "template": "./src/index.ejs"
    },
    autoprefixer: {
        browsers: [
            'last 7 versions',
            'Android >= 4.2',
            'iOS >= 6'
        ]
    },
    extraBabelPlugins: [
        'transform-runtime',
        ['import', {
            libraryName: 'antd',
            style: true
        }]
    ],
    env: {
        production: {
            multipage: true,
            publicPath: '/static/dist/',
            outputPath: '../lite-flow-console/lite-flow-console-web/src/main/resources/static/dist',
        },
        development: {
            multipage: false,
            publicPath: '/',
            extraBabelPlugins: [
                'dva-hmr'
            ]
        }
    },
    // proxy: {
    //     '/': {
    //         'target': 'http://localhost:8080',
    //         'changeOrigin': true,
    //         'pathRewrite': {'^/': ''}
    //     }
    // }
};
