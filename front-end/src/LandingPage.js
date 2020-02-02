import React, { Component } from "react";
import { NavLink } from "react-router-dom";
import { Compass, Search } from 'react-feather';
import './index.css'
import './responsive.css'
 
class LandingPage extends Component {
  render() {
    return (
      <>
      <ul className="navbar">
        <li><a>Login</a></li>
        <li><a>Sign Up</a></li>
      </ul>
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