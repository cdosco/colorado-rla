import { merge } from 'lodash';

import { parse } from 'corla/adapter/countyDashboardRefresh';

import { countyState } from 'corla/reducer/defaultState';

export default function dashboardRefreshOk(
    state: County.AppState,
    action: Action.CountyDashboardRefreshOk,
): County.AppState {
    const newState = parse(action.data, state);
    const defaultState = countyState();

    // If it becomes null it will not get overwritten.
    delete state.auditBoardCount;

    const nextState = merge({}, state, newState);

    // We want to overwrite these, not deeply merge, because an empty
    // value indicates a signed-out audit board or that we are between
    // rounds.
    nextState.auditBoards = newState.auditBoards;
    nextState.currentRound = newState.currentRound;

    // Explicitly reset final review state if we get a dashboard refresh, this
    // handles cases where users navigate back to the main county page as well
    // as sending the user back to the final review page when a CVR is
    // uploaded, assuming there are no more ballots to audit.
    nextState.finalReview = defaultState.finalReview;

    return nextState;
}
