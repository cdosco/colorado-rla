import * as React from 'react';
import { connect } from 'react-redux';

import Wizard from './Wizard';

import currentBallotNumber from 'corla/selector/county/currentBallotNumber';
import totalBallotsForBoard from 'corla/selector/county/totalBallotsForBoard';

interface WizardContainerStateProps {
    countyState: County.AppState;
    currentBallotNumber?: number;
    totalBallotsForBoard?: number;
}

interface WizardContainerOwnProps {
    reviewingBallotId?: number;
}

interface WizardContainerProps extends
    WizardContainerStateProps, WizardContainerOwnProps {}

const WizardContainer = (props: WizardContainerProps) => {
    return <Wizard { ...props } />;
};

function mapStateToProps(state: County.AppState, props: WizardContainerOwnProps) {
    return {
        countyState: state,
        currentBallotNumber: currentBallotNumber(state),
        reviewingBallotId: props.reviewingBallotId,
        totalBallotsForBoard: totalBallotsForBoard(state),
    };
}

export default connect(mapStateToProps)(WizardContainer);
