import React from "react";
import {Route, Switch} from "react-router-dom";

import Login from "../pages/Login";
import Register from "../pages/Register";
import Home from "../pages/Home";
import Search from "../pages/Search";

const Content = () => {
    return (
        <div className="content">
            <Switch>
                <Route path="/login"
                       component={Login}/>
                <Route path="/register"
                       component={Register}/>
                <Route path="/search"
                       component={Search}/>
                <Route path="/"
                       component={Home}/>
            </Switch>
        </div>
    );
}

export default Content;
