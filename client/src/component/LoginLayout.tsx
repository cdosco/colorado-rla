import * as React from 'react';

import LicenseFooter from 'corla/component/LicenseFooter';
import Helpdesk from 'corla/component/Helpdesk';

interface Props {
    main: React.ReactNode;
}

const LoginLayout = (props: Props) => {
    return (
        <div className='l-wrapper'>
            <div className='l-main'>
                { props.main }
                <Helpdesk/>
            </div>
            <LicenseFooter />
        </div>
    );
};

export default LoginLayout;
