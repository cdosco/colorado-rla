import { endpoint } from 'corla/config';

export default (id: number) => {
    const params = `fileId=${id}`;
    const url = `${endpoint('download-file')}?${params}`;

    window.location.replace(url);
};
