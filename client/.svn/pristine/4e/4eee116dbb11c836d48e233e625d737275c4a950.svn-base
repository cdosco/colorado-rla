import * as React from 'react';

import * as _ from 'lodash';

import { Redirect } from 'react-router-dom';

import { History } from 'history';

import standardizeContests from 'corla/action/dos/standardizeContests';

import StandardizeContestsPage from './StandardizeContestsPage';

import withDOSState from 'corla/component/withDOSState';
import withPoll from 'corla/component/withPoll';
import resetAudit from 'corla/action/dos/resetAudit';

import counties from 'corla/data/counties';

// The next URL path to transition to.
const NEXT_PATH = '/sos/audit/standardize-choices';

// The previous URL path to transition to.
const PREV_PATH = '/sos/audit';

const contestsToDisplay = (
    contests: DOS.Contests,
    canonicalContests: DOS.CanonicalContests,
): DOS.Contests => {
    return _.reduce(contests, (acc: DOS.Contests, contest: Contest) => {
        const countyName = counties[contest.countyId].name;
        const countyStandards = canonicalContests[countyName] || [];

        if (!_.isEmpty(countyStandards)
            && !_.includes(countyStandards, contest.name)) {
            acc[contest.id] = contest;
        }

        return acc;
    }, {});
};

interface Props {
    areContestsLoaded: boolean;
    asm: DOS.ASMState;
    contests: DOS.Contests;
    canonicalContests: DOS.CanonicalContests;
    history: History;
}

const StandardizeContestsPageContainer = (props: Props) => {
    const {
        areContestsLoaded,
        asm,
        canonicalContests,
        contests,
        history,
    } = props;

    const nextPage = (data: DOS.Form.StandardizeContests.FormData) => {
        standardizeContests(contests, data).then(function(r) {
            // use the result here
            if (r.ok) {
                history.push(NEXT_PATH);
            }
        })
        .catch(function(reason){
            console.log("standardizeContests error in submitAction " + reason);
        });
    };

    const previousPage = async() => {
        await resetAudit();
        history.push('/sos/audit');
    };

    if (asm === 'DOS_AUDIT_ONGOING') {
        return <Redirect to='/sos' />;
    }

    let filteredContests = {};

    if (areContestsLoaded) {
        filteredContests = contestsToDisplay(contests, canonicalContests);

        if (_.isEmpty(filteredContests)) {
            return <Redirect to={ NEXT_PATH } />;
        }
    }

    return <StandardizeContestsPage areContestsLoaded={ areContestsLoaded }
                                    back={ previousPage }
                                    canonicalContests={ canonicalContests }
                                    contests={ filteredContests }
                                    forward={ nextPage } />;
};

const mapStateToProps = (state: DOS.AppState) => {
    const canonicalContests = state.canonicalContests;
    const contests = state.contests;
    const areContestsLoaded = !_.isEmpty(contests)
        && !_.isEmpty(canonicalContests)
        && !state.settingAuditInfo;

    return {
        areContestsLoaded,
        asm: state.asm,
        canonicalContests,
        contests,
    };
};

export default withPoll(
    withDOSState(StandardizeContestsPageContainer),
    'DOS_SELECT_CONTESTS_POLL_START',
    'DOS_SELECT_CONTESTS_POLL_STOP',
    mapStateToProps,
);
