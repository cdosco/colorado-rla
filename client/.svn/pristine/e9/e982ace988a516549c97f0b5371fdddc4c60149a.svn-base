import { endpoint } from 'corla/config';

import { format } from 'corla/adapter/standardizeContests';

import action from 'corla/action';
import createSubmitAction from 'corla/action/createSubmitAction';

const url = endpoint('set-contest-names');

export default (
    contests: DOS.Contests,
    data: DOS.Form.StandardizeContests.FormData,
) => {
    const submitAction = createSubmitAction({
        failType: 'STANDARDIZE_CONTESTS_FOR_AUDIT_FAIL',
        networkFailType: 'STANDARDIZE_CONTESTS_FOR_AUDIT_NETWORK_FAIL',
        okType: 'STANDARDIZE_CONTESTS_FOR_AUDIT_OK',
        sendType: 'STANDARDIZE_CONTESTS_FOR_AUDIT_SEND',
        url,
    });

    action('STANDARDIZE_CONTESTS_FOR_AUDIT');
    return submitAction(format(contests, data));
};
