import Socket from "./Socket";
import { baseUrl, idmEPs } from "../config/config.json";
import Gateway from "./Gateway";

async function login(email, password) {
    const payLoad = {
        email: email,
        password: password.split("")
    };

    const options = {
        baseURL: baseUrl, // Base URL
        url: idmEPs.loginEP, // Path of URL
        data: payLoad // Data to send in Body
    }

    const response = await Socket.POST(options);

    return await Gateway.getReport(response);
}

export default {
    login
};
