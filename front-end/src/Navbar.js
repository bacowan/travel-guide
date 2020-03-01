import React, { Component } from "react";
import { NavLink } from "react-router-dom";
import './index.css'
import './responsive.css'

class Navbar extends Component {
    constructor(props) {
        super(props);
        this.onSignOutClick = this.onSignOutClick.bind(this);
        this.onAvatarClick = this.onAvatarClick.bind(this);
        this.onClickNavbar = this.onClickNavbar.bind(this);
        this.state = {
            showUserMenu: false
        };
    }

    onSignOutClick() {
        document.cookie = `bearer=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
        this.props.bearerChanged();
    }

    onAvatarClick(event) {
        this.props.showUserMenuChanged(!this.props.showUserMenu);
        cancelEventPropigation(event);
        
    }

    onClickNavbar(event) {
        this.props.showUserMenuChanged(false);
        cancelEventPropigation(event);
    }

    render() {
        const isLoggedIn = this.props.bearer !== "";
        return (
            <div className="navbar" onClick={this.onClickNavbar}>
            <ul>
                <li className="navbar-left"><NavLink to="/NearbyListPage">Nearby</NavLink></li>
                <li className="navbar-left"><NavLink to="/MapPage">Map</NavLink></li>
                {(isLoggedIn &&
                    <>
                    <img src="https://www.w3schools.com/css/pineapple.jpg" className="navbar-right" onClick={this.onAvatarClick}/>
                    </>)
                    ||
                    (<>
                    <li className="navbar-right"><NavLink to="/Login">Login</NavLink></li>
                    <li className="navbar-right"><NavLink to="/SignUp">Sign Up</NavLink></li>
                    </>)}
            </ul>
            {this.props.showUserMenu && isLoggedIn &&
                <ul className="vertical-navbar-list">
                    <li><NavLink to="/Options">Options</NavLink></li>
                    <li><NavLink to="/NearbyListPage" onClick={this.onSignOutClick}>Sign Out</NavLink></li>
                </ul>}
            </div>
        )
    }
}

function cancelEventPropigation(e) {
    if (!e) var e = window.event;
    e.cancelBubble = true;
    if (e.stopPropagation) e.stopPropagation();
}

export default Navbar;