import React, { Component } from "react";

class Login extends Component {

    constructor(props) {
        super(props);
        this.state = {
            email: "",
            password: ""
        };
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleEmailChange = this.handleEmailChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
        this.emailRef = React.createRef();
        this.passwordRef = React.createRef();
    }

    handleEmailChange(event) {
        this.setState({email: event.target.value});
    }

    handlePasswordChange(event) {
        this.setState({password: event.target.value});
    }

    handleSubmit(event) {
        event.preventDefault();
        const request = new XMLHttpRequest();
        const url=`http://localhost:8080/authenticate?user=${this.state.email}&password=${this.state.password}`;
        const self = this;
        request.onreadystatechange = () => {
            if (request.readyState === XMLHttpRequest.DONE && request.status === 200) {
                let expires = new Date();
                expires.setTime(expires.getTime() + (24 * 60 * 60 * 1000));
                // TODO: set cookie expiry that lines up with the bearer expiry date
                document.cookie = `bearer=${request.response};expires=${expires};path=/`;
                self.props.bearerChanged();
                self.props.history.goBack();
            }
        }
        request.open("POST", url);
        request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        request.send();
        return false;
    }

    render() {
        return (
            <>
            <h2>Login</h2>
            <form onSubmit={this.handleSubmit}>
                <div className="row">
                    <label className="col-2">Email address:</label>
                    <input
                        className="col-6"
                        type="email"
                        value={this.state.email}
                        onChange={this.handleEmailChange}
                        ref={this.emailRef} required/>
                </div>
                <div className="row">
                    <label className="col-2">Password:</label>
                    <input
                        className="col-6"
                        type="password"
                        value={this.state.password}
                        onChange={this.handlePasswordChange}
                        ref={this.passwordRef}
                        required/>
                </div>
                <div className="row">
                    <input className="col-2" type="submit" value="Login"/>
                </div>
            </form>
            </>
        )
    }
}

export default Login;