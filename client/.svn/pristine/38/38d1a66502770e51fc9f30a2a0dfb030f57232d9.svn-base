import * as React from 'react';
import IdleDialog from '../../IdleDialog';

import * as _ from 'lodash';

import { Breadcrumb, Button, Card, Intent } from '@blueprintjs/core';

import DOSLayout from 'corla/component/DOSLayout';

import SelectContestsForm from './SelectContestsForm';

const Breadcrumbs = () => (
    <ul className='pt-breadcrumbs mb-default'>
        <li><Breadcrumb href='/sos' text='SoS' />></li>
        <li><Breadcrumb href='/sos/audit' text='Audit Admin' /></li>
        <li><Breadcrumb className='pt-breadcrumb-current' text='Select Contests' /></li>
    </ul>
);

interface WaitingPageProps {
    back: OnClick;
}

const WaitingForContestsPage = ({ back }: WaitingPageProps) => {
    const main =
        <div>
            <Breadcrumb />
            <Card>
                Waiting for counties to upload contest data.
            </Card>
            <Button onClick={ back }
                    className='pt-breadcrumb'>
                Back
            </Button>
            &nbsp;
            <Button disabled
                    intent={ Intent.PRIMARY }
                    className='pt-breadcrumb'>
                Save & Next
            </Button>
        </div>;

    return <DOSLayout main={ main } />;
};

interface PageProps {
    auditedContests: DOS.AuditedContests;
    back: OnClick;
    contests: DOS.Contests;
    isAuditable: OnClick;
    nextPage: OnClick;
    selectContestsForAudit: OnClick;
}

const SelectContestsPage = (props: PageProps) => {
    const {
        auditedContests,
        back,
        contests,
        isAuditable,
        nextPage,
        selectContestsForAudit,
    } = props;

    if (_.isEmpty(contests)) {
        return <WaitingForContestsPage back={ back } />;
    }

    const forms: DOS.Form.SelectContests.Ref = {};

    const haveSelectedContests = !_.isEmpty(auditedContests);

    const onSaveAndNext = () => {
        selectContestsForAudit(forms.selectContestsForm);
        nextPage();
    };

    const main =
        <div>
            <IdleDialog />
            <Breadcrumbs />
            <SelectContestsForm forms={ forms }
                                contests={ contests }
                                auditedContests={auditedContests}
                                isAuditable={ isAuditable } />

            <Button onClick={ back }
                    className='pt-breadcrumb'>
                Back
            </Button>
            &nbsp;
            <Button onClick={ onSaveAndNext }
                    intent={ Intent.PRIMARY }
                    className='pt-breadcrumb'>
                Save & Next
            </Button>
        </div>;

    return <DOSLayout main={ main } />;
};

export default SelectContestsPage;
