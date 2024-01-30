import { endpoint } from 'corla/config';

import createSubmitAction from 'corla/action/createSubmitAction';

interface Payload {
    auditBoardIndex: number;
    comment?: string;
    id: number;
    reaudit?: boolean;
}

const url = endpoint('ballot-not-found');

const ballotNotFound = (
    id: number,
    auditBoardIndex: number,
    reAudit = false,
    comment = '',
) => {
    const action = createSubmitAction({
        failType: 'BALLOT_NOT_FOUND_FAIL',
        networkFailType: 'BALLOT_NOT_FOUND_NETWORK_FAIL',
        okType: 'BALLOT_NOT_FOUND_OK',
        sendType: 'BALLOT_NOT_FOUND_SEND',
        url,
    });

    const payload: Payload = {
        auditBoardIndex,
        id,
    };

    if (reAudit) {
        payload.comment = comment;
        payload.reaudit = reAudit;
    }

    action(payload);
};

export default ballotNotFound;
