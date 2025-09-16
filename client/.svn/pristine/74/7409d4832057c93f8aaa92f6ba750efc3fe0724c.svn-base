export default function deleteFileOK(state: County.AppState,
                                     action: Action.DeleteFileOK): County.AppState {
    const nextState = state;
    switch (action.data.fileType) {
        // fileType could maybe be an enum

        case 'bmi': {
            nextState.ballotManifestHash = undefined;
            nextState.ballotManifest = undefined;
            return nextState;
        }
        case 'cvr': {
            nextState.cvrExportHash = undefined;
            nextState.cvrExport = undefined;
            return nextState;
        }
        default: {
            return nextState;
        }
    }
}
