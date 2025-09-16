import * as React from 'react';

import { Card, Intent, Spinner } from '@blueprintjs/core';

const WaitingForNextBallot = () => {
    return (
        <div className='rla-page'>
            <h2>Ballot Card Verification</h2>
            <Card>
                <Card>
                    Loading next ballot...
                </Card>
                <Spinner className='pt-large' intent={ Intent.PRIMARY } />
            </Card>
        </div>
    );
};

export default WaitingForNextBallot;
