import { endpoint } from 'corla/config';

import createSubmitAction from 'corla/action/createSubmitAction';

import { format } from 'corla/adapter/uploadAcvr';

const url = endpoint('upload-audit-cvr');

const uploadAcvr = (
    acvr: County.ACVR,
    cvr: CVR,
    auditBoardIndex: number,
    reAudit = false,
    comment = '',
) => {
    const body = format(acvr, cvr, auditBoardIndex, reAudit, comment);

    const action = createSubmitAction({
        failType: 'UPLOAD_ACVR_FAIL',
        networkFailType: 'UPLOAD_ACVR_NETWORK_FAIL',
        okType: 'UPLOAD_ACVR_OK',
        sendType: 'UPLOAD_ACVR_SEND',
        url,
    });

    action(body);
};

export default uploadAcvr;
