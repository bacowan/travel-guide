import React, { Component } from "react";
import { Link } from "react-router-dom";

class OptionsPage extends Component {
    render() {
        const permissions = [];
        if (permissions.length === 0) {
            permissions.push("No special permissions");
        }

        return (
            <div className="row">
                <h2 className="col-s-12">Options</h2>
                <div className="col-12">
                    <label>Language:</label>
                    <select name="language" onChange={this.handleLanguageChange}>
                        <option name="English">English</option>
                        <option name="日本語">日本語</option>
                    </select>
                </div>
                <div className="col-12">
                    <label>Permissions:</label>
                    <Link to="/RequestPermissions">
                        <button type="button">Request Permissions</button>
                    </Link>
                </div>
                <ul className="col-12">
                    {permissions.map(p => <li>{p}</li>)}
                </ul>
            </div>
        );
    }
}

export default OptionsPage;