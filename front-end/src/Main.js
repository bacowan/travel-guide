import React, { Component } from "react";
import {
  Route,
  BrowserRouter
} from "react-router-dom";
import Navbar from "./Navbar";
import NearbyListPage from "./NearbyListPage";
import MapPage from "./MapPage";
import InterestPointEditPage from "./InterestPointEditPage";
import SignUp from "./SignUp";
import Login from "./Login";
import OptionsPage from "./OptionsPage";
import RequestPermissions from "./RequestPermissions";
 
class Main extends Component {
  constructor(props) {
    super(props);
    this.bearerChanged = this.bearerChanged.bind(this);
    this.onClickOffNavbar = this.onClickOffNavbar.bind(this);
    this.showUserMenuChanged = this.showUserMenuChanged.bind(this);
    this.state = {
      bearer: "",
      showUserMenu: false
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

  showUserMenuChanged(isOpen) {
    this.setState({showUserMenu: isOpen});
  }

  onClickOffNavbar(event) {
    this.showUserMenuChanged(false);
  }

  render() {
    var userId = getUserIdFromBearerToken(this.state.bearer);
    return (
      <BrowserRouter>
        <div className="router-content" onClick={this.onClickOffNavbar}>
          <Navbar
            bearer={this.state.bearer}
            bearerChanged={this.bearerChanged}
            showUserMenu={this.state.showUserMenu}
            showUserMenuChanged={this.showUserMenuChanged}/>
          <div className="fill-rest">
            <Route exact path="/" component={NearbyListPage}/>
            <Route path="/NearbyListPage" component={NearbyListPage}/>
            <Route path="/MapPage" render={(props) => <MapPage {...props} bearer={this.state.bearer}/>}/>
            <Route path="/InterestPointEdit" render={(props) => <InterestPointEditPage {...props} bearer={this.state.bearer}/>}/>
            <Route path="/Options" render={(props) => <OptionsPage {...props} bearer={this.state.bearer} userId={userId}/>}/>
            <Route path="/SignUp" render={(props) => <SignUp {...props} bearerChanged={this.bearerChanged}/>}/>
            <Route path="/Login" render={(props) => <Login {...props} bearerChanged={this.bearerChanged}/>}/>
            <Route path="/RequestPermissions" render={(props) => <RequestPermissions {...props} bearer={this.state.bearer} userId={userId}/>}/>
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

function getUserIdFromBearerToken(token) {
  const payload = token.split('.')[1];
  if (payload !== undefined) {
    const asJson = JSON.parse(window.atob(payload));
    return asJson.sub;
  }
  else {
    return null;
  }
}
 
export default Main;