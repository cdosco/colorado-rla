import * as React from 'react';
import { RouteComponentProps } from 'react-router-dom';

import * as _ from 'lodash';

import withState from 'corla/component/withState';
import withSync from 'corla/component/withSync';

import AuditBoardPage from './Page';
import SignedInPage from './SignedInPage';

import auditBoardSignedInSelector from 'corla/selector/county/auditBoardSignedIn';
import countyInfoSelector from 'corla/selector/county/countyInfo';
import hasAuditedAnyBallotSelector from 'corla/selector/county/hasAuditedAnyBallot';

interface MatchParams {
    id: string;
}

interface ContainerProps extends RouteComponentProps<MatchParams> {
    auditBoards: AuditBoards;
    countyName: string;
    countyState: County.AppState;
    hasAuditedAnyBallot: boolean;
}

class AuditBoardSignInContainer extends React.Component<ContainerProps> {
    public render() {
        const {
            auditBoards,
            countyName,
            countyState,
            hasAuditedAnyBallot,
            history,
            match,
        } = this.props;

        const boardIndex = parseInt(match.params.id, 10);

        const auditBoardSignedIn = auditBoardSignedInSelector(
            boardIndex,
            countyState,
        );

        const auditBoardStartOrContinue = () =>
            history.push('/county/audit/' + boardIndex);

        if (auditBoardSignedIn) {
            return (
                <SignedInPage auditBoardStatus={ auditBoards[boardIndex] }
                              auditBoardIndex={ boardIndex }
                              auditBoardStartOrContinue={ auditBoardStartOrContinue }
                              countyName={ countyName }
                              hasAuditedAnyBallot={ hasAuditedAnyBallot } />
            );
        }

        return <AuditBoardPage auditBoardIndex={ boardIndex }
                               auditBoardStartOrContinue={ auditBoardStartOrContinue }
                               countyName={ countyName } />;
    }
}

interface SelectProps {
    auditBoards: AuditBoards;
    countyName: string;
    countyState: County.AppState;
    hasAuditedAnyBallot: boolean;
}

function mapStateToProps(countyState: County.AppState): SelectProps {
    const countyInfo = countyInfoSelector(countyState);
    const countyName = _.get(countyInfo, 'name', '');

    return {
        auditBoards: countyState.auditBoards,
        countyName,
        countyState,
        hasAuditedAnyBallot: hasAuditedAnyBallotSelector(countyState),
    };
}

export default withState('County', withSync(
    AuditBoardSignInContainer,
    'COUNTY_BOARD_SIGN_IN_SYNC',
    mapStateToProps,
));
