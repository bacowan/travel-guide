import React, { Component } from "react";
import { Check, X } from 'react-feather';

class EditTagModal extends Component {
    constructor(props) {
        super(props);
        this.handleNameChange = this.handleNameChange.bind(this);
        this.handleDescriptionChange = this.handleDescriptionChange.bind(this);
        this.collapseAutoComplete = this.collapseAutoComplete.bind(this);
        this.expandAutoComplete = this.expandAutoComplete.bind(this);
        this.confirm = this.confirm.bind(this);
        this.state = {
            name: "",
            description: "",
            autoCompleteOptions: []
        };
    }

    handleDescriptionChange(event) {
        this.setState({
            description: event.target.value
        });
    }

    handleNameChange(event) {
        this.handleNameChangeText(event.target.value);
    }

    expandAutoComplete() {
        this.handleNameChangeText(this.state.name);
    }

    confirm() {
        this.props.confirm(this.state.name, this.state.description);
        this.props.cancel();
    }

    handleNameChangeText(text) {
        let autoCompleteOptions = this.props.availableTagNames
            .filter(t => t.toLowerCase().includes(text.toLowerCase()))
            .sort();
        this.setState({
            name: text,
            autoCompleteOptions: autoCompleteOptions
        });
    }

    collapseAutoComplete() {
        this.setState({
            autoCompleteOptions: []
        });
    }

    clickAutoCompleteName(name) {
        this.setState({
            name: name,
            autoCompleteOptions: []
        });
    }

    render() {
        let autoCompleteOptions = this.state.autoCompleteOptions.map(s =>
            <div className="auto-complete-item" onClick={this.clickAutoCompleteName.bind(this, s)} key={s}>{s}</div>
        );

        let autoCompleteDiv = <div className="auto-complete">{autoCompleteOptions}</div>

        return (
            <form autoComplete="off">
                <div className="row">
                    <label className="col-3">Tag Name:</label>
                    <div className="col-9">
                        <input type="text" value={this.state.name} onChange={this.handleNameChange}
                            onFocus={this.expandAutoComplete}/>
                        {autoCompleteOptions.length > 0 && autoCompleteDiv}
                    </div>
                </div>
                <div className="row">
                    <label className="col-3">Description:</label>
                    <textarea className="col-9" rows="7" onFocus={this.collapseAutoComplete}
                        onChange={this.handleDescriptionChange} value={this.state.description}/>
                </div>
                <div className="row">
                    <div className="modular-button-container">
                        <button type="button" onClick={this.confirm}><Check/></button>
                        <button type="button" onClick={this.props.cancel}><X/></button>
                    </div>
                </div>
            </form>
        )
    }
}


export default EditTagModal;