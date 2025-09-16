import { empty } from 'corla/util';

import action from '.';

type CreateDataFn<S, R> = (sent: S, received: R) => Action.SubmitData<S, R>;

interface CreateSubmitConfig<S, R> {
    createData?: CreateDataFn<S, R>;
    failType: string;
    networkFailType: string;
    okType: string;
    sendType: string;
    url: string;
}

function defaultCreateData<S, R>(sent: S, received: R) {
    return { received, sent };
}

function createSubmitAction<S, R>(config: CreateSubmitConfig<S, R>) {
    const {
        failType,
        networkFailType,
        okType,
        sendType,
        url,
    } = config;

    const createData = config.createData || defaultCreateData;
    async function submitAction(sent: S) {
    
        action(sendType);

        const init: RequestInit = {
            body: JSON.stringify(sent),
            credentials: 'include',
            method: 'post',
        };

        try {
            const r = await fetch(url, init);
    if (url.includes("set-contest-names")) {
        console.log('++++++++++++++  fetching set-contest-name');
    }
            if (!r.ok) {
                const err = await r.json();

                action(failType, err);

                if (r.status === 401) {
                    action('NOT_AUTHORIZED');
                }
                return r;
            }

            const received = await r.json().catch((error) =>{
                console.log('Failed to parse JSON respone:', error)
                return r.text();
            });
            const data = createData(sent, received);

            action(okType, data);
            return r;
        } catch (e) {
           action(networkFailType);
            console.log("createSubmitAction catch(e):" + e);
            throw e;
        } 


    }

    return submitAction;
}

export default createSubmitAction;
