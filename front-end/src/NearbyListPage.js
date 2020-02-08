import React, { Component } from "react";
import { NavLink } from "react-router-dom";
import './index.css';
import './responsive.css';
import Navbar from "./Navbar";
import NearbyItem from "./NearbyItem";

class NearbyListPage extends Component {
  constructor(props) {
    super(props);
    this.state = {
      nearbyItems: []
    }
  }

  componentDidMount() {
    let self = this;
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(async function(position) {
        let fetchurl = `http://localhost:8080/interest_points?lat=${position.coords.latitude}&lon=${position.coords.longitude}&distance=10`;
        console.log(fetchurl)
        let response = await fetch(fetchurl, {mode: 'cors'});
        let asJson = await response.json();
        self.setState({
          nearbyItems: asJson.map((point, index) => <NearbyItem key={index} title={point.name}/>)
        });
      });
    } else {
      // TODO: Error handling
    }
  }

  componentWillUnmount() {

  }

  render() {
    return (
      <>
      <Navbar/>
      <h1>Nearby Interest Points</h1>
      {this.state.nearbyItems}
      </>
    );
  }
}

export default NearbyListPage;