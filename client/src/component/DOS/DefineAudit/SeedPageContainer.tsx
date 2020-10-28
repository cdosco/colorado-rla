import * as React from 'react';
import { Redirect } from 'react-router-dom';

import { History } from 'history';

import withDOSState from 'corla/component/withDOSState';
import withSync from 'corla/component/withSync';
import { formatLocalDate } from 'corla/date';
import SeedPage from './SeedPage';
import resetAudit from 'corla/action/dos/resetAudit';

import uploadRandomSeed from 'corla/action/dos/uploadRandomSeed';

interface ContainerProps {
    dosState: DOS.AppState;
    history: History;
    publicMeetingDate: Date;
    seed: string;
}

class SeedPageContainer extends React.Component<ContainerProps> {

    public render() {
        const { history, publicMeetingDate, seed, dosState } = this.props;

        if (!dosState) {
            return <div />;
        }

        if (!dosState.asm) {
            return <div />;
        }

        if (dosState.asm === 'DOS_AUDIT_ONGOING') {
            return <Redirect to='/sos' />;
        }
        const previousPage = async() => {
			      await resetAudit();
            history.push('/sos/audit');
        };
 
        const props = {
            back: previousPage,
            formattedPublicMeetingDate: formatLocalDate(publicMeetingDate),
            nextPage: () => history.push('/sos/audit/review'),
            seed,
            uploadRandomSeed,
        };

        return <SeedPage { ...props } />;
    }
}

function mapStateToProps(dosState: DOS.AppState) {
    if (!dosState) { return {}; }

    return {
        dosState,
        publicMeetingDate: dosState.publicMeetingDate,
        seed: dosState.seed,
    };
}

export default withSync(
    withDOSState(SeedPageContainer),
    'DOS_DEFINE_AUDIT_RANDOM_SEED_SYNC',
    mapStateToProps,
);
