import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router-dom';

import * as _ from 'lodash';

import { Menu, MenuDivider, MenuItem } from '@blueprintjs/core';

interface StateProps {
    currentASMState: DOS.ASMState;
}

type Props = RouteComponentProps & StateProps;

const SoSNavMenu = (props: Props) => {
    const { currentASMState, history } = props;

    const disableStates = [
        'AUDIT_READY_TO_START',
        'DOS_AUDIT_ONGOING',
        'DOS_AUDIT_COMPLETE',
        'AUDIT_RESULTS_PUBLISHED',
    ];
    const disableAuditButton = _.includes(disableStates, currentASMState);

    return (
        <Menu>
            <MenuItem icon='home'
                      onClick={ () => history.push('/sos') }
                      text='Home' />
            <MenuDivider />
            <MenuItem icon='map'
                      onClick={ () => history.push('/sos/county') }
                      text='Counties' />
            <MenuItem icon='numbered-list'
                      onClick={ () => history.push('/sos/contest') }
                      text='Contests' />
            <MenuItem disabled={ disableAuditButton }
                      icon='eye-open'
                      onClick={ () => history.push('/sos/audit') }
                      text='Define Audit' />
        </Menu>
    );
};

function mapStateToProps(state: DOS.AppState): StateProps {
    return {
        currentASMState: state.asm,
    };
}

export default connect(mapStateToProps)(withRouter(SoSNavMenu));
