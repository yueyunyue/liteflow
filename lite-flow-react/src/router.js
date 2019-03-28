import React from 'react';
import App from "./modules/app/view/App";
import Login from "./modules/app/view/Login";
import {Router} from "react-router";
import {requestGetNoShowMsg} from "./common/utils/Request";
import appConfig from "./modules/app/config/AppConfig";

export default function ({history, sysUserModel}) {

    //验证用户是否已经登录
    async function userInfo(params) {
        return requestGetNoShowMsg(appConfig.urls.infoUrl, {});
    }
    //用来判断用户是否登录
    const isUserLogined = (nextState, replace, callback) => {
        userInfo().then(result => {
            if (result.status == 0 && result.data.name != 'null') {
                sysUserModel.loginSuccess(result.data)
            } else {
                replace('/login');
            }
            callback();
        }).catch(err => {
            callback(err);
        });
    };

    const routes = [{
        path: '/',
        component: App,
        onEnter: isUserLogined,
        indexRoute: {
            onEnter: function (nextState, replace) {
                replace('/console/dashboard')
            }
        },
        childRoutes: [
            {
                path: 'console/dashboard',
                getComponent(nextState, cb) {
                    require.ensure([], require => {
                        cb(null, require('./modules/dashboard/view/Dashboard'))
                    })
                }
            },
            {
                path: 'console/flow',
                getComponent(nextState, cb) {
                    require.ensure([], require => {
                        cb(null, require('./modules/flow/view/FlowView'))
                    })
                }
            },
            {
                path: 'console/task',
                getComponent(nextState, cb) {
                    require.ensure([], require => {
                        cb(null, require('./modules/task/view/TaskView'))
                    })
                }
            },
            {
                path: 'console/version',
                getComponent(nextState, cb) {
                    require.ensure([], require => {
                        cb(null, require('./modules/taskVersion/view/TaskVersionView'))
                    })
                }
            },
            {
                path: 'console/dailyInit',
                getComponent(nextState, cb) {
                    require.ensure([], require => {
                        cb(null, require('./modules/dailyInit/view/DailyInitView'))
                    })
                }
            },
            {
                path: 'executor/container',
                getComponent(nextState, cb) {
                    require.ensure([], require => {
                        cb(null, require('./modules/container/view/ContainerView'))
                    })
                }
            },
            {
                path: 'executor/server',
                getComponent(nextState, cb) {
                    require.ensure([], require => {
                        cb(null, require('./modules/executor/view/ExecutorView'))
                    })
                }
            },
            {
                path: 'executor/plugin',
                getComponent(nextState, cb) {
                    require.ensure([], require => {
                        cb(null, require('./modules/plugin/view/PluginView'))
                    })
                }
            },
            {
                path: 'executor/exeJob',
                getComponent(nextState, cb) {
                    require.ensure([], require => {
                        cb(null, require('./modules/executeJob/view/ExecuteJobView'))
                    })
                }
            },
            {
                path: 'console/system/user',
                getComponent(nextState, cb) {
                    require.ensure([], require => {
                        cb(null, require('./modules/user/view/UserView'))
                    })
                }
            },
            {
                path: 'console/system/ugroup',
                getComponent(nextState, cb) {
                    require.ensure([], require => {
                        cb(null, require('./modules/userGroup/view/UserGroupView'))
                    })
                }
            },
            {
                path: 'console/system/role',
                getComponent(nextState, cb) {
                    require.ensure([], require => {
                        cb(null, require('./modules/role/view/RoleView'))
                    })
                }
            }
        ]
    }, {
        path: '/login',
        component: Login
    }, {
        path: '*',
        name: 'error',
        getComponent(nextState, cb) {
            require.ensure([], require => {
                cb(null, require('./modules/app/view/error/Error'))
            })
        }
    }
    ];

    // const history = useRouterHistory(createHashHistory)({ queryKey: false });

    return <Router history={history} routes={routes}/>
}