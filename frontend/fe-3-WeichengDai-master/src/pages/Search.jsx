import React, { useEffect, useState} from "react";

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
const Search = ({ history, location, match }) => {
    const [ movieList, setMovieList] = useState([]);
    const [ title, setTitle] = useState(undefined);
    const [ genre, setGenre] = useState(undefined);

   useEffect(() => {
       // Movie.search(location.search)
       // .then(response => {
       //    setMovieList(response.data.movies);
       // })
    }, [location.search]);

   const createQuery = () => {
       return "title=" + title + "&" + "genre=" + genre;
   }

   const onclick = () => {
       history.push("/search" + createQuery());
   }

    return (
        <div className="form-box">
            <h1>{location.search}</h1>
        </div>
    );
}

export default Search;
