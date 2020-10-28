declare namespace JSON {
    interface AuditBoardMember {
        first_name: string;
        last_name: string;
        political_party: string;
    }

    interface AuditBoardStatus {
        members: JSON.AuditBoardMember[];
        sign_in_time: Date;
    }

    interface AuditBoards {
        [index: number]: AuditBoardStatus;
    }

    interface RiskLimit {
        risk_limit: number;
    }

    interface Elector {
        first_name: string;
        last_name: string;
    }

    interface Signatories {
        [index: number]: JSON.Elector[];
    }

    interface ContestInfo {
        choices: string[];
        comment: string;
        contest: number;
        consensus: string;
    }

    interface ACVR {
        audit_cvr: JSON.CVR;
        cvr_id: number;
        reaudit?: boolean;
        comment?: string;
        auditBoardIndex: number;
    }

    interface Round {
        actual_count: number;
        disagreements: number;
        discrepancies: number;
        expected_count: number;
        number: number;
        signatories: Signatories;
        start_audit_prefix_length: number;
        start_index: number;
        start_time: Date;
    }

    interface CountyDashboard {
        asm_state: string;
        audit_board_count?: number;
        audit_boards: JSON.AuditBoards;
        audit_info: any;
        audit_time: string;
        audited_ballot_count: number;
        audited_prefix_length: number;
        ballot_manifest_count: number;
        ballot_manifest_file: any;
        ballot_under_audit_ids: number[];
        ballot_sequence_assignment: object[];
        ballots_remaining_in_round: number;
        current_round: JSON.Round;
        cvr_export_count: number;
        cvr_export_file: any;
        cvr_import_status: CVRImportStatus;
        contests: number[];
        contests_under_audit: number[];
        disagreement_count: number;
        discrepancy_count: number;
        estimated_ballots_to_audit: number;
        general_information: string;
        id: number;
        risk_limit: number;
        rounds: JSON.Round[];
        status: CountyDashboardStatus;
    }

    interface CVRImportStatus {
        error_message?: string;
        import_state: County.CVRImportState;
        timestamp: string;
    }

    interface ContestChoice {
        description: string;
        fictitious: boolean;
        name: string;
        qualified_write_in: boolean;
    }

    interface CVR {
        audited?: boolean;
        audit_board_index?: number;
        ballot_type: string;
        batch_id: number;
        contest_info: JSON.ContestInfo[];
        county_id: number;
        cvr_number: number;
        db_id?: number;
        id: number;
        imprinted_id: string;
        previously_audited?: boolean;
        record_id: number;
        record_type: RecordType;
        scanner_id: number;
        storage_location?: string;
        timestamp: Date;
    }

    interface UploadedFile {
        approximateRecordCount: number;
        countyId: number;
        id: number;
        fileName: string;
        hash: string;
        status: string;
        size: number;
        timestamp: string;
        result: Result;
    }

    interface Result {
        success: boolean;
        importedCount: number;
        errorMessage: string;
        errorRowNum: number;
        errorRowContent: string;
    }

    type UploadFileOk = JSON.UploadedFile;

    type UploadBallotManifestOk = JSON.UploadFileOk;

    type UploadCVRExportOk = JSON.UploadFileOk;

    interface FetchCountyASMStateOk {
        current_state: County.ASMState;
        enabled_ui_events: string[];
    }

    interface FetchDOSASMStateOk {
        current_state: DOS.ASMState;
        enabled_ui_events: string[];
    }

    interface Contest {
        choices: JSON.ContestChoice[];
        county_id: number;
        description: string;
        id: number;
        name: string;
        votes_allowed: number;
    }

    interface ContestForAudit {
        audit: AuditType;
        contest: number;
        reason: string;
    }

    interface Standardize {
        contestId: number;
        countyId: number;
        name?: string;
        choices?: Array<{
            oldName: string;
            newName: string;
        }>;
    }
}
