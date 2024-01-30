import * as React from 'react';
import { Link } from 'react-router-dom';

import * as _ from 'lodash';

import { Breadcrumb } from '@blueprintjs/core';

import DOSLayout from 'corla/component/DOSLayout';
import counties from 'corla/data/counties';
import { formatCountyASMState } from 'corla/format';

const Breadcrumbs = () => (
    <ul className='pt-breadcrumbs mb-default'>
        <li><Breadcrumb text='SoS' href='/sos' /></li>
        <li><Breadcrumb className='pt-breadcrumb-current' text='Counties' /></li>
    </ul>
);

interface RowProps {
    county: CountyInfo;
    status: DOS.CountyStatus;
}

const CountyTableRow = (props: RowProps) => {
    const { county, status } = props;

    const countyState = formatCountyASMState(status.asmState);
	
console.log('--------------------------------------------------');
console.log(countyState);	
	
    const submitted = status.auditedBallotCount;

    const auditedCount = _.get(status, 'discrepancyCount.audited') || '—';
    const unauditedCount = _.get(status, 'discrepancyCount.unaudited') || '—';

    return (
        <tr>
            <td className='ellipsize'>
                <Link to={ `/sos/county/${county.id}` }>
                    { county.name }
                </Link>
            </td>
            <td className='ellipsize'>{ countyState }</td>
            <td>{ submitted }</td>
            <td>{ auditedCount }</td>
            <td>{ unauditedCount }</td>
        </tr>
    );
};

interface TableProps {
    countyStatus: DOS.CountyStatuses;
}

const CountyTable = (props: TableProps) => {
    const { countyStatus } = props;

    const countyRows = _
        .chain(counties)
        .filter((c: CountyInfo) => !!countyStatus[c.id])
        .map(c => <CountyTableRow key={ c.id } county={ c } status={ countyStatus[c.id] } />)
        .value();

    return (
        <table className='pt-html-table pt-html-table-striped rla-table mt-default'>
            <thead>
                <tr>
                    <th>Name</th>
                    <th className='status-col'>Status</th>
                    <th># Ballots Submitted</th>
                    <th>Audited Contest Discrepancies</th>
                    <th>Non-audited Contest Discrepancies</th>
                </tr>
            </thead>
            <tbody>{ countyRows }</tbody>
        </table>
    );
};

interface PageProps {
    countyStatus: DOS.CountyStatuses;
}

const CountyOverviewPage = (props: PageProps) => {
    const { countyStatus } = props;

    const main =
        <div>
            <Breadcrumbs />
            <CountyTable countyStatus={ countyStatus } />
        </div>;

    return <DOSLayout main={ main } />;
};

export default CountyOverviewPage;
