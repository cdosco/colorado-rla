import * as React from 'react';
import AuditReportForm from 'corla/component/AuditReportForm';

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
                {canRenderReport && (<AuditReportForm
                canRenderReport={canRenderReport}
                /> 
                )}
            </div>
        </div>
    );
};

export default Status;
