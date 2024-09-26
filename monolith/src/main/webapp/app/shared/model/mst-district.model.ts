import { type IMstCity } from '@/shared/model/mst-city.model';

export interface IMstDistrict {
  id?: number;
  name?: string;
  unm49Code?: string | null;
  isoAlpha2Code?: string | null;
  city?: IMstCity | null;
}

export class MstDistrict implements IMstDistrict {
  constructor(
    public id?: number,
    public name?: string,
    public unm49Code?: string | null,
    public isoAlpha2Code?: string | null,
    public city?: IMstCity | null,
  ) {}
}
