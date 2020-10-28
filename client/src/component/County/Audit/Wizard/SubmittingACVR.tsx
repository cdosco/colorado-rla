import * as React from 'react';

import { Card, Intent, Spinner } from '@blueprintjs/core';

const SubmittingACVR = () => {
    return (
        <div className='rla-page'>
            <h2>Ballot Card Review</h2>
            <Card>
                <Card>
                    Submitting ballot card intepretations...
                </Card>
                <Spinner className='pt-large' intent={ Intent.PRIMARY } />
            </Card>
        </div>
    );
};

export default SubmittingACVR;
