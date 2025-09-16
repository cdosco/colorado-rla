import * as React from 'react';
import IdleDialog from '../../IdleDialog';

import { Radio, RadioGroup } from '@blueprintjs/core';

import * as format from 'corla/format';

const ELECTION_TYPES: ElectionType[] =
    ['coordinated', 'primary', 'general', 'recall'];

interface FormProps {
    onChange: (t: ElectionType) => void;
    initType: ElectionType;
}

interface FormState {
    type?: ElectionType;
}

class ElectionTypeForm extends React.Component<FormProps, FormState> {
    public state: FormState = {
        type: this.props.initType,
    };

    public render() {
        const { type } = this.state;

        const radios = ELECTION_TYPES.map(ty => {
            const label = format.electionType(ty);

            return <Radio key={ ty } label={ label } value={ ty } />;
        });

        return (
            <div>
                <IdleDialog />
                    <RadioGroup
                        className='rla-radio-group'
                        selectedValue={ type }
                        onChange={ this.onFormChange }
                        label='Election Type'>
                        { radios }
                    </RadioGroup>
            </div>
        );
    }

    private onFormChange = (e: React.ChangeEvent<any>) => {
        const type = e.target.value;

        this.setState({ type });

        this.props.onChange(type);
    }
}

export default ElectionTypeForm;
