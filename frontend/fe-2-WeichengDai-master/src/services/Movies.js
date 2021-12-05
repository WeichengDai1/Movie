import Socket from "./Socket";
import { moviesUrl, moviesEPs } from "../config/config.json";

async function search(userEmail,session,by,term){
    let payLoad = { };
    payLoad[by] = term;
    console.log(payLoad);

    const headers={
        "email":userEmail,
        "session_id":session
    }

    const options={
        baseURL:moviesUrl,
        url:moviesEPs.searchEP,
        params:payLoad,
        headers:headers
    }

    return await Socket.GET(options);
}

export default {
    search
};