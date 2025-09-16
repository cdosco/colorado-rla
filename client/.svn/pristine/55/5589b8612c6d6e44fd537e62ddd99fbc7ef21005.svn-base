import * as React from 'react';
import { connect } from 'react-redux';

import { Button, Intent, NumericInput } from '@blueprintjs/core';

import setNumberOfAuditBoards from 'corla/action/county/setNumberOfAuditBoards';

/**
 * Minimum number of audit boards that can participate.
 */
const MIN_AUDIT_BOARDS = 1;

interface AuditBoardNumberSelectorProps {
    auditBoardCount: number;
    numberOfBallotsToAudit?: number;
    isShown: boolean;
    isEnabled: boolean;
    setNumberOfAuditBoards: (x: { auditBoardCount: number }) => void;
}

interface AuditBoardNumberSelectorState {
    auditBoardCount: number;
    isEnabled: boolean;
    enterTask: boolean;
}

class AuditBoardNumberSelector
    extends React.Component<AuditBoardNumberSelectorProps,
                            AuditBoardNumberSelectorState> {
    constructor(props: AuditBoardNumberSelectorProps) {
        super(props);

        this.state = {
          auditBoardCount: props.auditBoardCount,
          isEnabled: props.isEnabled,
          enterTask: false,
        };

        this.handleChangeAuditBoards = this.handleChangeAuditBoards.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    public componentWillReceiveProps(nextProps: AuditBoardNumberSelectorProps) {
        if (!nextProps.isEnabled) {
            this.setState({ auditBoardCount: nextProps.auditBoardCount,
                            isEnabled: false });
        }
    }

    public render() {
        const { isShown, numberOfBallotsToAudit } = this.props;
        const { isEnabled } = this.state;

        if (!isShown) {
            return null;
        }

        return (
            <div className='mt-default'>
                <div className="container">
                    <div className="confirmation-text">
                    Please confirm you’d like to use { this.state.auditBoardCount } audit boards for this round of the audit. Remember, once the number is confirmed you may not change it until another round begins.
                    </div>
                    <div className="button-container">
                    <button 
                        className="cancel-button" 
                        onClick={() => this.handleGoBack()}>
                        go back
                    </button>
                    <button 
                        className="confirmation-button"
                        onClick={() => this.handleConfirmationBox()}>
                        confirm
                        </button>
                    </div>
                </div>
                <div 
                    className="confirm-bg">
                    onClick={() => this.handleConfirmationBox()}
                </div>

                <form>
                    <div className='pt-form-group'>
                        <label className='pt-label pt-ui-text-large font-weight-bold'
                               htmlFor='number-of-audit-boards-input'>
                            How many audit boards will be auditing?
                        </label>
                        <div className='pt-control-group'>
                            <NumericInput id='number-of-audit-boards-input'
                                          min={ MIN_AUDIT_BOARDS }
                                          value={ this.state.auditBoardCount }
                                          onValueChange={ this.handleChangeAuditBoards }
                                          disabled={ !isEnabled } />
                            <Button disabled={ !isEnabled } intent={ Intent.PRIMARY } type='button' onClick={ () => this.handleConfirmationBox()} >Enter</Button>
                        </div>
                        { this.helperText(numberOfBallotsToAudit) }
                    </div>
                </form>
            </div>
        );
    }

    private handleConfirmationBox() {
        if (!this.state.enterTask) {
             this.handleConfirm();
        } else {
            this.handleGoBack();
            this.handleSubmit();
        }

    }

    private handleConfirm() {

        var bgItems:any = document.getElementsByClassName('confirm-bg');
        for (let i = 0; i < bgItems.length; i++) {
            let element = bgItems[i];
            element.style.display = 'flex';
        }
 
        var containerItems:any = document.getElementsByClassName('container');
        for (let i = 0; i < containerItems.length; i++) {
            let element = containerItems[i];
            element.style.display = 'flex';
        }
       this.setState({ enterTask: true })
 
    }
     
    private  handleGoBack() {
        var bgItems:any = document.getElementsByClassName('confirm-bg');
        for (let i = 0; i < bgItems.length; i++) {
            let element = bgItems[i];
            element.style.display = 'none';
        }
 
        var containerItems:any = document.getElementsByClassName('container');
        for (let i = 0; i < containerItems.length; i++) {
            let element = containerItems[i];
            element.style.display = 'none';
        }

        this.setState({ enterTask: false })
    }
    
    private handleChangeAuditBoards(asNumber: number, asString: string) {
        if (asNumber >= 1) {
          this.setState({ auditBoardCount: asNumber });
        }
    }

   // private handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    private handleSubmit() {
 
      //  e.preventDefault();

        const { auditBoardCount } = this.state;

        this.setState({ isEnabled: false });
        this.props.setNumberOfAuditBoards({ auditBoardCount });
    }

    private helperText(toAudit?: number) {
        if (!toAudit) {
            return null;
        }

        if (1 === toAudit) {
            return (
                <div className='pt-form-helper-text'>
                    There is <b>1</b> ballot card to audit in this round.
                </div>
            );
        }

        return (
            <div className='pt-form-helper-text'>
                There are <b>{ toAudit }</b> ballot cards to audit in this
                round.
            </div>
        );
    }
}

const mapDispatchToProps = () => {
    return { setNumberOfAuditBoards };
};

export default connect(null, mapDispatchToProps)(AuditBoardNumberSelector);
