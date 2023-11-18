import * as React from 'react';

import { EditableText } from '@blueprintjs/core';

interface FormProps {
    initSeed: string;
    setValid: OnClick; // fn
    updateForm: OnClick; // fn
}

interface FormState {
    seed: string;
}

class SeedForm extends React.Component<FormProps, FormState> {

    constructor(props: FormProps) {
        super(props);
        this.state = { seed: (props.initSeed || '')};
        props.setValid(this.isValid());
    }

    public isValid() {
        return this.state.seed.length >= 20;
    }

    public render() {
        return (
            <label>
               <strong className='block mb-default'> Seed:</strong>
                <EditableText
                    className='pt-input'
                    minWidth={ 200 }
                    value={ this.state.seed }
                    onChange={ this.onSeedChange } />
            </label>
        );
    }

    private onSeedChange = (seed: string) => {
        this.setState({ seed }, () => {
            this.props.updateForm(seed);
            this.props.setValid(this.isValid());
        });
    }
}

export default SeedForm;
