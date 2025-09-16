import * as React from 'react';
import { match } from 'react-router-dom';

import withDOSState from 'corla/component/withDOSState';
import withPoll from 'corla/component/withPoll';

import counties from 'corla/data/counties';

import CountyDetailPage from './DetailPage';

interface ContainerProps {
    countyStatus: DOS.CountyStatuses;
    match: match<any>;
}

class CountyDetailContainer extends React.Component<ContainerProps> {

    public render() {
        const { countyStatus } = this.props;

        const { countyId } = this.props.match.params;
        const county = counties[countyId];

        if (!county) {
            return <div />;
        }

        const status = countyStatus[countyId];

        if (!status) {
            return <div />;
        }

        return <CountyDetailPage county={ county } status={ status } />;
    }
}

function mapStateToProps(dosState: DOS.AppState) {
    const { countyStatus } = dosState;

    return { countyStatus };
}

export default withPoll(
    withDOSState(CountyDetailContainer),
    'DOS_DASHBOARD_POLL_START',
    'DOS_DASHBOARD_POLL_STOP',
    mapStateToProps,
);
