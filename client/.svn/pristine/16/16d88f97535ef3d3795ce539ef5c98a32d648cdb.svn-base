import * as React from 'react';

import { Button, Card, EditableText, Intent } from '@blueprintjs/core';

interface FormFieldProps {
    elector: Elector;
    index: number;
    onFirstNameChange: OnClick;
    onLastNameChange: OnClick;
    onTextConfirm: OnClick;
}

const ElectorFormField = (props: FormFieldProps) => {
    const {
        elector,
        index,
        onFirstNameChange,
        onLastNameChange,
        onTextConfirm,
    } = props;

    const { firstName, lastName } = elector;

    return (
        <Card>
            <h3>Audit Board Member #{index + 1}</h3>
            <Card>
                <label>
                    <span>
                        First Name:
                        <EditableText
                            className='pt-input'
                            value={ firstName }
                            onChange={ onFirstNameChange }
                            onConfirm={ onTextConfirm }
                        />
                    </span>
                    <span>
                        Last Name:
                        <EditableText
                            className='pt-input'
                            value={ lastName }
                            onChange={ onLastNameChange }
                            onConfirm={ onTextConfirm }
                        />
                    </span>
                </label>
            </Card>
        </Card>
    );
};

interface FormProps {
    form: Elector[];
    formIsValid: boolean;
    onFirstNameChange: OnClick;
    onLastNameChange: OnClick;
    onTextConfirm: OnClick;
    submit: OnClick;
}

const EndOfRoundForm = (props: FormProps) => {
    const {
        form,
        formIsValid,
        onFirstNameChange,
        onLastNameChange,
        onTextConfirm,
        submit,
    } = props;

    const createFormField = (index: number) => (
        <ElectorFormField
            index={ index }
            elector={ form[index] }
            onFirstNameChange={ onFirstNameChange(index) }
            onLastNameChange={ onLastNameChange(index) }
            onTextConfirm={ onTextConfirm }
        />
    );

    const disableSubmitButton = !formIsValid;

    return (
        <div>
            { createFormField(0) }
            { createFormField(1) }
            <Button intent={ Intent.PRIMARY }
                    disabled={ disableSubmitButton }
                    onClick={ submit }>
                Submit
            </Button>
        </div>
    );
};

export default EndOfRoundForm;
