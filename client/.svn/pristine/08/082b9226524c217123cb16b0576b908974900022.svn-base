import * as React from 'react';

import { Button, Dialog, FormGroup, Intent, TextArea } from '@blueprintjs/core';

import action from 'corla/action/';

interface FinalReviewDialogProps {
    cvr?: JSON.CVR;
    onClose: () => void;
    isOpen: boolean;
}

interface FinalReviewDialogState {
    comment: string;
    hasError: boolean;
}

class FinalReviewDialog extends React.Component<FinalReviewDialogProps, FinalReviewDialogState> {
    constructor(props: FinalReviewDialogProps) {
        super(props);

        this.state = { comment: '', hasError: true };
    }

    public render() {
        const cvr = this.props.cvr;

        const title = cvr ? 'Re-audit ' + cvr.imprinted_id : 'Re-audit';

        return (
            <Dialog icon='confirm'
                    isOpen={ this.props.isOpen }
                    onClose={ this.handleCancel }
                    title={ title }>
                <div className='pt-dialog-body'>
                    <p>
                        To re-audit this ballot, please explain your reason for
                        re-auditing this ballot in the space below and click
                        "Next". To go back, click "Back".
                    </p>
                    <FormGroup intent={ this.formIntent() }
                               label='Reason for re-audit '
                               labelFor='review-dialog-input'
                               requiredLabel={ true }>
                        <TextArea id='review-dialog-input'
                                  className='pt-fill'
                                  intent={ this.formIntent() }
                                  value={ this.state.comment }
                                  onChange={ this.handleCommentChange } />
                    </FormGroup>
                </div>
                <div className='pt-dialog-footer'>
                    <div className='pt-dialog-footer-actions'>
                        <Button text='Cancel'
                                onClick={ this.handleCancel } />
                        <Button intent={ Intent.PRIMARY }
                                onClick={ this.handleReAudit }
                                text='Next' />
                    </div>
                </div>
            </Dialog>
        );
    }

    private formIntent = (): Intent => {
        return this.state.hasError ? Intent.DANGER : Intent.NONE;
    }

    private handleCancel = () => {
        this.props.onClose();
    }

    private handleCommentChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
        this.setState({
            comment: e.target.value,
            hasError: e.target.value.length < 1,
        });
    }

    private handleReAudit = () => {
        if (this.state.hasError) {
            return;
        }

        const cvr = this.props.cvr;

        if (cvr) {
            action('RE_AUDIT_CVR', {
                comment: this.state.comment,
                cvrId: cvr.db_id,
            });
        }
    }
}

export default FinalReviewDialog;
