import * as _ from 'lodash';
import * as React from 'react';

import * as moment from 'moment';

import { History } from 'history';
import { Redirect } from 'react-router-dom';

import StartPage from './StartPage';

import setAuditInfo from 'corla/action/dos/setAuditInfo';
import withDOSState from 'corla/component/withDOSState';
import withSync from 'corla/component/withSync';

const DEFAULT_RISK_LIMIT = 0.05;

interface ContainerProps {
    election: Election;
    history: History;
    publicMeetingDate: Date;
    riskLimit: number;
    dosState: DOS.AppState;
}

interface ContainerState {
    electionDate: Date;
    publicMeetingDate: Date;
    riskLimit: number;
    type: ElectionType;
    uploadedFiles?: string[];
}

class StartPageContainer extends React.Component<ContainerProps, ContainerState> {
    constructor(props: ContainerProps) {
        super(props);

        const defaultElectionDate = moment().toDate();
        const defaultPublicMeetingDate = moment().add(7, 'days').toDate();

        this.state = {
            electionDate: _.get(props, 'election.date', defaultElectionDate),
            publicMeetingDate: _.get(props, 'publicMeetingDate', null) || defaultPublicMeetingDate,
            riskLimit: _.get(props, 'riskLimit', DEFAULT_RISK_LIMIT),
            type: _.get(props, 'election.type', 'general'),
        };

        this.isFormValid = this.isFormValid.bind(this);
        this.setElectionDate = this.setElectionDate.bind(this);
        this.setPublicMeetingDate = this.setPublicMeetingDate.bind(this);
        this.setRiskLimit = this.setRiskLimit.bind(this);
        this.setType = this.setType.bind(this);
        this.setUploadedFiles = this.setUploadedFiles.bind(this);
    }

    public render() {
        const { election, history, publicMeetingDate, riskLimit, dosState } = this.props;

        if (!dosState) {
            return <div />;
        }

        if (!dosState.asm) {
            return <div />;
        }

        if (dosState.asm === 'DOS_AUDIT_ONGOING') {
            return <Redirect to='/sos' />;
        }

        const props = {
            electionDate: this.state.electionDate,
            isFormValid: this.isFormValid(),
            nextPage: () => {
                setAuditInfo({
                    election: {
                        date: this.state.electionDate,
                        type: this.state.type,
                    },
                    publicMeetingDate: this.state.publicMeetingDate,
                    riskLimit: this.state.riskLimit,
                    uploadFiles: this.state.uploadedFiles,
                });

                history.push('/sos/audit/standardize-contests');
            },
            publicMeetingDate: this.state.publicMeetingDate,
            riskLimit: this.state.riskLimit,
            setElectionDate: this.setElectionDate,
            setPublicMeetingDate: this.setPublicMeetingDate,
            setRiskLimit: this.setRiskLimit,
            setType: this.setType,
            setUploadedFiles: this.setUploadedFiles,
            type: this.state.type,
        };

        return <StartPage { ...props } />;
    }

    private isFormValid() {
        return !!(
            this.state.electionDate
                && this.state.publicMeetingDate
                && this.state.riskLimit
                && this.state.type
        );
    }

    private setElectionDate(d: Date) {
        this.setState({ electionDate: d });
    }

    private setPublicMeetingDate(d: Date) {
        this.setState({ publicMeetingDate: d });
    }

    private setRiskLimit(riskLimit: number) {
        this.setState({ riskLimit });
    }

    private setType(type: ElectionType) {
        this.setState({ type });
    }

    private setUploadedFiles(files: string[]) {
        this.setState({ uploadedFiles: files });
    }
}

function mapStateToProps(dosState: DOS.AppState) {
    const { election, publicMeetingDate, riskLimit } = dosState;

    return { election, riskLimit, publicMeetingDate, dosState };
}

export default withSync(
    withDOSState(StartPageContainer),
    'DOS_DEFINE_AUDIT_SYNC',
    mapStateToProps,
);
