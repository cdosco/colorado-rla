import { endpoint } from 'corla/config';

const url = endpoint('publish-audit-report')
    + '?reportType=audit'
    + '&contentType=zip';

export default () => window.location.replace(url);
