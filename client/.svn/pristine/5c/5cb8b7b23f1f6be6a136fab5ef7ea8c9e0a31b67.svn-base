import withDOSState from 'corla/component/withDOSState';
import withSync from 'corla/component/withSync';

import CountyOverviewPage from './OverviewPage';

function mapStateToProps(dosState: DOS.AppState) {
    const { countyStatus } = dosState;

    return { countyStatus, dosState };
}

export default withSync(
    withDOSState(CountyOverviewPage),
    'DOS_COUNTY_OVERVIEW_SYNC',
    mapStateToProps,
);
