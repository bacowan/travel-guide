import React, { Component } from "react";
import {
  Route,
  NavLink,
  HashRouter
} from "react-router-dom";
import Navbar from "./Navbar";
import NearbyListPage from "./NearbyListPage";
import MapPage from "./MapPage";
import SignUp from "./SignUp";
 
class Main extends Component {
  render() {
    return (
      <HashRouter>
        <div className="router-content">
          <Navbar/>
          <div>
            <Route exact path="/" component={NearbyListPage}/>
            <Route path="/NearbyListPage" component={NearbyListPage}/>
            <Route path="/MapPage" component={MapPage}/>
            <Route path="/SignUp" component={SignUp}/>
          </div>
        </div>
      </HashRouter>
    );
  }
}
 
export default Main;