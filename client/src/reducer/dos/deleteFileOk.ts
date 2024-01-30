import * as _ from 'lodash';

export default function deleteFileOk(
    state: DOS.AppState,
    action: Action.DOSDeleteFileOk,
): DOS.AppState {
    const nextState = state;

    if (!action.data.countyId) {
        return nextState;
    }

    const countyId = action.data.countyId;

    switch (action.data.fileType) {
        case 'bmi': {
            delete nextState.countyStatus[countyId].ballotManifest;
            return _.cloneDeep(nextState);
        }
        case 'cvr': {
            delete nextState.countyStatus[countyId].cvrExport;
            return _.cloneDeep(nextState);
        }
        default: {
            return nextState;
        }
    }
}
