import * as React from 'react';

import { IconName, Menu, MenuDivider, MenuItem } from '@blueprintjs/core';
import { Link } from 'react-router-dom';

interface NavItemProps {
    path: string;
    icon: IconName;
    text: string;
}

const NavItem = (props: NavItemProps) => (
    <Link to={ props.path }>
        <MenuItem icon={ props.icon } text={ props.text } />
    </Link>
);

const NavMenu = () => {
    return (
        <Menu>
            <NavItem
                text='Home'
                path='/'
                icon='home'
            />
            <MenuDivider />
            <NavItem
                text='Audit'
                path='/audit'
                icon='eye-open'
            />
            <NavItem
                text='Report'
                path='/audit/report'
                icon='timeline-bar-chart'
            />
            <NavItem
                text='Round'
                path='/audit/round'
                icon='repeat'
            />
            <NavItem
                text='Seed'
                path='/audit/seed'
                icon='numerical'
            />
            <NavItem
                text='Upload'
                path='/audit/upload'
                icon='folder-close'
            />
        </Menu>
    );
};

export default NavMenu;
