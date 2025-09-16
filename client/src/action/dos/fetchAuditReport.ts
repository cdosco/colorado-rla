import { endpoint } from 'corla/config';

export default (reports: string) => {
    debugger;
    const params = `reports=${reports}`;
    const url = `${endpoint('download-audit-report')}?${params}`;

    window.location.replace(url);
};
