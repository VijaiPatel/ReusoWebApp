import { Typography } from '@material-ui/core';
import Button from '@material-ui/core/Button';
import Checkbox from '@material-ui/core/Checkbox';
import Container from '@material-ui/core/Container';
import CssBaseline from '@material-ui/core/CssBaseline';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import IconButton from '@material-ui/core/IconButton';
import InputAdornment from '@material-ui/core/InputAdornment';
import { makeStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import Visibility from '@material-ui/icons/Visibility';
import VisibilityOff from '@material-ui/icons/VisibilityOff';
import axios from 'axios';
import { useFormik } from 'formik';
import React from 'react';
import { Link, useHistory } from 'react-router-dom';
import Logo from '../../Assets/Logo.svg';
import { AuthContext } from './userContext';

const baseUrl = localStorage.getItem("baseApiUrl") || 'http://localhost:8080'
const http = axios.create({
    baseURL: baseUrl,
    validateStatus: false
})

const useStyles = makeStyles((theme) => ({
    paper: {
        marginTop: theme.spacing(8),
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
    },
    submit: {
        margin: theme.spacing(2, 0, 2),
        background: "linear-gradient(45deg, #6fb5ce, #67B26F)",
    },
    form: {
        width: '100%',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center'
    },
    altFlow: {
        display: 'flex',
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between',
        paddingLeft: "10px",
        paddingRight: "10px",
    },
    logo: {
        maxWidth: '200px'
    }
}));

export default function LogIn() {
    const classes = useStyles();
    const userContext = React.useContext(AuthContext);
    let history = useHistory()
    const [passVisible, togglePassVisiblity] = React.useState(false);
    const [badLogin, setBadLogin] = React.useState(false)

    const formik = useFormik({
        initialValues: {
            username: '',
            password: '',
            remembered: false,
        },
        onSubmit: values => {
            let body = {
                username: values.username, password: values.password
            }
            http.post('/users/log-in', body)
            .then(res => {
                if (res.status === 200) {
                    let token = res.headers.authorization.split(' ').pop();
                    sessionStorage.setItem("token", token);
                    console.log(token);
                    if (values.remembered) localStorage.setItem("token", token)
                    userContext.login();
                    history.goBack();
                } else if (res.status === 403) {
                    setBadLogin(true);
                }
            })
            .catch(err => {
                window.location.reload();
                console.log(err);
            })
        },
    });

    return (
        <Container component="main" maxWidth="xs">
            <CssBaseline />
            <div className={classes.paper}>
                <Link to='/'>
                    <img src={Logo} alt="logo.svg" className={classes.logo} />
                </Link>
                <form className={classes.form} onSubmit={formik.handleSubmit}>
                    <TextField
                        margin="normal" variant="outlined" required fullWidth
                        id="username" label="Username" name="username"
                        value={formik.values.username} onChange={formik.handleChange}
                        error={formik.touched.username && formik.errors.username}
                        helperText={formik.errors.username!='Required' && formik.errors.username}
                    />
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
                    <FormControlLabel
                        control={
                            <Checkbox 
                                name='remembered' id='remembered' value={formik.values.remembered}
                                color='default' onChange={(formik.handleChange)} 
                            />
                        }
                        label="Remember me"
                    />
                    <Typography hidden={!badLogin} variant='caption' color='error'>
                        Invalid details, try again.
                    </Typography>
                    <Button
                        fullWidth variant="contained" 
                        className={classes.submit} type='submit'
                    >
                        Sign In
                    </Button>
                    
                    <div className={classes.altFlow}>
                        <Button href="#" color="default" size="medium" disabled>
                            Forgot password?
                        </Button>
                        <Button color="default" size="medium" onClick={()=>history.replace('/signup')}>
                            Sign Up
                        </Button>
                    </div>
                </form>
            </div>
        </Container>
    );
}