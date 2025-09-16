function canRenderReport(state: County.AppState): boolean {
    const countyAsmState = state.asm.county;

    return (countyAsmState === 'COUNTY_AUDIT_UNDERWAY'
        || countyAsmState === 'COUNTY_AUDIT_COMPLETE')
        && (typeof state.auditBoardCount !== 'number');
}

export default canRenderReport;
