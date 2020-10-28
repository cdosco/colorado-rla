import * as _ from 'lodash';

export function format(
    contests: DOS.Contests,
    data: DOS.Form.StandardizeContests.FormData,
): JSON.Standardize[] {
    return _.chain(contests)
        // Keep just the changes
        .filter((contest: Contest) => {
            return !_.isNil(data[contest.id]);
        })
        .map((contest: Contest) => {
            return {
                contestId: contest.id,
                countyId: contest.countyId,
                name: data[contest.id].name,
            };
        })
        .value();
}
