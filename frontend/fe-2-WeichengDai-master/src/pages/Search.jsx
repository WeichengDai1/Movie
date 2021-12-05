import React,{useState} from "react";
import Movies from "../services/Movies";

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

const Search = ({ history, location, match }) => {
    let [searchBy,setSearchBy] = useState();
    let [searchTerm, setSearchTerm]= useState();
    let [results, setResults] = useState();

    const handleSearch=(e)=>{
        e.preventDefault();
        Movies.search(localStorage.get("email"),localStorage.get("session_id"),searchBy,searchTerm)
            .then(
                (response)=>{
                    setResults(response["data"]["movies"]);
                    // alert(response);
                }
            ).catch((error)=>{alert(error)})
    };

    let movieTable = ""

    if(results){
        movieTable = (
            <table>
                <thead>
                    <td>Title</td>
                    <td>Director</td>
                    <td>Year</td>
                </thead>
                <tbody>
                {
                    results.map((result)=>{
                        return (<tr key = {result.movie_id}>
                            <td>{result.title}</td>
                            <td>{result.director}</td>
                            <td>{result.year}</td>
                        </tr>)
                    })
                }
                </tbody>
            </table>
        );
    }else{
        movieTable=(
            <p></p>
        )
    };


    return (
        <div className="form-box">
            <h1>Search</h1>
            <form onSubmit={handleSearch}>
                <input type = "text" onChange = {(e)=>setSearchTerm(e.target.value)}/>
                <select
                    // name="By" id = "By"
                    onChange={(e)=>{
                        setSearchBy(e.target.value)
                        console.log("e.target.value", e.target.value);
                    }}
                    defaultValue=""
                    value = {searchBy}>
                    <option value=""></option>
                    <option value="title">title</option>
                    <option value="year">year</option>
                    <option value="director">director</option>
                </select>
                <button>Search</button>
                {movieTable}
            </form>
        </div>
    );
}

export default Search;
