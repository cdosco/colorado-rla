import * as _ from 'lodash';

// TODO: Consider another home for this
//
// Project an audit-board-specific view of the overall CVRs to audit.
export const auditBoardSlice = (
    cvrsToAudit: JSON.CVR[] | undefined,
    ballotSequenceAssignment: object[] | undefined,
    auditBoardIndex: number | undefined,
) => {
    const bsa: any = _.nth(ballotSequenceAssignment, auditBoardIndex);

    if (!bsa) {
      return [];
    }

    const { index, count } = bsa;

    return _.slice(cvrsToAudit, index, index + count);
};

function currentBallotNumber(state: County.AppState): number | undefined {
    const { auditBoardIndex,
            ballotSequenceAssignment,
            cvrsToAudit,
            currentBallot } = state;

    if (typeof auditBoardIndex !== 'number') { return undefined; }
    if (!ballotSequenceAssignment) { return undefined; }
    if (!cvrsToAudit) { return undefined; }
    if (!currentBallot) { return undefined; }

    const slice = auditBoardSlice(
        cvrsToAudit,
        ballotSequenceAssignment,
        auditBoardIndex,
    );

    return 1 + _.findIndex(slice, cvr => {
        return cvr.db_id === currentBallot.id;
    });
}

export default currentBallotNumber;
