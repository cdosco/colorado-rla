import * as React from 'react';

import * as _ from 'lodash';

import { Breadcrumb } from '@blueprintjs/core';

import DOSLayout from 'corla/component/DOSLayout';
import { endpoint } from 'corla/config';
import counties from 'corla/data/counties';

interface BreadcrumbProps {
    contest: Contest;
}

const Breadcrumbs = ({ contest }: BreadcrumbProps) => (
    <ul className='pt-breadcrumbs mb-default'>
        <li><Breadcrumb text='SoS' href='/sos' /></li>
        <li><Breadcrumb text='Contests' href='/sos/contest'/></li>
        <li><Breadcrumb className='pt-breadcrumb-current' text={ contest.name } /></li>
    </ul>
);

interface ChoicesProps {
    contest: Contest;
}

const ContestChoices = (props: ChoicesProps) => {
    const { contest } = props;

    const choiceItems = _.map(contest.choices, (c, k) => (
        <li key={ k }>{ c.name }</li>
    ));

    return (
        <div>
            <h3 className='mt-default'>Choices</h3>
            <ul>{ choiceItems }</ul>
        </div>
    );
};

interface PageProps {
    contest?: Contest;
}

function activityReportUrl(contestName: string) {
    return endpoint('publish-audit-report')
        + '?contestName=' + encodeURIComponent(contestName)
         + '&reportType=activity'
         + '&contentType=xlsx';
}

const ContestDetailPage = (props: PageProps) => {
    const { contest } = props;

    if (!contest) {
        return <div />;
    }

    const row = (k: string, v: (number | string)) => (
        <tr key={ k } >
            <td><strong>{ k }</strong></td>
            <td>{ v }</td>
        </tr>
    );

    const county = counties[contest.countyId];

    const main =
        <div>
            <Breadcrumbs contest={ contest } />
            <h2 className='mt-default'>Contest Report</h2>
            <p>
                The contest report is a contest-centric report detailing
                the ballots that have been audited
                for <b>{ contest.name }</b>, including the county that
                audited each ballot.
            </p>
            <a className='pt-button pt-large pt-intent-primary'
               href={ activityReportUrl(contest.name) }>
               Download Activity Report
            </a>
            <h2 className='mt-default'>Contest Data</h2>
            <table className='pt-html-table'>
                <tbody>
                    { row('County', county.name) }
                    { row('Name', contest.name) }
                    { row('Description', contest.description) }
                    { row('Vote For', contest.votesAllowed) }
                    { row('Ballot Manifest', 'Uploaded') }
                    { row('CVR Export', 'Uploaded') }
                </tbody>
            </table>
            <ContestChoices contest={ contest } />
        </div>;

    return <DOSLayout main={ main } />;
};

export default ContestDetailPage;
