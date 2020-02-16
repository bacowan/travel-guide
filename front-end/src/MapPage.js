import React, { Component } from "react";
import OlMap from "ol/Map";
import OlView from "ol/View";
import OlTileLayer from "ol/layer/Tile";
import OlOsmSource from "ol/source/OSM";
import OlFeature from "ol/Feature";
import OlPoint from "ol/geom/Point";
import OlVectorSource from "ol/source/Vector";
import OlVectorLayer from "ol/layer/Vector";
import { Style, Fill, Stroke } from 'ol/style';
import { fromLonLat, toLonLat } from 'ol/proj';
import { RegularShape } from 'ol/style';
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
    let currentLocation = fromLonLat([lon, lat]);

    let youAreHere = new OlFeature({
      geometry: new OlPoint(currentLocation),
    });
    var vectorSource = new OlVectorSource({
      features: [youAreHere]
    });
    var markerVectorLayer = new OlVectorLayer({
      source: vectorSource,
      style: new Style({
        image: new RegularShape({
          fill: new Fill({color: 'red'}),
          stroke: new Stroke({color: 'black', width: 2}),
          points: 16,
          radius: 5,
          angle: Math.PI / 4
        })
      })
    });

    this.olmap = new OlMap({
      target: this.mapRef.current,
      layers: [
        new OlTileLayer({
          source: new OlOsmSource()
        }),
        markerVectorLayer
      ],
      view: new OlView({
        center: currentLocation,
        zoom: 15
      })
    });

    this.olmap.on('moveend', this.onMoveEnd.bind(this));
  }

  async onMoveEnd(event) {
    // TODO: This will spam the database way too hard since it calls the database
    // any time the map view changes at all
    let extent = this.olmap.getView().calculateExtent();
    let centerMeters = [(extent[0] + extent[2])/2, (extent[1] + extent[3])/2];
    let centerLonLat = toLonLat(centerMeters);
    let radius = Math.sqrt(Math.pow(extent[2] - centerMeters[0], 2) + Math.pow(extent[3] - centerMeters[1], 2)) / 1000;

    let self = this;
    if (navigator.geolocation) {
      let fetchurl = `http://localhost:8080/interest_points?lat=${centerLonLat[1]}&lon=${centerLonLat[0]}&distance=${radius}`;
      let response = await fetch(fetchurl, {mode: 'cors'});
      let asJson = await response.json();

      // TODO: filter out the points that have already been added

      for (let i = 0; i < asJson.length; i++) {
        let point = new OlFeature({
          geometry: new OlPoint(fromLonLat([asJson[i].lon, asJson[i].lat])),
        });
        var vectorSource = new OlVectorSource({
          features: [point]
        });
        var markerVectorLayer = new OlVectorLayer({
          source: vectorSource,
          style: new Style({
            image: new RegularShape({
              fill: new Fill({color: 'red'}),
              stroke: new Stroke({color: 'black', width: 2}),
              points: 16,
              radius: 5,
              angle: Math.PI / 4
            })
          })
        });
        self.olmap.addLayer(markerVectorLayer);
      }
    } else {
      // TODO: Error handling
    }
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