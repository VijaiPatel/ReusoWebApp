import Avatar from '@material-ui/core/Avatar';
import ButtonBase from '@material-ui/core/ButtonBase';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import CardHeader from '@material-ui/core/CardHeader';
import CardMedia from '@material-ui/core/CardMedia';
import Container from '@material-ui/core/Container';
import Divider from '@material-ui/core/Divider';
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import PersonIcon from '@material-ui/icons/Person';
import axios from 'axios';
import React, { useEffect } from 'react';
import { Link, useHistory } from 'react-router-dom';

const url = localStorage.getItem("baseApiUrl")
const postInfo = (id) => `${url}/posts/${id}`
const getPostImage = (id, key) => `${url}/media/posts/${id}/${key}`

const useStyles = makeStyles((theme) => ({
    root: {
        maxWidth: '100%',
    },
    subCard: {
        marginBottom: theme.spacing(2),
    },
    divider: {
        marginBottom: theme.spacing(3),
        marginTop: theme.spacing(2)
    }
}));

export default function ViewPost() {
    const classes = useStyles();
    const [post, setPost] = React.useState(null)
    const postId = window.location.pathname.split("/").pop();
    let history = useHistory();

    useEffect(() => {
        axios.get(postInfo(postId))
            .then(res => {
                let newState = res.data;
                let checkImages = [];
                newState.createdAt = new Intl.DateTimeFormat("en-GB", {
                    year: "numeric",
                    month: "long",
                    day: "2-digit"
                }).format(new Date(res.data.createdAt))
                newState.postSteps.forEach((step, i) => {
                    checkImages.push(
                        axios.get(getPostImage(postId, step.stepKey))
                            .then(res => {
                                if (res.status === 200) {
                                    newState.postSteps[i].image = getPostImage(postId, step.stepKey);
                                }
                            })
                            .catch(() => {
                                if (res.status === 404) newState.postSteps[i].image = false;
                            })
                    )
                })
                Promise.all(checkImages)
                .then(() => setPost(newState))
                .catch(err => console.log(err))
            })
            .catch(err => {
                console.log(err); history.push('/')
            })
    // eslint-disable-next-line
    }, [])

    return (
        <Container maxWidth='sm'>
            {(post) ?
                <Card className={classes.root}>
                    <ButtonBase
                        component={Link}
                        to={`/user/${post.author.username}`}
                    >
                        <CardHeader
                            avatar={
                                <Avatar className={classes.avatar}>
                                    <PersonIcon />
                                </Avatar>
                            }
                            title={post.author.username}
                            subheader={post.createdAt}
                        />
                    </ButtonBase>
                    <CardMedia
                        component="img"
                        image={getPostImage(post.id, 'main')}
                        title="main"
                    />
                    <CardContent>
                        <Typography
                            variant='body1' color="textPrimary"
                            component="p" className={classes.mainCaption}
                        >
                            {post.caption}
                        </Typography>
                        <Divider className={classes.divider} />
                        {post.postSteps.map((step) => {
                            return (
                                <Card className={classes.subCard}>
                                    <CardHeader
                                        title={
                                            <Typography variant='h6'>
                                                {`Step ${step.stepKey}`}
                                            </Typography>
                                        }
                                    />

                                    <CardContent>
                                        {step.image ?
                                            <CardMedia
                                                className={classes.media}
                                                component="img"
                                                image={step.image}
                                            />
                                            : ''}
                                        <Typography variant="body2" color='textSecondary' component="p">
                                            {step.caption}
                                        </Typography>
                                    </CardContent>
                                </Card>
                            )
                        })}
                    </CardContent>
                </Card>
                : ''}
        </Container>
    );

}