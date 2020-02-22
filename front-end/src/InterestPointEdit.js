import React, { Component } from "react";
import OlMap from "ol/Map";
import OlView from "ol/View";
import OlTileLayer from "ol/layer/Tile";
import OlOsmSource from "ol/source/OSM";
import { MapPin, Plus } from 'react-feather';
import InterestPointEditPageTag from './InterestPointEditPageTag';
import './map.css';

class InterestPointEdit extends Component {
    constructor(props) {
        super(props);
        this.mapRef = React.createRef();
        this.handleInterestPointNameChange = this.handleInterestPointNameChange.bind(this);
        this.state = {
            header: "New Interest Point",
            interestPointName: "",
            tags: [<InterestPointEditPageTag/>]
        }
    }

    componentDidMount() {
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
    }

    handleInterestPointNameChange(event) {
        this.setState({interestPointName: event.target.value});
    }

    render() {
        return (
            <form className="row" onSubmit={this.handleSubmit}>
                <h2 className="col-s-12">{this.state.header}</h2>
                <label className="col-2">Interest Point Name:</label>
                <div className="col-10">
                    <input
                        value={this.state.interestPointName}
                        onChange={this.handleInterestPointNameChange}
                        required/>
                </div>
                <label className="col-2">Location:</label>
                <div className="col-10">
                    <div className="map-form-container">
                        <div ref={this.mapRef} className="map"/>
                        <MapPin size={16} color="red"/>
                    </div>
                </div>
                <label className="col-2">Tags:</label>
                <div className="col-10">
                    {this.state.tags}
                    <button><Plus/></button>
                </div>
                <div className="col-2">
                    <input type="submit" value="Submit"/>
                </div>
            </form>
        )
    }
}

export default InterestPointEdit;