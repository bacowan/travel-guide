import React, { Component, useState } from "react";
import { useParams } from 'react-router-dom';

function RequestPermissions(props) {

    const params = useParams();
    const approverParam = params.Approver === "true";
    const moderatorParam = params.Moderator === "true";

    const [approver, setApprover] = useState(approverParam);
    const [moderator, setModerator] = useState(moderatorParam);
    const [justification, setJustification] = useState("");

    function requestCallback(response) {
        if (response.readyState === XMLHttpRequest.DONE) {
            if (response.status === 200) {
            }
            else {
                console.log("failed to submit permission request");
                // TODO: error handling
            }
        }
    }

    function handleSubmit(event) {
        event.preventDefault();
        const puturl = `http://localhost:8080/permissions/${props.userId}`;
        const requestedPermissions = [];
        if (approver) requestedPermissions.push("Approver");
        if (moderator) requestedPermissions.push("Moderator");

        const body = JSON.stringify({
            permissions: requestedPermissions,
            justification: justification
        });
        
        const request = new XMLHttpRequest();
        request.onreadystatechange = () => {
            requestCallback(request);
        }
        request.open("PUT", puturl);
        request.setRequestHeader("Authorization", props.bearer);
        request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        request.send(body);

        return false;
    }

    return (
        <>
        <h2>Request Permissions</h2>
        <form onSubmit={handleSubmit}>
            <div className="row">
                <input type="checkbox" id="Approver"
                    checked={approver} onChange={e => setApprover(e.target.checked)}/>
                <label htmlFor="Approver">Approver</label>
            </div>
            <div className="row">
                <input type="checkbox" id="Moderator"
                    checked={moderator} onChange={e => setModerator(e.target.checked)}/>
                <label htmlFor="Moderator">Moderator</label>
            </div>
            <div className="row">
                <label className="col-s-12" htmlFor="Justification">Justification:</label>
                <textarea id="Justification" className="col-s-12" rows="6"
                    value={justification} onChange={e => setJustification(e.target.value)}/>
            </div>
            <div className="row">
                <input className="col-s-12" type="submit" disabled={approverParam === approver && moderatorParam === moderator}/>
            </div>
        </form>
        </>
    );
}

export default RequestPermissions;