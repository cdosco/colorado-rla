import { takeLatest } from 'redux-saga/effects';

import fetchCvr from 'corla/action/county/fetchCvr';

function* loadCvr(action: Action.App) {
    const { cvrId } = action.data;

    yield fetchCvr(cvrId);
}

function* reAuditCvrSaga() {
    yield takeLatest('RE_AUDIT_CVR', loadCvr);
}

export default reAuditCvrSaga;
