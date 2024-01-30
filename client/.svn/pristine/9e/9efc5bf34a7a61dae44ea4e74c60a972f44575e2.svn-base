import * as React from 'react';
import { connect } from 'react-redux';

import ReviewStage from './ReviewStage';

import uploadAcvr from 'corla/action/county/uploadAcvr';

interface StateProps {
    auditBoardIndex: number;
    comment?: string;
    currentBallot?: County.CurrentBallot;
    isReAuditing?: boolean;
    marks?: County.ACVR;
}

interface OwnProps {
    countyState: County.AppState;
    currentBallotNumber?: number;
    reviewingBallotId?: number;
    totalBallotsForBoard?: number;
    nextStage: OnClick;
    prevStage: OnClick;
}

type Props = StateProps & OwnProps;

const ReviewStageContainer = (props: Props) => {
    const {
        auditBoardIndex,
        comment,
        countyState,
        currentBallot,
        currentBallotNumber,
        isReAuditing,
        marks,
        nextStage,
        prevStage,
        totalBallotsForBoard,
    } = props;

    if (!currentBallot) {
        return null;
    }

    if (!marks) {
        return null;
    }

    return <ReviewStage auditBoardIndex={ auditBoardIndex }
                        comment={ comment }
                        countyState={ countyState }
                        currentBallot={ currentBallot }
                        currentBallotNumber={ currentBallotNumber }
                        isReAuditing={ isReAuditing }
                        marks={ marks }
                        nextStage={ nextStage }
                        prevStage={ prevStage }
                        totalBallotsForBoard={ totalBallotsForBoard }
                        uploadAcvr={ uploadAcvr } />;
};

function mapStateToProps(countyState: County.AppState): StateProps {
    const { currentBallot } = countyState;

    const auditBoardIndex = countyState.auditBoardIndex || 0;
    const comment = countyState.finalReview.comment;
    const marks = currentBallot ? countyState.acvrs[currentBallot.id] : undefined;

    return {
        auditBoardIndex,
        comment,
        currentBallot,
        isReAuditing: !!comment,
        marks,
    };
}

export default connect(mapStateToProps)(ReviewStageContainer);
