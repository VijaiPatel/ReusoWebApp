import { ButtonBase, makeStyles } from '@material-ui/core';
import Accordion from '@material-ui/core/Accordion';
import AccordionActions from '@material-ui/core/AccordionActions';
import AccordionDetails from '@material-ui/core/AccordionDetails';
import AccordionSummary from '@material-ui/core/AccordionSummary';
import Button from '@material-ui/core/Button';
import Card from '@material-ui/core/Card';
import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';
import CardMedia from '@material-ui/core/CardMedia';
import Chip from '@material-ui/core/Chip';
import Container from '@material-ui/core/Container';
import Divider from '@material-ui/core/Divider';
import Grid from '@material-ui/core/Grid';
import IconButton from '@material-ui/core/IconButton';
import InputAdornment from '@material-ui/core/InputAdornment';
import Paper from '@material-ui/core/Paper';
import TextField from '@material-ui/core/TextField';
import Tooltip from '@material-ui/core/Tooltip';
import Typography from '@material-ui/core/Typography';
import AddCircleIcon from '@material-ui/icons/AddCircle';
import DeleteIcon from '@material-ui/icons/Delete';
import ErrorIcon from '@material-ui/icons/Error';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ImageIcon from '@material-ui/icons/Image';
import SendIcon from '@material-ui/icons/Send';
import axios from 'axios';
import FormData from 'form-data';
import React, { useContext, useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import { AuthContext } from '../Auth/userContext';

const baseUrl = localStorage.getItem("baseApiUrl") || "http://localhost:8080";

const stepErrTxt = 'This step doesn\'t have any text!'
const noDescTxt = 'Your post needs to have a description.'
const noImgTxt = 'Your post needs to have a cover image.'

const useStyles = makeStyles((theme) => ({
    paper: {
        marginTop: theme.spacing(2),
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        width: '100%'
    },
    imgPlaceholder: {
        background: 'radial-gradient(circle, hsla(0, 0%, 76%, 1) 54%, hsla(0, 0%, 73%, 1) 100%)',
        display: 'flex',
        flexDirection: 'column',
        width: '100%',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '100px'
    },
    accordion: {
        width: '100%',
    },
    hoverImage: {
        position: 'absolute',
        display: 'flex',
        flexDirection: 'column',
        color: 'white',
        width: '100%',
        height: '100%',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'rgba(0,0,0,0.2)',
        opacity: '0',
        '&:hover': {
            opacity: '100%'
        }
    },
    accordiondetails: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
    },
    card: {
        width: '100%',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center'
    },
    chipArray: {
        display: 'flex',
        justifyContent: 'center',
        flexWrap: 'wrap',
        '& > *': {
            margin: theme.spacing(0.5),
        },
    },
    divider: {
        marginBottom: theme.spacing(1),
        marginTop: theme.spacing(1)
    },
    submit: {
        marginTop: theme.spacing(2),
        fontWeight: '500',
        color: 'white',
        background: 'linear-gradient(45deg, #6fb5ce, #67B26F)'
    }
}));

const postObj = {
    caption: '',
    postSteps: [
        {
            stepKey: 1,
            caption: ''
        }
    ],
    tags: ['']
};

export default function CreatePost() {
    const classes = useStyles();
    const [mainContent, setMainContent] = React.useState({
        caption: '', image: null, txtErr: false, imgErr: false
    });
    const userContext = useContext(AuthContext);
    const JWT = "Bearer " + sessionStorage.getItem("token");
    let history = useHistory();

    useEffect(() => {
        if (!userContext.isLoggedIn || !JWT) {
            history.push('/login')
        }
    }, [])


    const [steps, setSteps] = React.useState([]);

    const [expanded, setExpanded] = React.useState(0);

    const [tags, setTags] = React.useState([]);

    const [searchTags, setSearchTags] = React.useState([]);

    const [searchTagFieldValue, setSearchField] = React.useState('');

    /**Auto tagging from existing tags
     * @param {string[]} query 
     */
    function autoTag(query) {
        let newState = [];
        let newSet = new Set();
        tags.forEach(tag => newSet.add(tag));
        axios.post(`${baseUrl}/search/tags`, query)
            .then(res => {
                res.data.forEach(tag => newSet.add(tag));
                newSet.forEach(tag => newState.push(tag));
                setTags(newState);
            })
            .catch(res => { if (res.status === 404) return; })
    }

    useEffect(() => {
        if (!steps.length || !steps[expanded-1]) return
        let query;
        if (expanded === -1) {
            if (tags.length > 0) return;
            let bigString = mainContent.caption;
            steps.forEach(step => bigString += (' ' + step.caption))
            query = new Set(bigString.replace(/[^a-z0-9]/gi, ' ').split(' '));
        } else {
            if (!steps[expanded-1].caption) return
            query = new Set(steps[expanded-1].caption.replace(/[^a-z0-9]/gi, ' ').split(' '));
        }
        if (!query.size) return
        else autoTag([...query]);
    }, [expanded])

    const onMainCaptionBlur = (event) => {
        let s = event.target.value;
        if (!s) return;
        let query = new Set(s.replace(/[^a-z0-9]/gi, ' ').split(' '))
        if (!query.size) return;
        else autoTag([...query]);
    }


    const handleTagField = (event) => {
        const field = event.target.value;
        const str = field.replace(/[^A-Za-z0-9]/g, '').toLowerCase();
        setSearchField(str);
        refreshSearchTags();
    }

    const refreshSearchTags = () => {
        const string = searchTagFieldValue;
        if (!string || searchTags.includes(string)) return;
        let q = [string];
        axios.post(`${baseUrl}/search/tags`, q)
            .then(res => {
                let found = res.data;
                setSearchTags(found);
            })
            .catch(err => console.log(err))
    }

    const addTag = (index) => (event) => {
        if (tags.includes(searchTags[index])) return;
        setTags([...tags, searchTags[index]]);
    }

    const addNewTag = () => {
        if (!tags.includes(searchTagFieldValue)) {
            setTags([...tags, searchTagFieldValue]);
        }
    }

    const removeTag = (key) => (event) => {
        let newState = [...tags];
        newState.splice(key, 1);
        setTags(newState);
    }

    const addNewStep = () => {
        let newState = [...steps];
        newState.push({
            caption: '', image: '', error: false
        });
        setSteps(newState);
    }

    const removeStep = (key) => (event) => {
        setExpanded(false);
        let newState = [...steps];
        newState.splice(key, 1);
        setSteps(newState);
    }

    const editCaptionText = (key) => (event) => {
        let caption = event.target.value;
        let newSteps = [...steps];
        newSteps[key].caption = caption;
        newSteps[key].error = false;
        setSteps(newSteps);
    }

    const setStepError = (key) => {
        let newState = [...steps];
        newState[key].error = true;
        setSteps(newState);
    }

    const editDescription = (event) => {
        let caption = event.target.value;
        let newState = { ...mainContent };
        newState.caption = caption;
        newState.txtErr = false;
        setMainContent(newState);
    }

    const addCoverImage = (event) => {
        let file = event.target.files[0];
        let image = URL.createObjectURL(file);
        let newState = { ...mainContent };
        newState.image = image;
        newState.file = file;
        newState.imgErr = false;
        setMainContent(newState);
    }

    const removeCoverImage = () => {
        let newState = { ...mainContent };
        newState.image = null;
        setMainContent(newState);
    }

    const addStepImage = (key) => (event) => {
        let file = event.target.files[0];
        let newState = [...steps];
        newState[key].image = URL.createObjectURL(file);
        newState[key].file = file;
        setSteps(newState);
    }

    const removeStepImage = (key) => (event) => {
        let newState = [...steps]
        newState[key].image = null;
        setSteps(newState);
    }

    const handleAccordion = (index, expanded) => {
        let newState = [...steps];
        setExpanded(expanded ? index : false);
        setSteps(newState);
    }

    const submitPost = async () => {
        if (validateForm()) {
            // Formatting new object for valid api request
            var newPost = { ...postObj };
            newPost.postSteps.pop();
            newPost.caption = mainContent.caption;
            steps.forEach((step, index) => {
                newPost.postSteps.push({
                    stepKey: index + 1,
                    caption: step.caption
                })
            })
            newPost.tags = tags;
            const instance = axios.create();
            instance.defaults.headers.common['Authorization'] = JWT;
            instance.post(
                `${baseUrl}/posts/new`, newPost,
            )
                .then(async (res) => {
                    const postId = res.data.id;
                    let promises = [];
                    let data = new FormData();
                    data.append('image', mainContent.file);
                    promises.push(
                        instance.post(`${baseUrl}/media/posts/${postId}/main`, data,
                            {
                                headers: {
                                    'Content-Type': 'multipart/form-data'
                                }
                            })
                    )
                    steps.forEach(async (step, index) => {
                        let data = new FormData();
                        data.append('image', step.file);
                        instance.post(
                            `${baseUrl}/media/posts/${postId}/${index + 1}`, data,
                            {
                                headers: {
                                    'Content-Type': 'multipart/form-data'
                                }
                            }
                        ).catch(err => console.log(err));
                    })

                    Promise.all(promises)
                        .then(setTimeout(() => {
                            history.push(`/post/${res.data.id}`)
                        }, 500))
                })
                .catch((err) => {
                    console.log(err); history.push('/')
                })
        }
    }

    const validateForm = () => {
        let validForm = true;
        if (!mainContent.image || !mainContent.caption) {
            validForm = false;
            let newState = { ...mainContent };
            newState.imgErr = !mainContent.image;
            newState.txtErr = !mainContent.caption;
            setMainContent(newState);
        }
        steps.forEach((step, index) => {
            if (step.caption === '') {
                validForm = false;
                setStepError(index);
            }
        });
        return validForm;
    }

    return (
        <Container component="main" maxWidth="xs">
            <div className={classes.paper}>
                <Typography component="h1">Create post</Typography>

                <Card className={classes.card}>
                    {(mainContent.image) ?
                        <ButtonBase onClick={removeCoverImage}>
                            <Paper className={classes.hoverImage}>
                                <DeleteIcon />
                                <Typography variant='h5' component='caption'>
                                    Click to delete
                                </Typography>
                            </Paper>
                            <CardMedia
                                component='img'
                                image={mainContent.image}
                                title="cover image"
                            />
                        </ButtonBase>
                        :
                        <label htmlFor="uploadmainimg">
                            <ButtonBase component="span" className={classes.imgPlaceholder}>
                                <ImageIcon />
                                <Typography variant='caption' component='caption'>
                                    Add an image
                                </Typography>
                                <input
                                    accept="image/*" style={{ display: 'none' }}
                                    id="uploadmainimg" type="file" onChange={addCoverImage}
                                />
                                {(mainContent.imgErr) ?
                                    <Typography variant='body1' color='error'>{noImgTxt}</Typography>
                                    : ''}
                            </ButtonBase>
                        </label>
                    }
                    <CardContent>
                        <TextField
                            variant="outlined" required fullWidth multiline
                            name="maincaption" label="Description" id="maincaption"
                            error={mainContent.txtErr}
                            helperText={(mainContent.txtErr) ? noDescTxt : ''}
                            onChange={editDescription} onBlur={onMainCaptionBlur}
                        />
                    </CardContent>
                    <CardActions>
                        <Button size="small" onClick={addNewStep}>
                            Add a step
                        </Button>
                    </CardActions>
                    <Divider className={classes.divider} />
                    <CardContent>
                        {steps.map((step, index) => {
                            return (
                                <Accordion
                                    className={classes.accordion}
                                    expanded={(expanded === index)}
                                    onChange={(event, exp) => handleAccordion(index, exp)}
                                >
                                    <AccordionSummary
                                        expandIcon={<ExpandMoreIcon />}
                                    >
                                        <Grid container justify='space-between'>
                                            <Grid item>
                                                <Typography variant="h6">
                                                    {`Step ${index + 1}`}
                                                </Typography>
                                            </Grid>
                                            <Grid container item xs={9} alignItems='center' justify='space-between'>
                                                <Grid item>
                                                    {(step.caption.length > 27) ? 
                                                    step.caption.substring(0, 27) + '...' : 
                                                    step.caption}
                                                </Grid>
                                                {(step.error) ?
                                                    <Tooltip title={stepErrTxt} placement='left'>
                                                        <ErrorIcon /></Tooltip>
                                                    : null}
                                            </Grid>
                                        </Grid>
                                    </AccordionSummary>
                                    <AccordionDetails className={classes.accordiondetails}>
                                        <Card className={classes.card}>
                                            {(step.image) ?
                                                <ButtonBase onClick={removeStepImage(index)}>
                                                    <Paper className={classes.hoverImage}>
                                                        <DeleteIcon />
                                                        <Typography variant='h6' component='caption'>
                                                            Click to delete
                                                    </Typography>
                                                    </Paper>
                                                    <CardMedia
                                                        component='img'
                                                        image={step.image}
                                                        title="cover image"
                                                    />
                                                </ButtonBase>
                                                :
                                                <label htmlFor={`uploadimg${index + 1}`}>
                                                    <ButtonBase component="span" className={classes.imgPlaceholder}>
                                                        <Typography variant='caption' component='caption'>
                                                            Add an image
                                                </Typography>
                                                        <input
                                                            accept="image/*" style={{ display: 'none' }}
                                                            id={`uploadimg${index + 1}`} type="file" onChange={addStepImage(index)}
                                                        />
                                                        <ImageIcon />
                                                    </ButtonBase>
                                                </label>
                                            }
                                            <CardContent>
                                                <TextField
                                                    variant="outlined" fullWidth multiline
                                                    label="Caption"
                                                    onChange={editCaptionText(index)}
                                                    value={step.caption}
                                                />
                                            </CardContent>
                                        </Card>
                                    </AccordionDetails>
                                    <Divider />
                                    <AccordionActions>
                                        <Button size='small' onClick={removeStep(index)}>
                                            Delete
                                </Button>
                                    </AccordionActions>
                                </Accordion>
                            )
                        })}
                        <Accordion
                            className={classes.accordion}
                            expanded={(expanded === -1)}
                            onChange={(event, exp) => handleAccordion(-1, exp)}
                        >
                            <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                                <Grid container justify="space-between" alignItems="center">
                                    <Grid item>
                                        <Typography variant="h6">Tags</Typography>
                                    </Grid>
                                    <Grid item xs={9}>
                                        <Typography variant="caption">Help people find your new post!</Typography>
                                    </Grid>
                                </Grid>
                            </AccordionSummary>
                            <AccordionDetails className={classes.accordiondetails}>
                                <Grid container wrap="nowrap" alignItems="center">
                                    <Grid item={4}>
                                        <TextField
                                            label="Add new tag" value={searchTagFieldValue}
                                            size="small" variant="outlined"
                                            onInputCapture={handleTagField}
                                            onKeyDown={(e) => (e.key === 'Enter') ? addNewTag() : null}
                                            InputProps={{
                                                endAdornment:
                                                    <InputAdornment position="end">
                                                        <IconButton size="small" onClick={addNewTag}>
                                                            <AddCircleIcon />
                                                        </IconButton>
                                                    </InputAdornment>
                                            }}
                                        />
                                    </Grid>
                                    <Divider variant="middle" orientation="vertical" flexItem />
                                    <Grid item xs={8} className={classes.chipArray}>
                                        {searchTags.map((tag, index) => {
                                            if (tags.includes(tag)) return null;
                                            return (
                                                <Chip label={tag} variant="outlined"
                                                    onClick={addTag(index)}
                                                />
                                            )
                                        })}
                                    </Grid>
                                </Grid>
                                <div className={classes.chipArray}>
                                    {tags.map((tag, index) => {
                                        return (<Chip label={tag} onDelete={removeTag(index)} />)
                                    })}
                                </div>

                            </AccordionDetails>
                        </Accordion>
                    </CardContent>
                </Card>
                <Button
                    variant="contained"
                    className={classes.submit}
                    endIcon={<SendIcon />}
                    onClick={submitPost}
                >
                    Create post
                </Button>
            </div>
        </Container>
    )
}