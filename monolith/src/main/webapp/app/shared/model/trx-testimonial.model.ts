export interface ITrxTestimonial {
  id?: number;
  name?: string;
  feedback?: string;
  rating?: number;
  date?: Date;
}

export class TrxTestimonial implements ITrxTestimonial {
  constructor(
    public id?: number,
    public name?: string,
    public feedback?: string,
    public rating?: number,
    public date?: Date,
  ) {}
}
