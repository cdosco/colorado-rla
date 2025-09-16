import * as React from 'react';
import { connect } from 'react-redux';

import EndOfRoundPage from './Page';

import allRoundsCompleteSelector from 'corla/selector/county/allRoundsComplete';
import countyInfoSelector from 'corla/selector/county/countyInfo';
import currentRoundNumberSelector from 'corla/selector/county/currentRoundNumber';
import previousRoundSelector from 'corla/selector/county/previousRound';

interface ContainerProps {
    allRoundsComplete: boolean;
    auditBoardIndex: number;
    countyInfo: CountyInfo;
    currentRoundNumber: number;
    cvrsToAudit: JSON.CVR[];
    election: Election;
    estimatedBallotsToAudit: number;
    previousRound: Round;
}

class EndOfRoundPageContainer extends React.Component<ContainerProps> {
    public render() {
        return <EndOfRoundPage { ...this.props } />;
    }
}

function mapStateToProps(countyState: County.AppState) {
    const auditBoardIndex = countyState.auditBoardIndex || 0;
    const previousRound = previousRoundSelector(countyState);

    return {
        allRoundsComplete: allRoundsCompleteSelector(countyState),
        auditBoardIndex,
        countyInfo: countyInfoSelector(countyState),
        currentRoundNumber: currentRoundNumberSelector(countyState),
        cvrsToAudit: countyState.cvrsToAudit,
        election: countyState.election,
        estimatedBallotsToAudit: countyState.estimatedBallotsToAudit,
        previousRound: previousRound || {},
    };
}

export default connect(mapStateToProps)(EndOfRoundPageContainer);
