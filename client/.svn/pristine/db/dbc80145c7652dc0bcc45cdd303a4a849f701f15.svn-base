export default (state: County.AppState, action: Action.App) => {
    const nextState = { ...state };
    const { comment, cvrId } = action.data;

    nextState.finalReview.comment = comment;
    nextState.finalReview.ballotId = cvrId;

    return nextState;
};
