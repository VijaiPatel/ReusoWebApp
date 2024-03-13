import { makeStyles } from '@material-ui/core';
import Button from '@material-ui/core/Button';
import Container from '@material-ui/core/Container';
import CssBaseline from '@material-ui/core/CssBaseline';
import Grid from '@material-ui/core/Grid';
import IconButton from '@material-ui/core/IconButton';
import InputAdornment from '@material-ui/core/InputAdornment';
import TextField from '@material-ui/core/TextField';
import Visibility from '@material-ui/icons/Visibility';
import VisibilityOff from '@material-ui/icons/VisibilityOff';
import axios from 'axios';
import { useFormik } from 'formik';
import React from 'react';
import { Link, useHistory } from 'react-router-dom';
import * as Yup from 'yup';
import Logo from '../../Assets/Logo.svg';
import { AuthContext } from './userContext';

const baseUrl = localStorage.getItem("baseApiUrl") || 'http://localhost:8080'
const http = axios.create({
    baseURL: baseUrl,
    validateStatus: false
})

const minPassLen = 6;
const passLengthMsg = `Your password must be at least ${minPassLen} characters long.`
const usernameRequirements = 'Your username must be between 3-16 characters, and only contain letters, numbers, dots, hyphens - or underscores _';
const emailExists = "This email address is already in use."
const usernameExists = "This username is already in use."

const useStyles = makeStyles((theme) => ({
    paper: {
        marginTop: theme.spacing(8),
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
    },
    logo: {
        maxWidth: '200px'
    },
    form: {
        width: '100%', // Fix IE 11 issue.
        marginTop: theme.spacing(3),
    },
    submit: {
        margin: theme.spacing(3, 0, 2),
        background: "linear-gradient(45deg, #6fb5ce, #67B26F)",
    },
}));

const SignUpSchema = Yup.object().shape({
    email: Yup
        .string().required('Required')
        .email('Invalid email')
        .test('isEmailAvailable', emailExists, async function(value) {
            if (!value) return true; 
            return http.get(`/users/verify/email?q=${value}`)
            .then(res => {
                if (res.status === 200) return true;
                if (res.status === 409) return false;
            }).catch(err => console.log(err))
        }),
    username: Yup
        .string().required('Required')
        .matches(/^[a-z0-9._-]{3,16}$/gi, {
            message: usernameRequirements, 
            excludeEmptyString: true})
        .test('isUsernameAvailable', usernameExists, async function(value) {
            if (!value) return true;
            return http.get(`/users/verify/username?q=${value}`)
            .then(res => {
                if (res.status === 200) return true;
                if (res.status === 409) return false;
            }).catch(err => console.log(err))
        }),
    password: Yup.string().required('Required').min(minPassLen, passLengthMsg)
})

export default function SignUp() {
    const classes = useStyles();
    const userContext = React.useContext(AuthContext);
    const [passVisible, togglePassVisiblity] = React.useState(false);
    let history = useHistory();
    if (userContext.isLoggedIn) history.push('/')

    const formik = useFormik({
        initialValues: {
            firstName: '',
            lastName: '',
            email: '',
            username: '',
            password: '',
        },
        validationSchema: SignUpSchema,
        onSubmit: values => {
            http.post('/users/sign-up', values)
            .then(res => {
                if (res.status === 201) {
                    login(values.username, values.password);
                } else if (res.status === 400) {
                    
                }
            })
            .catch(err => {
                window.location.reload();
                console.log(err);
            })
        },
    });

    function login(username, password) {
        http.post('/users/log-in', {
            username: username, password: password
        })
        .then(res => {
            if (res.status === 200) {
                let token = res.headers.authorization.split(' ').pop();
                sessionStorage.setItem("token", token);
                userContext.login();
            }
            history.push('/');
        }).catch(err => {
            window.location.reload();
            console.log(err)
        })
    }

    return (
        <Container component="main" maxWidth="xs">
            <CssBaseline />
            <div className={classes.paper}>
                <Link to='/'>
                    <img src={Logo} alt="logo.svg" className={classes.logo} />
                </Link>
                <form className={classes.form} noValidate onSubmit={formik.handleSubmit}>
                    <Grid container spacing={1}>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                margin="normal" variant="outlined" fullWidth //required
                                id="firstName" label="First name" name="firstName"
                                value={formik.values.firstName} onChange={formik.handleChange}
                                //error={}
                                //helperText={}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                margin="normal" variant="outlined" fullWidth //required
                                id="lastName" label="Last name" name="lastName"
                                value={formik.values.lastName} onChange={formik.handleChange}
                                //error={}
                                //helperText={}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                margin="normal" variant="outlined" required fullWidth autoFocus
                                id="email" label="Email Address" name="email"
                                value={formik.values.email} onChange={formik.handleChange}
                                error={formik.touched.email && formik.errors.email}
                                helperText={formik.errors.email!='Required' && formik.errors.email}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                margin="normal" variant="outlined" required fullWidth
                                id="username" label="Username" name="username"
                                value={formik.values.username} onChange={formik.handleChange}
                                error={formik.touched.username && formik.errors.username}
                                helperText={formik.errors.username!='Required' && formik.errors.username}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                margin="normal" variant="outlined" required fullWidth
                                name="password" label="Password" id="password"
                                value={formik.values.password} onChange={formik.handleChange}
                                error={formik.touched.password && formik.errors.password}
                                helperText={formik.errors.password!='Required' && formik.errors.password}
                                type={passVisible ? "text" : "password"}
                                InputProps={{
                                    endAdornment: <InputAdornment position="end">
                                        <IconButton onClick={()=>togglePassVisiblity(!passVisible)}>
                                            {passVisible ? <Visibility /> : <VisibilityOff />}
                                        </IconButton>
                                    </InputAdornment>
                                }}
                            />
                        </Grid>
                    </Grid>
                    <Button fullWidth variant="contained" className={classes.submit} type="submit">
                    Sign Up
                    </Button>
                </form>
                <Button color="default" size="medium" onClick={() => history.replace('/login')}>
                    Already have an account? Sign in here
                </Button>
            </div>
        </Container>
    );
}