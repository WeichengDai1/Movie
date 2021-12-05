import React from "react";
import ReactDOM from "react-dom";
import { BrowserRouter } from "react-router-dom";

import App from "./app/App";

import "./css/index.css";
import "./css/nav-bar.css";
import "./css/form.css";

/*
  This Function connects Our Main <App> to our HTML file
  by connecting <App> to <div id="root"></div>. <App>'s render
  function then gets called and any component within <App> will
  also get called.
*/
ReactDOM.render(
  <BrowserRouter>
    <App />
  </BrowserRouter>,
  document.getElementById("root")
);
