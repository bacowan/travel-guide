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
import { defaults as defaultControls, Control } from 'ol/control';
import './map.css';
import feather from 'feather-icons';
import { MapPin } from 'react-feather';
import { Redirect } from 'react-router';

class MapPage extends Component {
  constructor(props) {
    super(props);
    this.editButtonClick = this.editButtonClick.bind(this);
    this.cancelEditButtonClick = this.cancelEditButtonClick.bind(this);
    this.mapRef = React.createRef();
    this.state = {
      isEditing: false,
      newPointLat: null,
      newPointLon: null
    };

    this.editButton = document.createElement('button');
    this.editButton.onclick = this.editButtonClick;
    this.editButton.innerHTML = feather.icons['edit-3'].toSvg();
    let editDiv = document.createElement('div');
    editDiv.appendChild(this.editButton);
    editDiv.className = 'ol-unselectable ol-control map-button-edit';
    this.editControl = new Control({ element: editDiv });

    this.cancelEditButton = document.createElement('button');
    this.cancelEditButton.onclick = this.cancelEditButtonClick;
    this.cancelEditButton.innerHTML = feather.icons['x'].toSvg();
    let cancelEditDiv = document.createElement('div');
    cancelEditDiv.appendChild(this.cancelEditButton);
    cancelEditDiv.className = 'ol-unselectable ol-control map-button-cancel-edit';
    this.cancelEditControl = new Control({ element: cancelEditDiv });
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
      controls: defaultControls().extend([this.editControl]),
      view: new OlView({
        center: currentLocation,
        zoom: 15
      })
    });

    this.olmap.on('moveend', this.onMoveEnd.bind(this));
  }

  editButtonClick() {
    if (!this.state.isEditing) {
      this.olmap.addControl(this.cancelEditControl);
      this.editButton.innerHTML = feather.icons['check'].toSvg();
      this.setState({
        isEditing: true
      });
    }
    else {
      let coords = this.olmap.getView().getCenter();
      this.setState({
        newPointLat: coords[1],
        newPointLon: coords[0]
      });
    }
  }

  cancelEditButtonClick() {
    this.editButton.innerHTML = feather.icons['edit-3'].toSvg();
    this.olmap.removeControl(this.cancelEditControl);
    this.setState({
      isEditing: false
    });
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
    if (this.state.newPointLat !== null && this.state.newPointLon !== null) {
      return <Redirect to='/InterestPointEdit'/>;
    }
    else {
      return (
        <>
        <div ref={this.mapRef} className="map"/>
        {this.state.isEditing && <MapPin size={24} color="red"/>}
        </>
      );
    }
  }
}

export default MapPage;