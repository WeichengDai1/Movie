import React,{useState} from "react";
import Idm from "../services/Idm";

/*
  Using localStorage is similar to how we use
  dictionary.
  
  To set a variable call `localStorage.set("key", value)`
  To get a variable call `localStorage.get("key")`

  Local Storage persists through website refreshes so
  it is perfect for storing things we dont want to lose
  like a users session

  You must call `const localStorage = require("local-storage");`
  in any class that you want to use this in, it is the same
  local storage in the entire website regardless of where you call
  it as each website gets the same instance of the storage.

  So think of it as a global dictionary.
*/
const localStorage = require("local-storage");

const Register = ({ history, location, match }) => {
    const [email, setEmail] = useState();
    const [password, setPassword] = useState();
    const handleSubmit = (e) => {
        e.preventDefault();

        Idm.regist(email, password)
            .then(response => {
                response["data"]["resultCode"] && alert(response["data"]["message"]);
                response["data"]["resultCode"] === 110 && history.push("/login")
            })
            .catch(error => alert(error));
    };
    return (
        <div className="form-box">
            <h1>Register</h1>
            <form onSubmit={handleSubmit}>
                <label className="form-label">Email</label>
                <input
                    className="form-input"
                    type="email"
                    onChange={(e) => setEmail(e.target.value)}
                />
                <label className="form-label">Password</label>
                <input
                    className="form-input"
                    type="password"
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button className="form-button">Register</button>
            </form>
        </div>
    );

    return (
        <div className="form-box">
            <h1>Register</h1>
        </div>
    );
}

export default Register;
