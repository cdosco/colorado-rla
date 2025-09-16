import * as _ from 'lodash';

export function empty() {
    return {};
}

/**
 * "Natural" sorting with support for numbers within strings.
 *
 * Numbers are sorted numerically rather than lexicographically, so "4" will
 * come before "10".
 */
export function naturalSortBy<T>(
    collection: T[],
    selector: (x: T) => any,
) {
    const intlComparator = new Intl.Collator('en', {
        numeric: true,
    }).compare;

    const comparator = (a: T, b: T): number =>
        intlComparator(selector(a), selector(b));

    // Array.prototype.sort is in-place
    return _.map(collection).sort(comparator);
}
