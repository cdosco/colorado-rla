import * as React from 'react';
import IdleDialog from '../../../IdleDialog';

import {
    Button,
    EditableText,
    FileInput,
    FormGroup,
    Intent,
} from '@blueprintjs/core';

interface FormProps {
    disableReupload: OnClick;
    fileUploaded: boolean;
    fileDeleted: boolean;
    form: {
        file?: File;
        hash: string;
    };
    onFileChange: OnClick;
    onHashChange: OnClick;
    upload: OnClick;
}

const CVRExportForm = (props: FormProps) => {
    const {
        disableReupload,
        fileUploaded,
        fileDeleted,
        form,
        onFileChange,
        onHashChange,
        upload,
    } = props;

    const { file, hash } = form;

    const fileName = file ? file.name : '';

    const cancelButton = (
        <Button intent={ Intent.WARNING } onClick={ disableReupload }>
            Cancel
        </Button>
    );

    // fileDeleted allows us to not wait for a dashboard refresh to get the asm
    // state, which is what fileUploaded is based on
    // then, we won't show the cancel button momentarily, which looks weird
    const renderedCancelButton = fileUploaded && !fileDeleted
                               ? cancelButton
                               : '';

    return (
        <div>
            <IdleDialog />
            <div style={{ width: '500px' }}>
                <div className='mb-default'>
                    <FormGroup label={
                        <span className='form-group-label pt-ui-text-large font-weight-bold'>
                            CVR Export
                        </span> }>
                        <FileInput fill={ true } text={ fileName } onInputChange={ onFileChange } />
                    </FormGroup>
                </div>
                <FormGroup label={
                    <span className='form-group-label pt-ui-text-large font-weight-bold'>
                        SHA-256 hash for CVR Export
                    </span>
                }>
                    <EditableText className='pt-input'
                                    minWidth={ 600 }
                                    maxLength={ 64 }
                                    value={ hash }
                                    onChange={ onHashChange } />
                </FormGroup>
            </div>
            <div className='form-controls'>
                { renderedCancelButton }
                <Button intent={ Intent.PRIMARY } onClick={ upload }>
                    Upload
                </Button>
            </div>
            <hr />
        </div>
    );
};

export default CVRExportForm;
