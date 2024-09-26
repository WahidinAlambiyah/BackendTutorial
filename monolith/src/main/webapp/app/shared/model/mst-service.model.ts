import { type ITrxTestimonial } from '@/shared/model/trx-testimonial.model';

import { type ServiceType } from '@/shared/model/enumerations/service-type.model';
export interface IMstService {
  id?: number;
  name?: string;
  description?: string | null;
  price?: number | null;
  durationInHours?: number | null;
  serviceType?: keyof typeof ServiceType | null;
  testimonial?: ITrxTestimonial | null;
}

export class MstService implements IMstService {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string | null,
    public price?: number | null,
    public durationInHours?: number | null,
    public serviceType?: keyof typeof ServiceType | null,
    public testimonial?: ITrxTestimonial | null,
  ) {}
}
