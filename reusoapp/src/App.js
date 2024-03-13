import { React, useEffect, useState } from "react";
import {
    BrowserRouter as Router,
    Route, Switch
} from "react-router-dom";
import LogIn from './Components/Auth/log_in';
import SignUp from './Components/Auth/sign_up';
import { AuthContext } from './Components/Auth/userContext';
import CreatePost from './Components/ContentPages/CreatePost';
import HomePage from './Components/ContentPages/HomePage';
import Profile from './Components/ContentPages/Profile';
import SearchPosts from './Components/ContentPages/SearchPosts';
import ViewPost from './Components/ContentPages/ViewPost';
import HomeBar from './Components/Global/AppBar';



export default function App() {
    const [loggedIn, setLoggedIn] = useState(false);
    const login = () => setLoggedIn(true);
    const logout = () => setLoggedIn(false);

    const tok = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0dXNlcm5hbWUiLCJleHAiOjE2MTMxNTE0MzF9.PbAZ0co-T1vghWAj4gx1sDUbiykaE6gYDDtZYJNQglr6LFnSTLseqhjiJIvD-7tXmVO7lJ5PNQcdS0nnNbD6DA'

    localStorage.setItem("baseApiUrl", "http://localhost:8080")
    
    useEffect(() => {
        if (localStorage.getItem("token")) {
            sessionStorage.setItem("token", localStorage.getItem("token"))
            setLoggedIn(true);
        }
    }, [])

    const [searchQuery, setSearchQ] = useState('');
    const handleSearchQuery = (q) => setSearchQ(q);

    return (
        <AuthContext.Provider value={
            { isLoggedIn: loggedIn, login: login, logout: logout }
        }>
            <Router>
                <Switch>
                    <Route exact path="/login">
                        <LogIn />
                    </Route>
                    <Route exact path="/signup">
                        <SignUp />
                    </Route>

                    <Route path="/">
                        <HomeBar onSearchChange={handleSearchQuery} />
                        {(searchQuery) ?
                            <SearchPosts searchQuery={searchQuery} />
                        : 
                            <Switch>
                                <Route path="/post">
                                    <ViewPost />
                                </Route>
                                <Route path="/create">
                                    <CreatePost />
                                </Route>
                                <Route path="/user">
                                    <Profile />
                                </Route>
                                <Route exact path="/">
                                    <HomePage />
                                </Route>
                            </Switch>   
                        }
                    </Route>
                </Switch>
            </Router>
        </AuthContext.Provider>
    );
}