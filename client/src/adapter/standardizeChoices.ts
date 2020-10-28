import * as _ from 'lodash';

export function format(
    contests: DOS.Contests,
    data: DOS.Form.StandardizeChoices.FormData,
): JSON.Standardize[] {
    return _.chain(contests)
        // Keep just the changes
        .filter((contest: Contest) => {
            return !_.isNil(data[contest.id]);
        })
        .map((contest: Contest) => {
            return {
                choices: _.map(data[contest.id], (newName, currentName) => {
                    return {
                        newName,
                        oldName: currentName,
                    };
                }),
                contestId: contest.id,
                countyId: contest.countyId,
            };
        })
        .value();
}
