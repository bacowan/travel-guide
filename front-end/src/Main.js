import React, { Component } from "react";
import {
  Route,
  BrowserRouter
} from "react-router-dom";
import Navbar from "./Navbar";
import NearbyListPage from "./NearbyListPage";
import MapPage from "./MapPage";
import SignUp from "./SignUp";
import Login from "./Login";
 
class Main extends Component {
  constructor(props) {
    super(props);
    this.bearerChanged = this.bearerChanged.bind(this);
    this.state = {
      bearer: ""
    };
  }

  componentDidMount() {
      this.bearerChanged();
  }

  componentWillUnmount() {
  }

  bearerChanged() {
    this.setState({
      bearer: getCookie("bearer")
    });
  }

  render() {
    return (
      <BrowserRouter>
        <div className="router-content">
          <Navbar bearer={this.state.bearer} bearerChanged={this.bearerChanged}/>
          <div>
            <Route exact path="/" component={NearbyListPage}/>
            <Route path="/NearbyListPage" component={NearbyListPage}/>
            <Route path="/MapPage" component={MapPage}/>
            <Route path="/SignUp" render={(props) => <SignUp {...props} bearerChanged={this.bearerChanged}/>}/>
            <Route path="/Login" render={(props) => <Login {...props} bearerChanged={this.bearerChanged}/>}/>
          </div>
        </div>
      </BrowserRouter>
    );
  }
}

// from https://www.w3schools.com/js/js_cookies.asp
function getCookie(cname) {
  var name = cname + "=";
  var decodedCookie = decodeURIComponent(document.cookie);
  var ca = decodedCookie.split(';');
  for(var i = 0; i <ca.length; i++) {
      var c = ca[i];
      while (c.charAt(0) == ' ') {
      c = c.substring(1);
      }
      if (c.indexOf(name) == 0) {
      return c.substring(name.length, c.length);
      }
  }
  return "";
}
 
export default Main;