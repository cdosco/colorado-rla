import * as test from 'tape';

import * as _ from 'lodash';

import { naturalSortBy } from 'corla/util';

test('naturalSortBy', t => {
    t.plan(1);

    const before = [
        'z11',
        'z2',
        'a 2',
        'a 11',
    ];

    const after = [
        'a 2',
        'a 11',
        'z2',
        'z11',
    ];

    // Non-strict array equality comparison
    t.ok(_.isEqual(naturalSortBy(before, _.identity), after));
});
