import { BaseEntity } from './../../shared';

export class Address implements BaseEntity {
    constructor(
        public id?: number,
        public addressLine?: string,
        public addressLine2?: string,
        public neighborhood?: string,
        public city?: string,
        public state?: string,
        public country?: string,
        public zipCode?: string,
    ) {
    }
}
