import { endpoint } from 'corla/config';
import { empty } from 'corla/util';

const appInfoUrl = endpoint('app-info');

async function getAppInfo() {
    const init: RequestInit = {
        credentials: 'include',
        method: 'get',
    };

    try {
        const r = await fetch(appInfoUrl, init);

        const received = await r.json().catch(empty);

        if (!r.ok) {
            return {};
        }

        return received ;

    } catch (e) {
        return {exception: e};
    }
}

export default getAppInfo;
