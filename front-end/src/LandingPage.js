import React, { Component } from "react";
import { NavLink } from "react-router-dom";
import { Compass, Search } from 'react-feather';
import './index.css';
import './responsive.css';
import Navbar from "./Navbar";
 
class LandingPage extends Component {
  render() {
    return (
      <>
      <Navbar/>
      <div className="row center-content">
        <button className="col-s-3 button-list-item">
          <Compass size={128}/>
          <p>Map</p>
        </button>
        <button className="col-s-3 button-list-item">
          <Search size={128}/>
          <p>Nearby</p>
        </button>
      </div>
      </>
    );
  }
}

export default LandingPage;