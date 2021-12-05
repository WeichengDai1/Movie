import Socket from "./Socket";
import { baseUrl, movieEPs } from "../config/config.json";
import Gateway from "./Gateway";

async function search(queryString) {
    const options = {
        baseURL: baseUrl, // Base URL
        url: movieEPs.searchEP, // Path of URL
    }

    const response = await Socket.GET(options);

    return await Gateway.getReport(response);
}

export default {
    search
};
