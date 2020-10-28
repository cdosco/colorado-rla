import * as _ from 'lodash';
import * as React from 'react';
import { connect } from 'react-redux';

import { Button, Card, Intent } from '@blueprintjs/core';

import CVRExportForm from './Form';
import Uploading from './Uploading';

import uploadCvrExport from 'corla/action/county/uploadCvrExport';

import { Spinner } from '@blueprintjs/core';

import deleteFile from 'corla/action/county/deleteFile';
import cvrExportUploadedSelector from 'corla/selector/county/cvrExportUploaded';
import cvrExportUploadingSelector from 'corla/selector/county/cvrExportUploading';

interface UploadedProps {
    deleting: boolean | undefined;
    enableReupload: OnClick;
    file: UploadedFile;
    handleDeleteFile: OnClick;
}

const UploadedCVRExport = (props: UploadedProps) => {
    const { enableReupload, handleDeleteFile, file } = props;

    if (props.deleting) {
        return (
            <Card>
                <Spinner className='pt-large' intent={ Intent.PRIMARY } />
                <div>Deleting file...</div>
            </Card>
        );
    } else {
        return (
            <div>
                <div className='mb-default'>
                    <div className='form-container-heading'><strong>CVR Export</strong></div>
                    <div><strong>File name: </strong>"{ file.fileName }"</div>
                    <div><strong>SHA-256 hash: </strong>{ file.hash }</div>
                </div>
                <div className='error'>
                    <strong>{file.result.success ? '' : 'Error Message: ' }</strong>
                    { file.result.errorMessage }
                </div>
                <div className='error rowNum'>
                    <strong>{file.result.success ? '' : 'Error row number: ' }</strong>
                    { file.result.errorRowNum }
                </div>
                <div className='error rowContent'>
                    <strong>{file.result.success ? '' : 'Error row content: ' }</strong>
                    { file.result.errorRowContent }
                </div>
                <Button intent={ Intent.PRIMARY } onClick={ handleDeleteFile }>
                    Delete File
                </Button>
                <span> &nbsp;&nbsp; </span>
                <Button intent={ Intent.PRIMARY } onClick={ enableReupload }>
                    Re-upload
                </Button>
                <hr />
            </div>
        );
    }
};

interface ContainerProps {
    countyState: County.AppState;
    fileUploaded: boolean;
    uploadingFile: boolean;
}

interface ContainerState {
    form: {
        file?: File;
        hash: string;
    };
    fileDeleted: boolean;
    reupload: boolean;
    uploadClicked: boolean;
}

class CVRExportFormContainer extends React.Component<ContainerProps, ContainerState> {
    public constructor(props: ContainerProps) {
        super(props);

        this.state = {
            fileDeleted: false,
            form: {
                file: undefined,
                hash: '',
            },
            reupload: false,
            uploadClicked: false,
        };
    }

    public componentDidUpdate(prevProps: ContainerProps) {
        // XXX: Paper over the gap between uploading a file and triggering its
        // subsequent import so we don't see the widget flash.
        //
        // The upload/import API separation is sub-optimal, to say the least.
        if (prevProps.uploadingFile && !this.props.uploadingFile) {
            setTimeout(() => {
                this.setState({ uploadClicked: false });
            }, 1000);
        }
    }

    public render() {
        const { countyState, fileUploaded, uploadingFile } = this.props;

        if (this.state.uploadClicked || uploadingFile) {
            return <Uploading countyState={ countyState } />;
        }

        if (fileUploaded && !this.state.reupload && countyState.cvrExport) {
            return (
                // shameless reuse of state variable uploading=deleting
                <UploadedCVRExport enableReupload={ this.enableReupload }
                                   handleDeleteFile={ this.handleDeleteFile }
                                   deleting={ countyState.uploadingCVRExport }
                                   file={ countyState.cvrExport } />
            );
        }

        return (
            <CVRExportForm disableReupload={ this.disableReupload }
                           fileUploaded={ fileUploaded }
                           fileDeleted={ this.state.fileDeleted }
                           form={ this.state.form }
                           onFileChange={ this.onFileChange }
                           onHashChange={ this.onHashChange }
                           upload={ this.upload } />
        );
    }

    private disableReupload = () => {
        this.setState({ reupload: false });
    }

    private enableReupload = () => {
        this.setState({ reupload: true });
    }

    private onFileChange = (e: React.ChangeEvent<any>) => {
        const s = { ...this.state };

        s.form.file = e.target.files[0];

        this.setState(s);
    }

    private onHashChange = (hash: string) => {
        const s = { ...this.state };

        s.form.hash = hash;

        this.setState(s);
    }

    private handleDeleteFile = () => {
        const result = deleteFile('cvr');

        if (result) {
            // clear form
            const s = { ...this.state };
            s.form.hash = '';
            s.form.file = undefined;
            s.fileDeleted = true; // don't show the cancel button momentarily
            this.setState(s);
        }

    }

    private upload = () => {
        const { countyState } = this.props;
        const { file, hash } = this.state.form;

        if (file) {
            this.setState({ uploadClicked: true });

            uploadCvrExport(countyState.id!, file, hash);

            this.disableReupload();
        }
    }
}

const mapStateToProps = (countyState: County.AppState) => {
    const uploadingFile = cvrExportUploadingSelector(countyState);

    return {
        countyState,
        fileUploaded: cvrExportUploadedSelector(countyState),
        uploadingFile,
    };
};

export default connect(mapStateToProps)(CVRExportFormContainer);
