import React, { Component } from "react";
import { Link } from "react-router-dom";

function OptionsPage(props) {
    const permissions = [];
    if (permissions.length === 0) {
        permissions.push("No special permissions");
        var permissionParameters = "";
    }
    else {
        var permissionParameters = "?" + permissions.map(p => p + "=true").join("&");
    }

    function handleLanguageChange(){}

    return (
        <div className="row options-list">
            <h2 className="col-s-12">Options</h2>
            <div className="col-12">
                <label>Language:</label>
                <select name="language" onChange={handleLanguageChange}>
                    <option name="English">English</option>
                    <option name="日本語">日本語</option>
                </select>
            </div>
            <div className="col-12">
                <label>Permissions:</label>
                <Link to={"/RequestPermissions" + permissionParameters}>
                    <button type="button">Request Permissions</button>
                </Link>
            </div>
            <ul className="col-12">
                {permissions.map(p => <li key={p}>{p}</li>)}
            </ul>
        </div>
    );
}

export default OptionsPage;