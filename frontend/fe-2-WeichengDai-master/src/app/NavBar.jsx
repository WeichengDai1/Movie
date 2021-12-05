import React from "react";
import {NavLink} from "react-router-dom";

/*
    NavBar is used to place all of your NavLinks
    Each NavLink acts like a redirect button, but will
    tell the Router Switch to switch the page WITHOUT
    needing to refresh the page.

    This makes for a better experience for the user and
    also helps us maintain our states while the user is 
    moving between pages

    NavLink takes one main argument you will need
        to="/path"
    
    Like all other components you can also pass className

    When you want to have certain links show up depending 
    on the Apps state (like in an instance of a user being
    loggedIn or out) use the conditional statement
        {boolean && <NavLink>}
    if you would like multiple components just make sure
    to wrap it in a <Fragment>
        {boolean && <Fragment><NavLink/><NavLink/><NavLink/></Fragment>}

    Fragment is imported here to show you an example of how
    to import it to react.
*/
const NavBar = () => {
    return (
        <nav className="nav-bar">
            <NavLink className="nav-link" to="/login">
                Login
            </NavLink>
            <NavLink className="nav-link" to="/register">
                Register
            </NavLink>
            <NavLink className="nav-link" to="/search">
                Search
            </NavLink>
        </nav>
    );
}

export default NavBar;
