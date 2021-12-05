import React from "react";
import {Route, Switch,Redirect} from "react-router-dom";

import Login from "../pages/Login";
import Register from "../pages/Register";
import Search from "../pages/Search";

/*
 *  Here is where we can place all our Page
 *  Components. Inside of the switch you can list
 *  as many of <Route>'s as you would like.
 *
 *  It takes two arguments:
 *    path="/path"
 *    component={Component}
 *
 *  Remember, if you want to pass some variables inside
 *  of a component inside of Route you must then
 *  replace:
 *    `component={Component}`
 *      with
 *    `render={props => <Component {...props} var={var}/>}`
 *  The reason for this is so that we can keep certain props
 *  that the Switch is trying to pass alive such as:
 *    history
 *    location
 *    match
 *
 *  Be careful not to mix up the the props within the lambda with the
 *  props passed to this Component, for example:
 *
 *    const Content = (props) => {
 *     ....
 *     <Route path="/register"
 *            render={props => <Register {...props}/>}/>
 *            // The props inside of render is not the same as the props passed to content
 *            // if you want to use the props passed to content prefix it with "this"
 *            render={props => <Register {...props}  more={this.props.value}/>}/>
 *     ....
 *    }
 *
 *  If you would like the user to be moved to another page
 *  simply use the history object. You can get it through
 *  this.props.
 *
 *    IMPORTANT: The three objects will be passed to your
 *    component automatically IF they are INSIDE OF SWITCH
 *    HOWEVER: if you have a component inside of that component
 *    YOU WILL NEED TO MANUALLY PASS IT along the chain.
 *
 *    history.push("/path")
 *      and
 *    history.push("/path", {key: value, key2: value})
 *
 *  are two ways of moving the user to a new page, the
 *  latter lets you also supply an object to be given to the new
 *  page that will be storage in location.state
 */
const Content = () => {
    return (
        <div className="content">
            <Redirect exact path = "/" to ="/login"/>
            <Switch>
                <Route path="/login"
                       component={Login}/>
                <Route path="/register"
                       component={props => <Register {...props}/>}/>
                <Route path = "/search"
                        component={Search}/>

            </Switch>
        </div>
    );
}

export default Content;
