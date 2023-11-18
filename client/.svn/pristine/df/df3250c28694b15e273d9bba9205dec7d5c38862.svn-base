import * as React from 'react';
import { connect } from 'react-redux';

import PasswordForm from './PasswordForm';
import SecondFactorForm from './SecondFactorForm';

interface Props {
    dashboard: Dashboard;
    loginChallenge: LoginChallenge;
    username: string;
}

const LoginFormContainer = (props: Props) => {
    const { loginChallenge } = props;

    if (loginChallenge) {
        return <SecondFactorForm { ...props } />;
    } else {
        return <PasswordForm />;
    }
};

function mapStateToProps(state: LoginAppState) {
    const { dashboard, loginChallenge, username } = state;

    return { dashboard, loginChallenge, username };
}

export default connect(mapStateToProps)(LoginFormContainer);
