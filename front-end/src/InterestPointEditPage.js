import React, { Component } from "react";
import OlMap from "ol/Map";
import OlView from "ol/View";
import OlTileLayer from "ol/layer/Tile";
import OlOsmSource from "ol/source/OSM";
import { toLonLat } from 'ol/proj';
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
        this.setData = this.setData.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.state = {
            header: "New Interest Point",
            interestPointName: "",
            modal: null,
            availableTagNames: null,
            tags: [
                {
                    tag: "",
                    description: ""
                }
            ]
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

        const self = this;
        let coords = toLonLat(this.olmap.getView().getCenter());
        const request = new XMLHttpRequest();
        let posturl = `http://localhost:8080/interest_points`;
        let body = JSON.stringify({
            lat: coords[1],
            lon: coords[0],
            name: this.state.interestPointName,
            subname: ""
        });
        request.onreadystatechange = () => {
            self.submitLocationCallback(request);
        }
        request.open("POST", posturl);
        request.setRequestHeader("Authorization", this.props.bearer);
        request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        request.send(body);

        return false;
    }

    submitLocationCallback(response) {
        if (response.readyState === XMLHttpRequest.DONE) {
            if (response.status === 201) {
                console.log("succeeded submitting location");
                const locationId = response.response;
                const posturl=`http://localhost:8080/interest_points/${locationId}/descriptions`;
    
                const request = new XMLHttpRequest();
                const body = JSON.stringify(this.state.tags.map(t => { return {
                    language: "English",
                    tag: t.tag,
                    text: t.description,
                    approved: false
                }}));
                request.onreadystatechange = () => {
                    this.submitTagsCallback(request);
                }
                request.open("PUT", posturl);
                request.setRequestHeader("Authorization", this.props.bearer);
                request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
                request.send(body);
            }
            else {
                console.log("failed to submit location");
                // TODO: error handling
            }
        }
    }

    submitTagsCallback(response) {
        if (response.readyState == XMLHttpRequest.DONE) {
            if (response.status === 200) {
                console.log("succeeded submitting descriptions");
            }
            else {
                console.log("failed to submit descriptions");
            }
        }
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

    setData(oldTagName, tagName, description) {
        const indexToReplace = this.state.tags.map(t => t.tag).indexOf(oldTagName);
        if (indexToReplace >= 0) {
            const newArray = this.state.tags;
            newArray[indexToReplace] = {
                tag: tagName,
                description: description
            }
            this.setState({
                tags: newArray
            });
        }
    }

    render() {
        const tags = this.state.tags.map(t =>
            <InterestPointEditPageTag
                setModal={this.setModal}
                modalClose={this.modalClose}
                availableTagNames={this.state.availableTagNames}
                description={t.description}
                tag={t.tag}
                setData={this.setData}
                key={t.tag}/>)

        return (
            <>
            <form onSubmit={this.handleSubmit} onSubmit={this.handleSubmit}>
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
                        {tags}
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