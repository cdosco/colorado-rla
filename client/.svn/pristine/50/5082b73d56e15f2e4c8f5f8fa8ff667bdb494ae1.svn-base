import * as React from 'react';
import { connect } from 'react-redux';

import { Card } from '@blueprintjs/core';

import BallotManifestFormContainer from './BallotManifest/FormContainer';
import CVRExportFormContainer from './CVRExport/FormContainer';

interface FileUploadFormsProps {
    countyState: County.AppState;
}

const FileUploadForms = (props: FileUploadFormsProps) => {
    const { countyState } = props;

    if (!countyState) { return null; }

    return (
        <div>
            <BallotManifestFormContainer />
            <CVRExportFormContainer />
        </div>
    );
};

const MissedDeadline = () => {
    return (
        <Card>
            The Risk-Limiting Audit has already begun.
            Please contact the Department of State for assistance.
        </Card>
    );
};

interface FileUploadContainerProps {
    countyState: County.AppState;
    missedDeadline: boolean;
}

class FileUploadContainer extends React.Component<FileUploadContainerProps> {
    public render() {
        const { countyState, missedDeadline } = this.props;

        if (missedDeadline) {
            return <MissedDeadline />;
        }

        return (
            <FileUploadForms countyState={ countyState } />
        );
    }
}

function mapStateToProps(countyState: County.AppState) {
    const { asm } = countyState;

    const missedDeadline = asm.county === 'DEADLINE_MISSED';

    return { countyState, missedDeadline };
}

export default connect(mapStateToProps)(FileUploadContainer);
