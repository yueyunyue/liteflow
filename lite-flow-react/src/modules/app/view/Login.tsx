import React, {Component} from "react";
import LoginForm from "./login/LoginForm";
import {withRouter} from 'react-router';
import {SysUserModel} from "../model/SysUserModel";
import {inject} from "../../../common/utils/IOC";
import Particles from 'react-particles-js';

interface RouterI{
    router:any
}

class Login extends Component<RouterI, any> {

    @inject(SysUserModel)
    private sysUserModel: SysUserModel;

    render(){
        let that = this;
        const height = 5000;

        let loginProps = {
            loading:this.sysUserModel.loading,
            onOk (username:string,password:string) {
                that.sysUserModel.login(username,password,that.props.router);
            }
        };

        return <div style={{
            background: "#019BF0",
            position: "relative",
            width: "100%",
            height: height + "px"
        }}>
            <Particles
                width="100%"
                height="100%"
                params={{
                    particles: {
                        number: {
                            value: 60,
                            density: {
                                enable: true,
                                value_area: 500
                            }
                        },
                        size: {
                            value: 5,
                            random: true,
                            anim: {
                                enable: false,
                                speed: 40,
                                size_min: 0.1,
                                sync: false
                            }
                        },
                        color: {
                            value: "#FFF"
                        },
                        line_linked: {
                            enable: true,
                            color: "#FFF",
                            opacity: 1
                        }
                    },
                    interactivity: {
                        events: {
                            onhover: {
                                enable: true,
                                mode: "grab"
                            }
                        }
                    }
                }}
            />
            <div>
                <LoginForm {...loginProps} />
            </div>
        </div>;
    }
}

export default withRouter(Login);