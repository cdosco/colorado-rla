import * as React from 'react';

import { Button, Callout, Intent, Label } from '@blueprintjs/core';

import login2F from 'corla/action/login2F';

function isFormValid(s: FormState): boolean {
    return s.token.length > 0;
}

function formatChallenge(challenge: Array<[string, string]>): string {
    return challenge.map(box => {
        return '[' + box.join('') + ']';
    }).join(' ');
}

interface FormProps {
    loginChallenge: LoginChallenge;
    username: string;
}

interface FormState {
    token: string;
}

export default class SecondFactorForm extends React.Component<FormProps, FormState> {
    public constructor(props: FormProps) {
        super(props);

        this.state = {
            token: '',
        };

        this.onTokenChange = this.onTokenChange.bind(this);
        this.onSubmit = this.onSubmit.bind(this);
    }

    public render() {
        const { loginChallenge, username } = this.props;
        const disabled = !isFormValid(this.state);
        const challenge = formatChallenge(loginChallenge);

        return (
            <form onSubmit={ this.onSubmit }>
                <p className='mb-default'>
                    Enter a response to the grid challenge
                    {' '} <span className='pt-ui-text-large font-weight-bold'>{ challenge }</span>
                    {' '} for the user
                    {' '} <span className='pt-ui-text-large font-weight-bold'>{ username }</span>.
                </p>
                <Label text='Grid challenge response'>
                    <input className='pt-input pt-large pt-fill'
                           onChange={ this.onTokenChange }
                           type='password'
                           value={ this.state.token || '' } />
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

    private onTokenChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        this.setState({
            token: e.target.value.replace(/\s*/g, ''),
        });
    }

    private onSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        const { token } = this.state;
        const { username } = this.props;
        e.preventDefault();

        login2F(username.toLowerCase(), token.split('').join(' '));
    }
}
