import React, { Component } from "react";
import OlMap from "ol/Map";
import OlView from "ol/View";
import OlLayerTile from "ol/layer/Tile";
import OlSourceOSM from "ol/source/OSM";
import { fromLonLat } from 'ol/proj';
import './map.css'

class MapPage extends Component {
  constructor(props) {
    super(props);
    this.mapRef = React.createRef();
  }

  componentDidMount() {
    let self = this;
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(async function(position) {
        self.createCenteredMap(position.coords.latitude, position.coords.longitude);
      });
    } else {
      self.createCenteredMap(0, 0);
    }
  }

  createCenteredMap(lat, lon) {
    this.olmap = new OlMap({
      target: this.mapRef.current,
      layers: [
        new OlLayerTile({
          source: new OlSourceOSM()
        })
      ],
      view: new OlView({
        center: fromLonLat([lon, lat]),
        zoom: 15
      })
    });
  }

  render() {
    return (
      <>
      <div ref={this.mapRef} className="map"/>
      </>
    );
  }
}

export default MapPage;