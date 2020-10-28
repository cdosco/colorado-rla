import * as React from 'react';

import { Button, Intent } from '@blueprintjs/core';

import fetchReport from 'corla/action/dos/fetchReport';

interface StatusProps {
    auditIsComplete: boolean;
    canRenderReport: boolean;
    currentRound: number;
    finishedCountiesCount: number;
    totalCountiesCount: number;
}

const Status = (props: StatusProps) => {
    const {
        auditIsComplete,
        canRenderReport,
        currentRound,
        finishedCountiesCount,
        totalCountiesCount,
    } = props;

    return (
        <div className='state-dashboard-round'>
            <div>
                { !auditIsComplete && <h4>Round { currentRound } in progress</h4> }
                { auditIsComplete && <h4>Congratulations! The audit is complete.</h4> }
                <span className='state-dashboard-round-summary'>
                    { finishedCountiesCount } of { totalCountiesCount } counties
                    have finished this round.
                </span>
            </div>
            <div>
                <Button large
                        disabled={ !canRenderReport }
                        intent={ Intent.PRIMARY }
                        icon='import'
                        onClick={ fetchReport }>
                    Download audit report
                </Button>
            </div>
        </div>
    );
};

export default Status;
