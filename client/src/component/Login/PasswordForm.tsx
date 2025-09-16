import * as React from 'react';

import { Button, Callout, Intent, Label } from '@blueprintjs/core';

import login1F from 'corla/action/login1F';

function isFormValid(s: FormState): boolean {
    const { username, password } = s;

    return username.length > 0;
}

interface FormState {
    password: string;
    username: string;
}

export default class PasswordForm extends React.Component<{}, FormState> {
    public constructor(props: {}) {
        super(props);

        this.state = {
            password: '',
            username: '',
        };

        this.onEmailChange = this.onEmailChange.bind(this);
        this.onPasswordChange = this.onPasswordChange.bind(this);
        this.onSubmit = this.onSubmit.bind(this);
    }

    public render() {
        const { username, password } = this.state;
        const disabled = !isFormValid(this.state);

        return (
            <form onSubmit={ this.onSubmit }>
                <Label text='User ID'>
                    <input className='pt-input pt-large pt-fill'
                           onChange={ this.onEmailChange }
                           type='text'
                           value={ username } />
                </Label>
                <Label text='Password'>
                    <input className='pt-input pt-large pt-fill'
                           onChange={ this.onPasswordChange }
                           type='password'
                           value={ password } />
                </Label>
                <Callout className='rla-callout-default'>
                    <Button className='submit'
                            disabled={ disabled }
                            fill
                            intent={ Intent.PRIMARY }
                            large
                            type='submit'>
                        Log in
                    </Button>
                </Callout>
            </form>
        );
    }

    private onEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        this.setState({
            username: e.target.value,
        });
    }

    private onPasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        this.setState({
            password: e.target.value,
        });
    }

    private onSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        const { username, password } = this.state;
        e.preventDefault();

        login1F(username.toLowerCase(), password);
    }
}
