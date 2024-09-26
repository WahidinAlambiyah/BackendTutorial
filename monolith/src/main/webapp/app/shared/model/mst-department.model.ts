import { type ILocation } from '@/shared/model/location.model';

export interface IMstDepartment {
  id?: number;
  departmentName?: string;
  location?: ILocation | null;
}

export class MstDepartment implements IMstDepartment {
  constructor(
    public id?: number,
    public departmentName?: string,
    public location?: ILocation | null,
  ) {}
}
