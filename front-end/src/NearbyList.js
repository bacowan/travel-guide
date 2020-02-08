import React, { Component } from "react";
import { NavLink } from "react-router-dom";
import './index.css';
import './responsive.css';
import Navbar from "./Navbar";
import NearbyItem from "./NearbyItem";

class NearbyList extends Component {
  render() {
    return (
      <>
      <Navbar/>
      <h1>Nearby Interest Points</h1>
      <NearbyItem title="Title" distance="99km" tags="test tags, test ttttttags"/>
      <NearbyItem title="Title" distance="99km" tags="test tags, test ttttttags"/>
      </>
    );
  }
}

export default NearbyList;