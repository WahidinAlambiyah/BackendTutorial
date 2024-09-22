export interface ITestimonial {
  id?: number;
  name?: string;
  feedback?: string;
  rating?: number;
  date?: Date;
}

export class Testimonial implements ITestimonial {
  constructor(
    public id?: number,
    public name?: string,
    public feedback?: string,
    public rating?: number,
    public date?: Date,
  ) {}
}
