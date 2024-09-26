import { type IMstSubDistrict } from '@/shared/model/mst-sub-district.model';

export interface IMstPostalCode {
  id?: number;
  code?: string;
  subDistrict?: IMstSubDistrict | null;
}

export class MstPostalCode implements IMstPostalCode {
  constructor(
    public id?: number,
    public code?: string,
    public subDistrict?: IMstSubDistrict | null,
  ) {}
}
