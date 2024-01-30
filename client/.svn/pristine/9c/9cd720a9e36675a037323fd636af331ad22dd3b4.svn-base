import * as React from 'react';
import { Redirect } from 'react-router-dom';

import withCountyState from 'corla/component/withCountyState';
import withPoll from 'corla/component/withPoll';

import FinalReviewPageContainer from './EndOfRound/FinalReviewPageContainer';
import EndOfRoundPageContainer from './EndOfRound/PageContainer';
import CountyAuditPage from './Page';

import notice from 'corla/notice';

import allRoundsCompleteSelector from 'corla/selector/county/allRoundsComplete';
import auditCompleteSelector from 'corla/selector/county/auditComplete';
import canAuditSelector from 'corla/selector/county/canAudit';
import isAuditBoardDoneSelector from 'corla/selector/county/isAuditBoardDone';
import previousRoundSelector from 'corla/selector/county/previousRound';
import roundInProgressSelector from 'corla/selector/county/roundInProgress';

function signedOff(auditBoardIndex: number, round: Round): boolean {
    if (!round.signatories) {
        return false;
    }

    if (!round.signatories[auditBoardIndex]) {
        return false;
    }

    if (round.signatories[auditBoardIndex].length < 2) {
        return false;
    }

    return true;
}

interface CountyAuditPageContainerProps {
    auditComplete: boolean;
    canAudit: boolean;
    finalReviewComplete: boolean;
    previousRoundSignedOff: boolean;
    reviewingBallotId?: number;
    roundNumber: number;
    showEndOfRoundPage: boolean;
}

const CountyAuditPageContainer = (props: CountyAuditPageContainerProps) => {
    const {
        auditComplete,
        canAudit,
        finalReviewComplete,
        previousRoundSignedOff,
        reviewingBallotId,
        roundNumber,
        showEndOfRoundPage,
    } = props;

    if (!canAudit) {
        return <Redirect to='/county' />;
    }

    if (auditComplete) {
        notice.ok('The audit is complete.');

        return <Redirect to='/county' />;
    }

    if (previousRoundSignedOff) {
        notice.ok('Congratulations! You have finished auditing your board’s'
            + ` ballots in round ${roundNumber}. Please wait for any other`
            + ' audit boards to complete the audit.');

        return <Redirect to='/county' />;
    }

    if (showEndOfRoundPage && finalReviewComplete) {
        return <EndOfRoundPageContainer />;
    } else if (showEndOfRoundPage && !finalReviewComplete) {
        return <FinalReviewPageContainer />;
    }

    return <CountyAuditPage reviewingBallotId={ reviewingBallotId } />;
};

function mapStateToProps(countyState: County.AppState) {
    const auditBoardIndex = countyState.auditBoardIndex || 0;
    const isAuditBoardDone = isAuditBoardDoneSelector(countyState);
    const finalReviewComplete = countyState.finalReview.complete;
    const previousRound = previousRoundSelector(countyState);
    const reviewingBallotId = countyState.finalReview.ballotId;
    const roundNumber = previousRound ? previousRound.number : 0;
    const showEndOfRoundPage =
        (allRoundsCompleteSelector(countyState)
         || !roundInProgressSelector(countyState)
         || isAuditBoardDone)
        && reviewingBallotId == null;
    const previousRoundSignedOff = previousRound
        && signedOff(auditBoardIndex, previousRound);

    return {
        auditComplete: auditCompleteSelector(countyState),
        canAudit: canAuditSelector(countyState),
        finalReviewComplete,
        previousRoundSignedOff,
        reviewingBallotId,
        roundNumber,
        showEndOfRoundPage,
    };
}

export default withPoll(
    withCountyState(CountyAuditPageContainer),
    'COUNTY_AUDIT_POLL_START',
    'COUNTY_AUDIT_POLL_STOP',
    mapStateToProps,
);
