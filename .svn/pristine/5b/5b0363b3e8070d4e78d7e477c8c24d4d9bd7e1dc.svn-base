import * as React from 'react';

import Nav from '../Nav';


const Breadcrumb = () => (
    <ul className='pt-breadcrumbs'>
        <li>
            <a className='pt-breadcrumb' href='/sos'>
                SoS
            </a>
        </li>
        <li>
            <a className='pt-breadcrumb' href='/sos/audit'>
                Audit Admin
            </a>
        </li>
        <li>
            <a className='pt-breadcrumb pt-breadcrumb-current'>
                Review
            </a>
        </li>
    </ul>
);

interface AuditReviewProps {
    back: OnClick;
    publishBallotsToAudit: OnClick;
    saveAndDone: OnClick;
    dosState: DOS.AppState;
}

const AuditReview = (props: AuditReviewProps) => {
    const { back, publishBallotsToAudit, saveAndDone, dosState } = props;

    const launch = () => {
        publishBallotsToAudit();
        saveAndDone();
    };

    const riskLimitPercent = dosState.riskLimit! * 100;

    const disableLaunchButton = !dosState.seed;

    return (
        <div>
            <Nav />
            <Breadcrumb />
            <h2>Audit</h2>
            <h3>Audit Definition Review</h3>
            <div>
                This is the set of audit data which will be used to define the list of
                ballots to audit for each county. Once this is submitted, it will be released
                to the counties and the previous pages will not be editable.
                In particular, you will not be able to change which contests are under audit.
            </div>
            <div className='pt-card'>
                <table className='pt-table'>
                    <tbody>
                        <tr>
                            <td><strong>Risk Limit:</strong></td>
                            <td>{ riskLimitPercent }%</td>
                        </tr>
                        <tr>
                            <td><strong>Random Number Generator Seed:</strong></td>
                            <td>{ dosState.seed }</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div>
                <button onClick={ back } className='pt-button pt-intent-primary pt-breadcrumb'>
                    Back
                </button>
                <button disabled={ disableLaunchButton }
                        onClick={ launch }
                        className='pt-button pt-intent-primary pt-breadcrumb'>
                    Launch Audit
                </button>
            </div>
        </div>
    );
};


export default AuditReview;
