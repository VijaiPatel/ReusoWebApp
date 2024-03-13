import { Typography } from '@material-ui/core'
import Container from '@material-ui/core/Container'
import axios from 'axios'
import React, { useEffect } from 'react'
import PostGrid from '../Global/PostGrid'


const url = localStorage.getItem("baseApiUrl") || 'http://localhost:8080'

/**
 * @param {Number} page Page number for result pagination
 */
const retrievePosts = (page) => `${url}/search/posts/tag/?page=${page}&size=6`
const thumbnailUrl = (id) => `${url}/media/posts/${id}/main`

export default function SearchPosts(props) {
    const [posts, setPosts] = React.useState([]);
    const [page, setPage] = React.useState(0);
    const [isFetching, setIsFetching] = React.useState(true);
    const [tags, setTags] = React.useState([])

    function handleTags() {
        let arr = props.searchQuery.replace(/[^A-Z0-9]+/gi, ' ').trim().split(' ');
        if (arr.every((t)=>tags.includes(t))) return
        setTags(arr); setPage(0); setPosts([]); setIsFetching(true);
    }
    
    // eslint-disable-next-line
    useEffect(() => handleTags(), [props])

    useEffect(() => {
        if (!isFetching) return;
        setIsFetching(false);
        axios.post(retrievePosts(page), tags)
            .then(res => {
                let newPostState = (posts) ? [...posts] : [];
                if (res.data.length) setPage(page + 1)
                res.data.forEach((post) => {
                    let newPost = {
                        post: {
                            id: post.id,
                            image: thumbnailUrl(post.id)
                        },
                        author: post.author.username
                    }
                    newPostState.push(newPost);
                })
                setPosts(newPostState);
            })
            .catch(err => console.log(err))
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isFetching]);

    useEffect(() => {
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    function handleScroll() {
        if (window.innerHeight + document.documentElement.scrollTop
            !== document.documentElement.offsetHeight) return;
        setIsFetching(true);
    }

    return (
        <Container maxWidth='sm'>
            {(posts) ?  
            <Typography variant='h4'>Search results</Typography>
            :
            <Typography varient='h2'>No posts found, try a broader search</Typography>
            }
            <PostGrid posts={posts} />
        </Container>
    )
}