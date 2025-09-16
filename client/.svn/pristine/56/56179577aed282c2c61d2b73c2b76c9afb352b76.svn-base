import * as React from 'react';

import { Link } from 'react-router-dom';

import { Breadcrumb } from '@blueprintjs/core';

import * as _ from 'lodash';

import DOSLayout from 'corla/component/DOSLayout';
import counties from 'corla/data/counties';

const Breadcrumbs = () => (
    <ul className='pt-breadcrumbs mb-default'>
        <li><Breadcrumb text='SoS' href='/sos' /></li>
        <li><Breadcrumb className='pt-breadcrumb-current' text='Contests' /></li>
    </ul>
);

interface RowProps {
    contest: Contest;
}

const ContestTableRow = (props: RowProps) => {
    const { contest } = props;

    const county = counties[contest.countyId];

    return (
        <tr>
            <td>{ county.name }</td>
            <td>
                <Link to={ `/sos/contest/${contest.id}` }>
                    { contest.name }
                </Link>
            </td>
            <td>{ contest.choices.length }</td>
            <td>{ contest.votesAllowed }</td>
        </tr>
    );
};

interface TableProps {
    contests: DOS.Contests;
}

const ContestTable = (props: TableProps) => {
    const { contests } = props;

    const contestRows = _.map(contests, c => (
        <ContestTableRow key={ c.id } contest={ c } />
    ));

    return (
        <table className='pt-html-table pt-html-table-striped rla-table mt-default'>
            <thead>
                <tr>
                    <th>County</th>
                    <th>Name</th>
                    <th>Choices</th>
                    <th>Vote For</th>
                </tr>
            </thead>
            <tbody>
                { contestRows }
            </tbody>
        </table>
    );
};

interface PageProps {
    contests: DOS.Contests;
}

const ContestOverviewPage = (props: PageProps) => {
    const { contests } = props;

    if (!contests) {
        return <div />;
    }

    const main =
        <div>
            <Breadcrumbs />
            <ContestTable contests={ contests } />
        </div>;

    return <DOSLayout main={ main } />;
};

export default ContestOverviewPage;
