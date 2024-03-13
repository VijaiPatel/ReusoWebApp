import AppBar from '@material-ui/core/AppBar';
import Button from '@material-ui/core/Button';
import Container from '@material-ui/core/Container';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogTitle from '@material-ui/core/DialogTitle';
import Divider from '@material-ui/core/Divider';
import Fade from '@material-ui/core/Fade';
import Grid from '@material-ui/core/Grid';
import IconButton from '@material-ui/core/IconButton';
import InputBase from '@material-ui/core/InputBase';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import Paper from '@material-ui/core/Paper';
import { makeStyles, useTheme } from '@material-ui/core/styles';
import Toolbar from '@material-ui/core/Toolbar';
import useMediaQuery from '@material-ui/core/useMediaQuery';
import AccountCircleIcon from '@material-ui/icons/AccountCircle';
import ClearIcon from '@material-ui/icons/Clear';
import PostAddIcon from '@material-ui/icons/PostAdd';
import SearchIcon from '@material-ui/icons/Search';
import React, { useContext, useEffect, useState } from 'react';
import { Link, useHistory } from 'react-router-dom';
import ReusoIcon from '../../Assets/Icon.svg';
import ReusoLogo from '../../Assets/Logo.svg';
import { AuthContext } from '../Auth/userContext';

const useStyles = makeStyles((theme) => ({
    root: {
        flexGrow: 1,
        display: 'flex',
        justifyContent: 'center',
        justifyItems: 'center'
        //maxHeight: "100%"
    },
    input: {
        padding: '2px 4px',
        display: 'flex',
        alignItems: 'center',
        marginLeft: theme.spacing(2)
    },
    iconBox: {
        width: 'fit-content'
    },
    logoBtn: {
        marginRight: theme.spacing(2),
        marginLeft: theme.spacing(2)
    },
    logo: {
        maxHeight: "40px",
    },
    header: {
        background: "linear-gradient(45deg, #6fb5ce, #67B26F)",
    },
    divider: {
        height: 28,
        margin: 4,
    },
}));

export default function ButtonAppBar(props) {
    let history = useHistory();
    const classes = useStyles();
    const xs = useMediaQuery(useTheme().breakpoints.only('xs'));

    const userContext = useContext(AuthContext);
    const [searchField , setSearchField] = useState('');
    useEffect(() => props.onSearchChange(searchField), [searchField]);

    const handleSearchField = (event) => setSearchField(event.target.value);
    const clickClearBtn = () => {
        setSearchField(''); 
        setSearchActive(false);
    }

    const [searchActive, setSearchActive] = React.useState(false);
    const handleSearchVisiblity = () => setSearchActive(!searchActive)

    const [anchorEl, setAnchorEl] = React.useState(null);
    const handleClick = (event) => setAnchorEl(event.currentTarget);
    const handleClose = () => setAnchorEl(null);

    const [logoutDialogOpen, setLogoutDialogOpen] = React.useState(false);
    const handleLogoutDialogOpen = () => setLogoutDialogOpen(true);
    const handleLogoutDialogClose = () => setLogoutDialogOpen(false);

    const menuOptLogout = () => handleLogoutDialogOpen();
    const menuOptProfile = () => {
        handleClose();
        history.push('/user')
    }

    const logout = () => {
        userContext.logout();
        sessionStorage.removeItem("token");
        localStorage.removeItem("token")
        if (!window.location.pathname || 
            window.location.pathname === '/') {
            window.location.reload();
        } else {
            history.push("/");
            window.location.reload();
        }
    }
    const goToLogin = () => history.push("/login")
/*
const testCtx = () => {
    userContext.isLoggedIn ? userContext.logout() : userContext.login();
}
<Button onClick={testCtx}>
{userContext.isLoggedIn ? 'logout' : 'login'}
</Button>
*/
    return (
        <div className={classes.root}>
            <AppBar position="static" className={classes.header}>
                <Toolbar>
                <Container maxWidth='sm'>
                <Grid container alignItems='center' justify='space-between' wrap='nowrap'>
                    <Grid item>
                        <Link to="/">
                            <img src={(xs)?ReusoIcon:ReusoLogo} alt="logo.svg" className={classes.logo} />
                        </Link>
                    </Grid>
                    
                    <Grid item hidden={!searchActive}>
                        <Fade in={searchActive}>
                        <Paper component="form" className={classes.input}>
                            <SearchIcon fontSize='large' style={{padding: '5px'}} />
                            <InputBase
                                value={searchField}
                                onChange={handleSearchField}
                                placeholder="Search"
                            />
                            <Divider className={classes.divider} orientation="vertical" />
                            <IconButton size='small' onClick={clickClearBtn}>
                                <ClearIcon fontSize='small' />
                            </IconButton>
                        </Paper>
                        </Fade>
                    </Grid>
                    
                    <Grid item container justify='flex-end' className={classes.iconBox}>
                        <Fade in={!searchActive}>
                        <Grid item hidden={searchActive}>
                            <IconButton onClick={handleSearchVisiblity}>
                                <SearchIcon />
                            </IconButton>
                        </Grid>
                        </Fade>
                        <Grid item hidden={!userContext.isLoggedIn}>
                            <IconButton onClick={() => history.push('/create')}>
                                <PostAddIcon />
                            </IconButton>
                        </Grid>
                        <Grid item>
                        <IconButton 
                            onClick={userContext.isLoggedIn ?
                                handleClick :
                                goToLogin}
                        >
                            <AccountCircleIcon />
                        </IconButton>
                        </Grid>
                        <Menu
                            id="simple-menu"
                            anchorEl={anchorEl}
                            keepMounted
                            open={Boolean(anchorEl)}
                            onClose={handleClose}
                        >
                            <MenuItem onClick={menuOptProfile}>Profile</MenuItem>
                            <MenuItem onClick={menuOptLogout}>Logout</MenuItem>
                        </Menu>
                    </Grid>
                </Grid>
                </Container>
                </Toolbar>
            </AppBar>
            <Dialog
                open={logoutDialogOpen}
                onClose={handleLogoutDialogClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">Are you sure you want to log out?</DialogTitle>
                <DialogActions>
                    <Button onClick={handleLogoutDialogClose} color="primary" autoFocus>
                        Go back
                    </Button>
                    <Button onClick={logout} color="primary">
                        Log out
                    </Button>
                </DialogActions>
          </Dialog>
        </div>
    );
}