import { endpoint } from 'corla/config';

import createSubmitAction from 'corla/action/createSubmitAction';

const url = endpoint('reset-audit');

const resetAudit = createSubmitAction({
    failType: 'RESET_AUDIT_FAIL',
    networkFailType: 'RESET_AUDIT_NETWORK_FAIL',
    okType: 'RESET_AUDIT_OK',
    sendType: 'RESET_AUDIT_SEND',
    url,
});

export default () => resetAudit({});
