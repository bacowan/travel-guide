import React, { Component } from "react";
import {
  Route,
  NavLink,
  HashRouter
} from "react-router-dom";
import LandingPage from "./LandingPage";
 
class Main extends Component {
  render() {
    return (
      <HashRouter>
        <div className="content">
          <Route exact path="/" component={LandingPage}/>
        </div>
      </HashRouter>
    );
  }
}
 
export default Main;