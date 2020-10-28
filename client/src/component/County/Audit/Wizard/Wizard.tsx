import * as React from 'react';

import BallotAuditStageContainer from './BallotAuditStageContainer';
import BallotListStageContainer from './BallotListStageContainer';
import ReviewStageContainer from './ReviewStageContainer';

type WizardStage = 'ballot-audit' | 'list' | 'review';

interface TransitionTable {
    // We'd want `[stage: WizardStage]`, but TS doesn't allow it.
    [stage: string]: WizardStage;
}

interface WizardProps {
    countyState: County.AppState;
    currentBallotNumber?: number;
    reviewingBallotId?: number;
    totalBallotsForBoard?: number;
}

interface WizardState {
    stage: WizardStage;
}

class CountyAuditWizard extends React.Component<WizardProps, WizardState> {
    constructor(props: WizardProps) {
        super(props);

        if (props.reviewingBallotId != null) {
            this.state = { stage: 'ballot-audit' };

            window.scrollTo(0, 0);
        } else {
            this.state = { stage: 'list' };
        }
    }

    public render() {
        const { nextStage, prevStage } = this;

        const props = {
            countyState: this.props.countyState,
            currentBallotNumber: this.props.currentBallotNumber,
            nextStage,
            prevStage,
            totalBallotsForBoard: this.props.totalBallotsForBoard,
        };

        switch (this.state.stage) {
            case 'ballot-audit':
                return <BallotAuditStageContainer { ...props } />;
            case 'list':
                return <BallotListStageContainer { ...props } />;
            case 'review':
                return <ReviewStageContainer { ...props } />;
        }
    }

    private nextStage = () => {
        // tslint:disable
        const t: TransitionTable = {
            'list': 'ballot-audit',
            'ballot-audit': 'review',
            'review': 'ballot-audit',
        };
        // tslint:enable

        const stage = t[this.state.stage];

        this.setState({ stage });

        window.scrollTo(0, 0);
    }

    private prevStage = () => {
        // tslint:disable
        const t: TransitionTable = {
            'ballot-audit': 'list',
            'review': 'ballot-audit',
        };
        // tslint:enable

        const stage = t[this.state.stage];

        this.setState({ stage });

        window.scrollTo(0, 0);
    }
}

export default CountyAuditWizard;
