import { type IMstJob } from '@/shared/model/mst-job.model';
import { type IMstDepartment } from '@/shared/model/mst-department.model';
import { type IMstEmployee } from '@/shared/model/mst-employee.model';

import { type Language } from '@/shared/model/enumerations/language.model';
export interface IJobHistory {
  id?: number;
  startDate?: Date | null;
  endDate?: Date | null;
  language?: keyof typeof Language | null;
  job?: IMstJob | null;
  department?: IMstDepartment | null;
  employee?: IMstEmployee | null;
}

export class JobHistory implements IJobHistory {
  constructor(
    public id?: number,
    public startDate?: Date | null,
    public endDate?: Date | null,
    public language?: keyof typeof Language | null,
    public job?: IMstJob | null,
    public department?: IMstDepartment | null,
    public employee?: IMstEmployee | null,
  ) {}
}
