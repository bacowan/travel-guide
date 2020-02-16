import React, { Component } from "react";
import { NavLink } from "react-router-dom";
import './index.css'
import './responsive.css'

class Navbar extends Component {
    render() {
        return (
            <ul className="navbar">
                <li className="navbar-left"><NavLink to="/NearbyListPage">Nearby</NavLink></li>
                <li className="navbar-left"><NavLink to="/MapPage">Map</NavLink></li>
                <li className="navbar-right"><a>Login</a></li>
                <li className="navbar-right"><a>Sign Up</a></li>
            </ul>
        )
    }
}

export default Navbar;