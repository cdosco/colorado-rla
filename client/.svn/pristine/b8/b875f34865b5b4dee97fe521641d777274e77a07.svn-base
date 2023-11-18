import * as React from 'react';

import { Callout } from '@blueprintjs/core';

import CountyLayout from 'corla/component/CountyLayout';
import * as config from 'corla/config';

const MissedDeadlinePage = () => {
    const main =
        <Callout icon='info-sign'
                 title='Upload deadline missed'>
            You are unable to upload a file because the deadline has passed and the
            audit has begun. Please contact the CDOS voting systems team at&nbsp;
            <strong>{ config.helpEmail }</strong> or <strong>{ config.helpTel }</strong> for assistance.
        </Callout>;

    return <CountyLayout main={ main } />;
};

export default MissedDeadlinePage;
