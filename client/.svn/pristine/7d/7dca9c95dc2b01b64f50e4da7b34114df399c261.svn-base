import * as React from 'react';
import { connect } from 'react-redux';

import FinalReviewPage from './FinalReviewPage';

interface Props {
    auditBoardIndex: number;
    ballotSequenceAssignment?: object[];
    cvrsToAudit?: JSON.CVR[];
}

const Container = (props: Props) => {
    return <FinalReviewPage { ...props } />;
};

const mapStateToProps = (state: County.AppState) => {
    return {
        auditBoardIndex: state.auditBoardIndex || 0,
        ballotSequenceAssignment: state.ballotSequenceAssignment,
        cvrsToAudit: state.cvrsToAudit,
    };
};

export default connect(mapStateToProps)(Container);
