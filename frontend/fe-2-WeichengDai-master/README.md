# CS122B - Frontend 2

### Pre-Setup if you would like to use Visual Studio instead of WebStorm

1. Install Visual Studio Code, NOTE: This is not Visual Studio
2. Install the following addons (Optional but strongly recommended)
- Prettier - Code formatter (This will automatically format your code as you write)
- React Native Tools - Helps with writing some repetitive code
4. Create a Chrome Shortcut with no security, follow instructions below:

### Using Chrome Without CORS

1. Create a folder to have your testing Chrome
2. Create a folder within it named temp
3. Create a new shortcut with the location
~~~
"C:\Program Files (x86)\Google\Chrome\Application\chrome.exe" --disable-web-security --user-data-dir="<full path to your temp folder>"
~~~
4. If it worked you should be able to see the warning: 
~~~
you are using an unsupported command-line flag: --disable-web-security. Stability and security will suffer.
~~~
5. Install React Developer Tools
6. Make localhost:3000 your homepage for ease of use

### Setup instructions

1. Download and install node.js - Latest Version
2. Type `npm` in your terminal to ensure you correctly installed node
3. Type `npm install` **within the root of this repo** to install all the dependencies (This will take a while)
4. Type `npm start` **within the root of this repo** to start the server and you should have a window open with the front end
5. [In case of error] If you run into a missing dependency error try `npm install` again then `npm start` if the problem persists manually install the dependency it is asking for.

### Dependencies

In case you get an error for a missing dependency just enter in these commands to download the respective dependency:

| Package          | Command                  | Site                                           |
| :--------------- | :----------------------- | :--------------------------------------------- |
| Axios            | `npm i axios`            | https://www.npmjs.com/package/axios            |
| js Cookie        | `npm i js-cookie`        | https://www.npmjs.com/package/js-cookie        |
| Local Storage    | `npm i local-storage`    | https://www.npmjs.com/package/local-storage    | 
| Create React App | `npm i create-react-app` | https://www.npmjs.com/package/create-react-app |
| React            | `npm i react`            | https://www.npmjs.com/package/react            |
| React DOM        | `npm i react-dom`        | https://www.npmjs.com/package/react-dom        |
| React Scripts    | `npm i react-scripts`    | https://www.npmjs.com/package/react-scripts    |
| React Router DOM | `npm i react-router-dom` | https://www.npmjs.com/package/react-router-dom |

## Notes

### Passing Variables and Functions

Remember when passing around variable and functions

If you want componenets down the tree to VIEW your variable, simply pass that varaible

If you want componenets down the tree to MODIFY your varaible, then pass a function down the tree that allows the components to modify it as needed

The function should be made in the Component that "owns" that varaible, as in which Component has it as part of its state because only that component can modify its own state.

Pass variables and functions into Components like so:
~~~
<Componenet var={value} func={value}/>
~~~
!DO NOT CALL THE FUNCTION!
~~~
func={function} WORKS
func={function()} DOES NOT WORK, This is calling the function and storing the return value
~~~

To get the passed variables and functions from inside the Componenet call `this.props.<NAME>`

##### useState()

The way useState works is by getting a varaiable and a function to update the variable

```javascript
// Starting state
const [ total, setTotal ] = useState();
// Starting state With Default Value
const [ page, setPage ] = useState(1);

// Function to Update
setTotal(100);
```


#### functions

There are two ways of declaring functions

```javascript
// without "this" binding
func() {}

// with "this" binding
func = () => {}
```

the latter, func = () => {} will automatically
bind "this" to it. In these functions you can
call this, however in the first declaration
this will be undefined.

```javascript
// without "this" binding
func() {
    this.setState() // throws error, this is undefined.
  }

// with "this" binding
func = () => {
    this.setState() // This is fine.
  }
```

### Router

<Switch> is where you place all your page components. Inside of the switch you can list
as many of <Route>'s as you would like.

It takes two arguments:
~~~
path="/path"
component={Component}
~~~

Remember, if you want to pass some varaibles inside of a componenet inside of Route you must then replace:
`component={Component}`
  with
`render={props => <Component {...props} var={var}/>}`
The reason for this is so that we can keep certain props that the Switch is trying to pass alive such as:
~~~
history
location
match
~~~

If you would like the user to be moved to another page simply use the history object. You can get it through this.props. 

**IMPORTANT: The three objects will be passed to your componenet automaticly IF they are INSIDE OF SWITCH**
**HOWEVER: if you have a component inside of that component YOU WILL NEED TO MANNUALY PASS IT along the chain.**

There are two ways of moving the user to a new page, the latter lets you also supply an object to be given to the new page that will be storage in `location.state`

~~~
history.push("/path") 
  and 
history.push("/path", {key: value, key2: value})
~~~

### LocalStorage

Using localStorage is similar to how we usedictionarys. 

To set a variable call `localStorage.set("key", value)`
To get a variable call `localStorage.get("key")`

Local Storage persists through website refreshes so it is perfect for storing things we dont want to lose like a users session

You must call `const localStorage = require("local-storage");` in any class that you want to use this in, it is the same local storage in the entire website regardless of where you call it as each website gets the same instance of the storage. So think of it as a global dictionary.
