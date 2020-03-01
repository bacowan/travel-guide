import React, { Component } from "react";

class RequestPermissions extends Component {
    render() {
        return (
            <form className="row">
                <input type="checkbox" id="Approver" value="Approver"/>
                <label for="Approver">Approver</label><br/>
            </form>
        );
    }
}

export default RequestPermissions;