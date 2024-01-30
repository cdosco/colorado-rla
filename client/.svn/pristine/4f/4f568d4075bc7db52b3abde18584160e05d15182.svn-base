import * as React from 'react';

import { RouteComponentProps } from 'react-router-dom';

import withCountyState from 'corla/component/withCountyState';
import withPoll from 'corla/component/withPoll';

import counties from 'corla/data/counties';

import MissedDeadlinePage from './MissedDeadlinePage';
import CountyDashboardPage from './Page';

import finishAudit from 'corla/action/county/finishAudit';

import allRoundsCompleteSelector from 'corla/selector/county/allRoundsComplete';
import auditCompleteSelector from 'corla/selector/county/auditComplete';
import auditStartedSelector from 'corla/selector/county/auditStarted';
import canAuditSelector from 'corla/selector/county/canAudit';
import canRenderReportSelector from 'corla/selector/county/canRenderReport';
import canSignInSelector from 'corla/selector/county/canSignIn';
import currentRoundNumberSelector from 'corla/selector/county/currentRoundNumber';
import missedDeadlineSelector from 'corla/selector/county/missedDeadline';

interface MatchParams {
    id: string;
}

interface DashboardProps extends RouteComponentProps<MatchParams> {
    allRoundsComplete: boolean;
    auditComplete: boolean;
    auditStarted: boolean;
    canAudit: boolean;
    canRenderReport: boolean;
    canSignIn: boolean;
    countyState: County.AppState;
    currentRoundNumber: number;
    missedDeadline: boolean;
}

class CountyDashboardContainer extends React.Component<DashboardProps> {
    public render() {
        const {
            allRoundsComplete,
            auditStarted,
            canAudit,
            canRenderReport,
            canSignIn,
            countyState,
            history,
            match,
            missedDeadline,
        } = this.props;

        if (!countyState) {
            return <div />;
        }

        if (missedDeadline) {
            return <MissedDeadlinePage />;
        }

        if (!countyState.id) {
            return <div />;
        }

        const boardIndex = parseInt(match.params.id, 10);

        const countyInfo = counties[countyState.id];
        const startAudit = () =>
            history.push('/county/audit/' + boardIndex);

        const props = {
            allRoundsComplete,
            auditStarted,
            canAudit,
            canRenderReport,
            canSignIn,
            countyInfo,
            finishAudit,
            startAudit,
            ...this.props,
        };

        return <CountyDashboardPage { ...props } />;
    }
}

function mapStateToProps(countyState: County.AppState) {
    return {
        allRoundsComplete: allRoundsCompleteSelector(countyState),
        auditComplete: auditCompleteSelector(countyState),
        auditStarted: auditStartedSelector(countyState),
        canAudit: canAuditSelector(countyState),
        canRenderReport: canRenderReportSelector(countyState),
        canSignIn: canSignInSelector(countyState),
        countyState,
        currentRoundNumber: currentRoundNumberSelector(countyState),
        missedDeadline: missedDeadlineSelector(countyState),
    };
}

export default withPoll(
    withCountyState(CountyDashboardContainer),
    'COUNTY_DASHBOARD_POLL_START',
    'COUNTY_DASHBOARD_POLL_STOP',
    mapStateToProps,
);
