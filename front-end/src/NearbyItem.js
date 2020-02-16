import React, { Component } from "react";
import { NavLink } from "react-router-dom";
import './index.css';
import './responsive.css';

class NearbyItem extends Component {
  render() {
    return (
        <div className="list-item">
            <img src="https://www.w3schools.com/css/pineapple.jpg" className="list-image"/>
            <p className="list-header">{this.props.title}</p>
            <p>{this.props.distance}</p>
            <p>{this.props.tags}</p>
        </div>
    );
  }
}

export default NearbyItem;