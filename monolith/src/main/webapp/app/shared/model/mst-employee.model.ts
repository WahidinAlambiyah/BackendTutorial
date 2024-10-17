import { type IMstDepartment } from '@/shared/model/mst-department.model';

export interface IMstEmployee {
  id?: number;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  phoneNumber?: string | null;
  hireDate?: Date | null;
  salary?: number | null;
  commissionPct?: number | null;
  manager?: IMstEmployee | null;
  department?: IMstDepartment | null;
  mstDepartment?: IMstDepartment | null;
}

export class MstEmployee implements IMstEmployee {
  constructor(
    public id?: number,
    public firstName?: string | null,
    public lastName?: string | null,
    public email?: string | null,
    public phoneNumber?: string | null,
    public hireDate?: Date | null,
    public salary?: number | null,
    public commissionPct?: number | null,
    public manager?: IMstEmployee | null,
    public department?: IMstDepartment | null,
    public mstDepartment?: IMstDepartment | null,
  ) {}
}
