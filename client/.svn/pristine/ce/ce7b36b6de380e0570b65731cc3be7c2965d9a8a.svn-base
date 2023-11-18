import * as React from 'react';
import { connect } from 'react-redux';

import { Redirect } from 'react-router-dom';

import session from 'corla/session';

interface RootRedirectContainerProps {
    stateType: AppStateType;
}

export class RootRedirectContainer extends React.Component<RootRedirectContainerProps> {
    public render() {
        const { stateType } = this.props;

        const s = session.get();

        if (s) {
            const { type } = s;

            if (type === 'county' && stateType === 'County') {
                return <Redirect to='/county' />;
            }

            if (type === 'dos' && stateType === 'DOS') {
                return <Redirect to='/sos' />;
            }

            session.expire();
        }

        return <Redirect to='/login' />;
    }
}

function mapStateToProps(state: AppState) {
    return { stateType: state.type };
}

export default connect(mapStateToProps)(RootRedirectContainer);
