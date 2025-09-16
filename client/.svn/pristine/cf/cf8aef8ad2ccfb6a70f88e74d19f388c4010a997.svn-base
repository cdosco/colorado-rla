import * as React from 'react';

import { Menu, MenuItem } from '@blueprintjs/core';

import { RouteComponentProps, withRouter } from 'react-router-dom';

const CountyNavMenu = (props: RouteComponentProps) => {
    const { history } = props;

    return (
        <Menu>
            <MenuItem icon='home'
                      onClick={ () => history.push('/county') }
                      text='Home' />
        </Menu>
    );
};

export default withRouter(CountyNavMenu);
