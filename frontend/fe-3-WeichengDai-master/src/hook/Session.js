import React, { createContext, useState, useContext } from "react";

const localStorage = require("local-storage");

const SessionContext = createContext(null);

export const SessionProvider = ({children}) => {
    const [session, setter] = useState(localStorage.get("session_id"));

    const setSession = (newSession) => {
        setter(newSession);
        localStorage.set("session_id", newSession)
    }

    const value = {session, setSession }

    return (
      <SessionContext.Provider value={value}>
          {children}
      </SessionContext.Provider>
    );
}

export const useSession = () => {
    const {session, setSession } = useContext(SessionContext);

    return {session, setSession};
}

