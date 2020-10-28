import { endpoint } from 'corla/config';

import { format } from 'corla/adapter/standardizeChoices';

import createSubmitAction from 'corla/action/createSubmitAction';

const url = endpoint('set-contest-names');

export default (
    contests: DOS.Contests,
    data: DOS.Form.StandardizeChoices.FormData,
) => {
    const action = createSubmitAction({
        failType: 'STANDARDIZE_CHOICES_FOR_AUDIT_FAIL',
        networkFailType: 'STANDARDIZE_CHOICES_FOR_AUDIT_NETWORK_FAIL',
        okType: 'STANDARDIZE_CHOICES_FOR_AUDIT_OK',
        sendType: 'STANDARDIZE_CHOICES_FOR_AUDIT_SEND',
        url,
    });

    return action(format(contests, data));
};
