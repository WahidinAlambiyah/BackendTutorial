import { type IMstCountry } from '@/shared/model/mst-country.model';

export interface IMstProvince {
  id?: number;
  name?: string;
  unm49Code?: string | null;
  isoAlpha2Code?: string | null;
  country?: IMstCountry | null;
}

export class MstProvince implements IMstProvince {
  constructor(
    public id?: number,
    public name?: string,
    public unm49Code?: string | null,
    public isoAlpha2Code?: string | null,
    public country?: IMstCountry | null,
  ) {}
}
