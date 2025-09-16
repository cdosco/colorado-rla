import * as React from 'react';

import { NumericInput } from '@blueprintjs/core';

interface FormProps {
    onChange: (r: number) => void;
    riskLimit: number;
}

interface FormState {
    riskLimitPercent: string;
}

const MIN_RISK_LIMIT = 0.0001;
const MAX_RISK_LIMIT = 1 - MIN_RISK_LIMIT;

function fromPercent(val: number) {
    return val / 100;
}

function toPercent(val: number) {
    return val * 100;
}

class RiskLimitForm extends React.Component<FormProps, FormState> {
    constructor(props: FormProps) {
        super(props);

        this.state = {
            riskLimitPercent: String(toPercent(props.riskLimit)),
        };

        this.onChange = this.onChange.bind(this);
    }

    public render() {
        const { riskLimitPercent } = this.state;

        return (
            <div>
                <label>
                    <div className='mb-default'>Comparison Audits (%)</div>
                    <NumericInput
                        min={ toPercent(MIN_RISK_LIMIT) }
                        max={ toPercent(MAX_RISK_LIMIT) }
                        stepSize={ 1 }
                        value={ riskLimitPercent }
                        onValueChange={ this.onChange } />
                </label>
            </div>
        );
    }

    private onChange(valueAsNumber: number, valueAsString: string) {
        this.setState({ riskLimitPercent: valueAsString });

        this.props.onChange(fromPercent(valueAsNumber));
    }
}

export default RiskLimitForm;
