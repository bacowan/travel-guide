import React, { Component } from "react";
import {
  Route,
  NavLink,
  HashRouter
} from "react-router-dom";
import Navbar from "./Navbar";
import NearbyListPage from "./NearbyListPage";
import MapPage from "./MapPage";
 
class Main extends Component {
  render() {
    return (
      <HashRouter>
        <div className="content">
          <Navbar/>
          <Route exact path="/" component={NearbyListPage}/>
          <Route path="/NearbyListPage" component={NearbyListPage}/>
          <Route path="/MapPage" component={MapPage}/>
        </div>
      </HashRouter>
    );
  }
}
 
export default Main;