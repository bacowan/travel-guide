import React, { Component } from "react";
import { NavLink } from "react-router-dom";
import './index.css'
import './responsive.css'

class Navbar extends Component {
    constructor(props) {
        super(props);
        this.onSignOutClick = this.onSignOutClick.bind(this);
    }

    onSignOutClick() {
        document.cookie = `bearer=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
        this.props.bearerChanged();
    }

    render() {
        return (
            <ul className="navbar">
                <li className="navbar-left"><NavLink to="/NearbyListPage">Nearby</NavLink></li>
                <li className="navbar-left"><NavLink to="/MapPage">Map</NavLink></li>
                {(this.props.bearer !== "" &&
                    <li className="navbar-right"><NavLink to="/NearbyListPage" onClick={this.onSignOutClick}>Sign Out</NavLink></li>)
                    ||
                    (<>
                    <li className="navbar-right"><NavLink to="/Login">Login</NavLink></li>
                    <li className="navbar-right"><NavLink to="/SignUp">Sign Up</NavLink></li>
                    </>)}
                
            </ul>
        )
    }
}

export default Navbar;