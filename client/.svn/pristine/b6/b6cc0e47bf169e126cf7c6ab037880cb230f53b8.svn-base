import * as moment from 'moment';

export function formatLocalDate(dob: Date): string {
    return moment(dob).format('M/D/Y');
}

export function parseLocalDate(s: string): Date {
    return moment(s, 'M/D/Y').startOf('day').toDate();
}
