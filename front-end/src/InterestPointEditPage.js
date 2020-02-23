import React, { Component } from "react";
import OlMap from "ol/Map";
import OlView from "ol/View";
import OlTileLayer from "ol/layer/Tile";
import OlOsmSource from "ol/source/OSM";
import { MapPin, Plus } from 'react-feather';
import InterestPointEditPageTag from './InterestPointEditPageTag';
import './map.css';
import './modal.css';

class InterestPointEditPage extends Component {
    constructor(props) {
        super(props);
        this.mapRef = React.createRef();
        this.handleInterestPointNameChange = this.handleInterestPointNameChange.bind(this);
        this.setModal = this.setModal.bind(this);
        this.modalClick = this.modalClick.bind(this);
        this.modalClose = this.modalClose.bind(this);
        this.state = {
            header: "New Interest Point",
            interestPointName: "",
            tags: ["1"],
            modal: null,
            availableTagNames: null
        }
    }

    async componentDidMount() {
        this.olmap = new OlMap({
          target: this.mapRef.current,
          layers: [
            new OlTileLayer({
              source: new OlOsmSource()
            })
          ],
          view: new OlView({
            center: [this.props.location.state.lon, this.props.location.state.lat],
            zoom: 18
          })
        });

        let tagUrl = `http://localhost:8080/tags`;
        let response = await fetch(tagUrl, {mode: 'cors'});
        let asJson = await response.json();
        this.setState({
            availableTagNames: asJson.map(item => item.english)
        });
    }

    handleSubmit(event) {
        event.preventDefault();
        return false;
    }

    handleInterestPointNameChange(event) {
        this.setState({interestPointName: event.target.value});
    }

    setModal(modal) {
        this.setState({
            modal: modal
        });
    }

    modalClick(event) {
        if (event.target.className != null && event.target.className.toString().includes("modal-displayed")) {
            this.modalClose();
        }
    }

    modalClose() {
        this.setState({
            modal: null
        });
    }

    render() {
        return (
            <>
            <form onSubmit={this.handleSubmit}>
                <div className="row">
                    <h2 className="col-s-12">{this.state.header}</h2>
                </div>
                <div className="row">
                    <label className="col-2">Interest Point Name:</label>
                    <div className="col-10">
                        <input
                            value={this.state.interestPointName}
                            onChange={this.handleInterestPointNameChange}
                            required/>
                    </div>
                </div>
                <div className="row">
                    <label className="col-2">Location:</label>
                    <div className="col-10">
                        <div className="map-form-container">
                            <div ref={this.mapRef} className="map"/>
                            <MapPin size={16} color="red"/>
                        </div>
                    </div>
                </div>
                <div className="row">
                    <label className="col-2">Tags:</label>
                    <div className="col-10">
                        {this.state.tags.map(tag => <InterestPointEditPageTag setModal={this.setModal} modalClose={this.modalClose} availableTagNames={this.state.availableTagNames} key={tag}/>)}
                    </div>
                </div>
                <div className="row">
                    <button type="button" className="col-1"><Plus/></button>
                </div>
                <div className="row">
                    <input className="col-4" type="submit" value="Submit"/>
                </div>
            </form>
            <div className={`modal ${this.state.modal !== null && 'modal-displayed'}`} onClick={this.modalClick}>
                <div className="modal-content">
                    {this.state.modal}
                </div>
            </div>
            </>
        )
    }
}

export default InterestPointEditPage;