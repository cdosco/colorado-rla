import * as React from 'react';

import * as _ from 'lodash';

import { Redirect } from 'react-router-dom';

import { History } from 'history';

import standardizeChoices from 'corla/action/dos/standardizeChoices';

import StandardizeChoicesPage from './StandardizeChoicesPage';
import resetAudit from 'corla/action/dos/resetAudit';

import withDOSState from 'corla/component/withDOSState';
import withPoll from 'corla/component/withPoll';

import counties from 'corla/data/counties';

// The next URL path to transition to.
const NEXT_PATH = '/sos/audit/select-contests';

// The previous URL path to transition to.
const PREV_PATH = '/sos/audit';

/**
 * Denormalize the DOS.Contests data structure from the application state into
 * something that can be easily displayed in a tabular format.
 */
const flattenContests = (
    contests: DOS.Contests,
    canonicalChoices: DOS.CanonicalChoices,
): DOS.Form.StandardizeChoices.Row[] => {
    return _.flatMap(contests, (contest: Contest) => {
        return _.map(contest.choices, (choice: ContestChoice, idx) => {
            return {
                choiceIndex: idx,
                choiceName: choice.name,
                choices: canonicalChoices[contest.name],
                contestId: contest.id,
                contestName: contest.name,
                countyName: counties[contest.countyId].name,
            };
        });
    });
};

/**
 * Remove rows with no canonical choices.
 */
const filterRows = (
    rows: DOS.Form.StandardizeChoices.Row[],
): DOS.Form.StandardizeChoices.Row[] => {
    return _.filter(rows, row => {
        if (_.isEmpty(row.choices)) {
            return false;
        }

        return !_.includes(row.choices, row.choiceName);
    });
};

interface Props {
    areChoicesLoaded: boolean;
    asm: DOS.ASMState;
    contests: DOS.Contests;
    canonicalChoices: DOS.CanonicalChoices;
    history: History;
}

const PageContainer = (props: Props) => {
    const {
        areChoicesLoaded,
        asm,
        canonicalChoices,
        contests,
        history,
    } = props;

    const nextPage = (data: DOS.Form.StandardizeChoices.FormData) => {
        standardizeChoices(contests, data).then(function(r) {
            // use the result here
            if (r.ok) {
                history.push(NEXT_PATH);
            }
        })
        .catch(function(reason){
            console.log("standardizeChoices error in submitAction " + reason);
        });
    };

    const previousPage = async() => {
        await resetAudit();
        history.push('/sos/audit');
    };

    if (asm === 'DOS_AUDIT_ONGOING') {
        return <Redirect to='/sos' />;
    }

    let rows: DOS.Form.StandardizeChoices.Row[] = [];

    if (areChoicesLoaded) {
        rows = filterRows(flattenContests(contests, canonicalChoices));

        if (_.isEmpty(rows)) {
            return <Redirect to={ NEXT_PATH } />;
        }
    }

    return <StandardizeChoicesPage areChoicesLoaded={ areChoicesLoaded }
                                   back={ previousPage }
                                   contests={ contests }
                                   rows={ rows }
                                   forward={ nextPage } />;
};

const mapStateToProps = (state: DOS.AppState) => {
    const contests = state.contests;
    const canonicalChoices = state.canonicalChoices;
    const areChoicesLoaded = !_.isEmpty(contests)
        && !_.isEmpty(canonicalChoices)
        && !state.standardizingContests;

    return {
        areChoicesLoaded,
        asm: state.asm,
        canonicalChoices,
        contests,
    };
};

export default withPoll(
    withDOSState(PageContainer),
    'DOS_SELECT_CONTESTS_POLL_START',
    'DOS_SELECT_CONTESTS_POLL_STOP',
    mapStateToProps,
);
