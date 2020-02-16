import React, { Component } from "react";

class SignUp extends Component {
    render() {
        return (
            <form className="row">
                <h2 className="col-s-12">Sign Up</h2>
                <label className="col-2">Email address:</label>
                <div className="col-10"><input type="email"/></div>
                <label className="col-2">Password:</label>
                <div className="col-10"><input type="password"/></div>
                <label className="col-2">Retype Password:</label>
                <div className="col-10"><input type="password"/></div>
                <div className="col-2"><input type="submit" value="Sign Up"/></div>
            </form>
        )
    }
}

export default SignUp;