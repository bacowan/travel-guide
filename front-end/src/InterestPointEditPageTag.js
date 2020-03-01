import React, { Component } from "react";
import { Edit3, Shield } from 'react-feather';
import EditTagModal from './EditTagModal';

class InterestPointEditPageTag extends Component {
    constructor(props) {
        super(props);
        this.setModal = this.setModal.bind(this);
        this.confirm = this.confirm.bind(this);
    }

    setModal() {
        this.props.setModal(
            <EditTagModal cancel={this.props.modalClose} confirm={this.confirm} availableTagNames={this.props.availableTagNames}/>
        )
    }

    confirm(tag, description) {
        this.props.setData(this.props.tag, tag, description);
    }

    render() {
        return (
            <div className="interest-point-tag-box">
                <div className="row">
                    <p className="col-s-4 text-label">Name:</p>
                    <p className="col-s-8">{this.props.tag}</p>
                </div>
                <div className="row">
                    <p className="col-s-4 text-label">Description:</p>
                    <p className="col-s-8">{this.props.description}</p>
                </div>
                <div className="row">
                    <div className="modular-button-container">
                        <button type="button" onClick={this.setModal} disabled={this.props.availableTagNames === null}><Edit3/></button>
                        <button type="button">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24"
                                strokeWidth="1" strokeLinejoin="round" strokeLinecap="round" stroke="currentColor">
                                <text y="20" fontSize="20">あ</text>
                            </svg>
                        </button>
                        <button type="button"><Shield/></button>
                    </div>
                </div>
            </div>
        );
    }
}

export default InterestPointEditPageTag;