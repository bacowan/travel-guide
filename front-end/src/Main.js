import React, { Component } from "react";
import {
  Route,
  NavLink,
  HashRouter
} from "react-router-dom";
import NearbyList from "./NearbyList";
 
class Main extends Component {
  render() {
    return (
      <HashRouter>
        <div className="content">
          <Route exact path="/" component={NearbyList}/>
        </div>
      </HashRouter>
    );
  }
}
 
export default Main;