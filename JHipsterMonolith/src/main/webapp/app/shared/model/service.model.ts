import { type ITestimonial } from '@/shared/model/testimonial.model';

import { type ServiceType } from '@/shared/model/enumerations/service-type.model';
export interface IService {
  id?: number;
  name?: string;
  description?: string | null;
  price?: number | null;
  durationInHours?: number | null;
  serviceType?: keyof typeof ServiceType | null;
  testimonial?: ITestimonial | null;
}

export class Service implements IService {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string | null,
    public price?: number | null,
    public durationInHours?: number | null,
    public serviceType?: keyof typeof ServiceType | null,
    public testimonial?: ITestimonial | null,
  ) {}
}
