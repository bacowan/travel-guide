import React, { Component } from "react";
import { Edit3, Shield } from 'react-feather';
import EditTagModal from './EditTagModal';

class InterestPointEditPageTag extends Component {
    constructor(props) {
        super(props);
        this.setModal = this.setModal.bind(this);
    }

    setModal() {
        this.props.setModal(
            <EditTagModal/>
        )
    }

    render() {
        return (
            <div className="interest-point-tag-box">
                <div className="row">
                    <p className="col-s-4">Name:</p>
                    <p className="col-s-8">{this.props.name}</p>
                </div>
                <div className="row">
                    <p className="col-s-4">Description:</p>
                    <p className="col-s-8">{this.props.description}</p>
                </div>
                <div className="row">
                    <div className="interest-point-button-container">
                        <button type="button" onClick={this.setModal}><Edit3/></button>
                        <button type="button">
                            <svg xmlns="http://www.w3.org/2000/svg" viewbox="0 0 24 24" width="24" height="24"
                                stroke-width="1" stroke-linejoin="round" stroke-linecap="round" stroke="currentColor">
                                <text y="20" font-size="20">あ</text>
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