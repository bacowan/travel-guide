import React, { Component } from "react";
import {
  Route,
  NavLink,
  HashRouter
} from "react-router-dom";
import NearbyListPage from "./NearbyListPage";
 
class Main extends Component {
  render() {
    return (
      <HashRouter>
        <div className="content">
          <Route exact path="/" component={NearbyListPage}/>
        </div>
      </HashRouter>
    );
  }
}
 
export default Main;