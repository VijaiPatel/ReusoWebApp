import { Button, Grid } from '@material-ui/core'
import Container from '@material-ui/core/Container'
import axios from 'axios'
import React, { useEffect } from 'react'
import PostGrid from '../Global/PostGrid'


const url = localStorage.getItem("baseApiUrl") || 'http://localhost:8080'

/**
 * @param {*} page Page number for result pagination
 */
const retrievePosts = (page) => `${url}/search/posts/?page=${page}&size=6`
const thumbnailUrl = (id) => `${url}/media/posts/${id}/main`


export default function ProfilePage() {
    const [posts, setPosts] = React.useState([]);
    const [page, setPage] = React.useState(0);
    const [isFetching, setIsFetching] = React.useState(true);

    function handleScroll() {
        if (window.innerHeight + document.documentElement.scrollTop 
            !== document.documentElement.offsetHeight) return;
        setIsFetching(true);
    }

    useEffect(() => {
        if (!isFetching) return;
        axios.get(retrievePosts(page))
        .then(res => {
            let newPostState = (posts) ? [...posts] : [];
            if (res.data.length) {
                setPage(page+1);
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
            }
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
            <Grid container justify='center' direction='column'>
            {(posts)?<PostGrid posts={posts}/>:''}
            {(isFetching)?'':
            <Button
                variant='contained' 
                onClick={() => setIsFetching(true)}
            >
                Load new posts
            </Button>
            }
            </Grid>
        </Container>
    )
}