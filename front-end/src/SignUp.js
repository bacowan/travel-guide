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
                console.log(request.response)
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
            <form className="row" onSubmit={this.handleSubmit}>
                <h2 className="col-s-12">Sign Up</h2>
                <label className="col-2">Email address:</label>
                <div className="col-10">
                    <input
                        type="email"
                        value={this.state.email}
                        onChange={this.handleEmailChange}
                        ref={this.emailRef} required/>
                </div>
                <label className="col-2">Language:</label>
                <div className="col-10">
                    <select name="language" onChange={this.handleLanguageChange}>
                        <option name="English">English</option>
                        <option name="日本語">日本語</option>
                    </select>
                </div>
                <label className="col-2">Password:</label>
                <div className="col-10">
                    <input
                        type="password"
                        value={this.state.password}
                        onChange={this.handlePasswordChange}
                        ref={this.passwordRef}
                        required/>
                </div>
                <label className="col-2">Retype Password:</label>
                <div className="col-10">
                    <input
                        type="password"
                        value={this.state.retypePassword}
                        onChange={this.handleRetypePasswordChange}
                        ref={this.retypePasswordRef}
                        required/>
                </div>
                <div className="col-2">
                    <input type="submit" value="Sign Up"/>
                </div>
            </form>
        )
    }
}

export default SignUp;