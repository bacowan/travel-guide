import React, { Component } from "react";

class SignUp extends Component {
    constructor(props) {
        super(props);
        this.state = {
            email: "",
            language: "English",
            password: "",
            retypePassword: ""
        };
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleEmailChange = this.handleEmailChange.bind(this);
        this.handleLanguageChange = this.handleLanguageChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
        this.handleRetypePasswordChange = this.handleRetypePasswordChange.bind(this);
        this.emailRef = React.createRef();
        this.passwordRef = React.createRef();
        this.retypePasswordRef = React.createRef();
    }

    handleSubmit(event) {
        event.preventDefault();
        const request = new XMLHttpRequest();
        const url='http://localhost:8080/users';
        const self = this;
        request.onreadystatechange = () => {
            self.submitCallback(request);
        }
        request.open("POST", url);
        request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        request.send(JSON.stringify({
            email: this.state.email,
            defaultLanguage: this.state.language,
            password: this.state.password
        }));
        return false;
    }

    submitCallback(response) {
        if (response.readyState == XMLHttpRequest.DONE) {
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
        }
    }

    handleEmailChange(event) {
        this.setState({email: event.target.value});
    }

    handleLanguageChange(event) {
        this.setState({language: event.target.value});
    }

    handlePasswordChange(event) {
        this.setState({password: event.target.value}, this.validatePassword);
    }

    handleRetypePasswordChange(event) {
        this.setState({retypePassword: event.target.value}, this.validatePassword);
    }

    validatePassword() {
        if (this.state.password != this.state.retypePassword) {
            this.retypePasswordRef.current.setCustomValidity("Passwords do not match");
        }
        else {
            this.retypePasswordRef.current.setCustomValidity("");
        }
    }

    render() {
        return (
            <>
            <h2>Sign Up</h2>
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
                    <label className="col-2">Language:</label>
                    <select className="col-6" name="language" onChange={this.handleLanguageChange}>
                        <option name="English">English</option>
                        <option name="日本語">日本語</option>
                    </select>
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
                    <label className="col-2">Retype Password:</label>
                    <input
                        className="col-6"
                        type="password"
                        value={this.state.retypePassword}
                        onChange={this.handleRetypePasswordChange}
                        ref={this.retypePasswordRef}
                        required/>
                </div>
                <div className="row">
                    <input className="col-2" type="submit" value="Sign Up"/>
                </div>
            </form>
            </>
        )
    }
}

export default SignUp;