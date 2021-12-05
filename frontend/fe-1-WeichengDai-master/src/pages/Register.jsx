import React,{useState} from "react";

import "../css/form.css";
import Idm from "../services/Idm";

const Register = () => {
    const [email, setEmail] = useState();
    const [password, setPassword] = useState();
    const handleSubmit = (e) => {
        e.preventDefault();

        Idm.regist(email, password)
            .then(response => alert(JSON.stringify(response.data, null, 4)))
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
}

export default Register;
