import { makeStyles } from '@material-ui/core';
import Avatar from '@material-ui/core/Avatar';
import ButtonBase from '@material-ui/core/ButtonBase';
import Fade from '@material-ui/core/Fade';
import GridList from '@material-ui/core/GridList';
import GridListTile from '@material-ui/core/GridListTile';
import GridListTileBar from '@material-ui/core/GridListTileBar';
import { useTheme } from '@material-ui/core/styles';
import useMediaQuery from '@material-ui/core/useMediaQuery';
import PersonIcon from '@material-ui/icons/Person';
import React from 'react';
import { Link } from 'react-router-dom';

const useStyles = makeStyles((theme) => ({
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
    titleBar: {
        background:
            'linear-gradient(to bottom, rgba(0,0,0,0.7) 0%, ' +
            'rgba(0,0,0,0.3) 50%, rgba(0,0,0,0) 100%)',
        titleWrap: {
            title: {
                color: 'black'
            }
        }
    },
    image: {
        objectFit: 'cover'
    },
    avatar: {
        margin: theme.spacing(1),
        height: '30px',
        width: '30px'
    }
}));

export default function PostGrid(props) {
    const classes = useStyles();
    const breaks = useMediaQuery(useTheme().breakpoints.up('sm'));
    const [hover, setHover] = React.useState({});

    const handleHover = (postId, bool) => () => setHover({ ...hover, [postId]: bool });

    
    React.useEffect(() => {
        props.posts.forEach(post => handleHover(post.id, false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
    },[props]);

    return (
        <div className={classes.gridListContainer}>
            <GridList className={classes.gridList} cols={(breaks) ? 3 : 2}>
                {props.posts.map((tile, i) => {
                    return (
                        <GridListTile key={tile.post.id}
                            onMouseOver={handleHover(tile.post.id, true)}
                            onMouseOut={handleHover(tile.post.id, false)}
                            component={Link} to={`/post/${tile.post.id}`}
                        >
                            {<img src={tile.post.image} alt={tile.post.id}/>}
                            {(tile.author) ?
                                <Fade in={hover[`${tile.post.id}`]}>
                                    <GridListTileBar
                                        title={tile.author}
                                        titlePosition="top"
                                        actionIcon={
                                            <ButtonBase
                                                component={Link}
                                                to={`/user/${tile.author}`}
                                            >
                                                <Avatar className={classes.avatar}>
                                                    <PersonIcon />
                                                </Avatar>
                                            </ButtonBase>
                                        }
                                        actionPosition="left"
                                        className={classes.titleBar}
                                    />
                                </Fade>
                                : ''}
                        </GridListTile>
                    )
                })}
            </GridList>
        </div>
    );
}