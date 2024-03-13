import { Avatar, makeStyles, Paper, Typography } from '@material-ui/core'
import Container from '@material-ui/core/Container'
import Grid from '@material-ui/core/Grid'
import PersonIcon from '@material-ui/icons/Person'
import axios from 'axios'
import jwt from 'jwt-decode'
import React, { useEffect } from 'react'
import { useHistory } from 'react-router-dom'
import { AuthContext } from '../Auth/userContext'
import PostGrid from '../Global/PostGrid'


const url = localStorage.getItem("baseApiUrl") || 'http://localhost:8080'

/**
 * @param {number} id User ID
 * @param {*} page Page number for result pagination
 */
const retrievePosts = (id, page) => `${url}/search/posts/id/?id=${id}&page=${page}&size=20`
const thumbnailUrl = (id) => `${url}/media/posts/${id}/main`
const searchUser = (username) => `${url}/users/profile/public/${username}`

const useStyles = makeStyles((theme) => ({
    header: {
        padding: '10px'
    },
    avatar: {
        paddingRight: '10px'
    },
    gridListContainer: {
        display: 'flex',
        flexWrap: 'wrap',
        justifyContent: 'space-around',
        overflow: 'hidden',
        //backgroundColor: theme.palette.background.paper,
    },
    gridList: {
        width: '100%',
    },
}));

export default function ProfilePage() {
    let history = useHistory();
    const classes = useStyles();
    const userContext = React.useContext(AuthContext);
    const [user, setUser] = React.useState({});
    const [posts, setPosts] = React.useState([]);
    const [page, setPage] = React.useState(0);
    const [isFetching, setIsFetching] = React.useState(false);

    const getUserDetails = () => {
        let username;
        let pathname = window.location.pathname;
        if (/* !pathname || */pathname === '/user') {
            const token = sessionStorage.getItem('token');
            if (!token || !userContext.isLoggedIn) history.push('/');
            let decoded = jwt(token);
            username = decoded.sub;
        } else username = pathname.split('/').pop();
        return username;
    }

    useEffect(() => {
        axios.get(searchUser(getUserDetails()))
        .then(res => {
            setUser(res.data); setIsFetching(true);
        })
        .catch((err) => {
            console.log(err)
            history.push('/');
        })
        // eslint-disable-next-line
    }, [])

    function handleScroll() {
        if (window.innerHeight + document.documentElement.scrollTop 
            !== document.documentElement.offsetHeight) return;
        setIsFetching(true);
    }

    useEffect(() => {
        if (!isFetching || !user) return;
        axios.get(retrievePosts(user.id, page))
        .then(res => {
            let newPostState = (posts) ? [...posts] : [];
            if (res.data.length) setPage(page+1)
            res.data.forEach((post) => {
                let newPost = {
                    post: {
                        id: post.id,
                        image: thumbnailUrl(post.id)
                    },
                    author: post.author
                }
                newPostState.push(newPost);
            })
            setPosts(newPostState);
        })
        .catch(err => console.log(err))
        .then(() => setIsFetching(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isFetching]);

    useEffect(() => {
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    return (
        <Container maxWidth='sm'>
            <Paper elevation={3}>
                <Grid
                    container alignItems='center'
                    className={classes.header}
                >
                    <Grid item className={classes.avatar}>
                        <Avatar><PersonIcon /></Avatar>
                    </Grid>
                    <Grid item><Typography>{user.username}</Typography></Grid>
                </Grid>
            </Paper>
            {(posts)?<PostGrid posts={posts}/>:''}
        </Container>
    )
}