import { forEach, merge } from 'lodash';

function createEmptyAcvr(cvr: CVR): County.ACVR {
    const acvr: any = {};

    forEach(cvr.contestInfo, c => {
        acvr[c.contest] = {
            choices: {},
            comments: '',
        };
    });

    return acvr;
}

const parse = (data: JSON.CVR, state: AppState): County.CurrentBallot => ({
    ballotType: data.ballot_type,
    batchId: data.batch_id,
    contestInfo: data.contest_info,
    countyId: data.county_id,
    cvrNumber: data.cvr_number,
    id: data.id,
    imprintedId: data.imprinted_id,
    recordId: data.record_id,
    recordType: data.record_type,
    scannerId: data.scanner_id,
    submitted: false,
});

const fetchCvrOk = (
    state: County.AppState,
    action: Action.CountyFetchCvrOk,
): County.AppState => {
    const nextState = merge({}, state);

    const currentBallot = parse(action.data, state);
    nextState.currentBallot = currentBallot;

    // Always overwrite the corresponding ACVR after a new CVR is fetched. We do
    // not want an audit board to be influenced by a previous interpretation.
    nextState.acvrs[currentBallot.id] = createEmptyAcvr(currentBallot);

    return nextState;
};

export default fetchCvrOk;
