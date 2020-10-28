import * as _ from 'lodash';

function totalBallotsForBoard(state: County.AppState): number | undefined {
    const { auditBoardIndex, ballotSequenceAssignment } = state;

    if (typeof auditBoardIndex !== 'number') { return undefined; }
    if (!ballotSequenceAssignment) { return undefined; }

    const bsa: any = _.nth(ballotSequenceAssignment, auditBoardIndex);

    const { count } = bsa;

    return count;
}

export default totalBallotsForBoard;
