import * as React from 'react';
import { Redirect } from 'react-router-dom';

import { History } from 'history';

import withDOSState from 'corla/component/withDOSState';
import withPoll from 'corla/component/withPoll';

import SelectContestsPage from './SelectContestsPage';

import selectContestsForAudit from 'corla/action/dos/selectContestsForAudit';
import resetAudit from 'corla/action/dos/resetAudit';

interface ContainerProps {
    auditedContests: DOS.AuditedContests;
    contests: DOS.Contests;
    dosState: DOS.AppState;
    history: History;
    isAuditable: OnClick;
}

class SelectContestsPageContainer extends React.Component<ContainerProps> {
    public render() {
        const {
            auditedContests,
            contests,
            dosState,
            history,
            isAuditable,
        } = this.props;

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
            auditedContests,
            back: previousPage,
            contests,
            isAuditable,
            nextPage: () => history.push('/sos/audit/seed'),
            selectContestsForAudit,
        };

        return <SelectContestsPage { ...props } />;
    }
}

function mapStateToProps(dosState: DOS.AppState) {
    const isAuditable = (contestId: number): boolean => {
        if (!dosState.auditTypes) { return false; }

        const t = dosState.auditTypes[contestId];

        return t !== 'HAND_COUNT' && t !== 'NOT_AUDITABLE';
    };

    return {
        auditedContests: dosState.auditedContests,
        contests: dosState.contests,
        dosState,
        isAuditable,
    };
}

export default withPoll(
    withDOSState(SelectContestsPageContainer),
    'DOS_SELECT_CONTESTS_POLL_START',
    'DOS_SELECT_CONTESTS_POLL_STOP',
    mapStateToProps,
);
