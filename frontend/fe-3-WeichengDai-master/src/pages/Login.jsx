import React, {useContext, useEffect} from "react";
import Idm from "../services/Idm";
import {useSession} from "../hook/Session";

const Login = ({history, location, match }) => {
    const {session, setSession} = useSession();

    return (
        <div className="form-box">
            <h1>Login</h1>
        </div>
    );
}

export default Login;
