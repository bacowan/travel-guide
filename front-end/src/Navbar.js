import React, { Component } from "react";
import { NavLink } from "react-router-dom";
import './index.css'
import './responsive.css'

class Navbar extends Component {
    render() {
        return (
            <ul className="navbar">
              <li><a>Login</a></li>
              <li><a>Sign Up</a></li>
            </ul>
        )
    }
}

export default Navbar;