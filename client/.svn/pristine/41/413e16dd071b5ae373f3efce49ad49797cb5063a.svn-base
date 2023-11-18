
import { RouteComponentProps } from 'react-router-dom';

import withDOSState from 'corla/component/withDOSState';
import withSync from 'corla/component/withSync';

import ContestDetailPage from './DetailPage';

interface MatchParams {
    contestId: string;
}

interface OwnProps extends RouteComponentProps<MatchParams> {}

function mapStateToProps(state: DOS.AppState, ownProps: OwnProps) {
    const { contests } = state;
    const contestId = parseInt(ownProps.match.params.contestId, 10);

    const contest = contests[contestId];

    if (!contest) {
        return {};
    }

    return { contest };
}

export default withSync(
    withDOSState(ContestDetailPage),
    'DOS_CONTEST_DETAIL_SYNC',
    mapStateToProps,
);
