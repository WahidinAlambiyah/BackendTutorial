import axios from 'axios';

import { type ITestimonial } from '@/shared/model/testimonial.model';

const baseApiUrl = 'api/testimonials';
const baseSearchApiUrl = 'api/testimonials/_search?query=';

export default class TestimonialService {
  public search(query): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(`${baseSearchApiUrl}${query}`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public find(id: number): Promise<ITestimonial> {
    return new Promise<ITestimonial>((resolve, reject) => {
      axios
        .get(`${baseApiUrl}/${id}`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public retrieve(): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(baseApiUrl)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public delete(id: number): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .delete(`${baseApiUrl}/${id}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public create(entity: ITestimonial): Promise<ITestimonial> {
    return new Promise<ITestimonial>((resolve, reject) => {
      axios
        .post(`${baseApiUrl}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public update(entity: ITestimonial): Promise<ITestimonial> {
    return new Promise<ITestimonial>((resolve, reject) => {
      axios
        .put(`${baseApiUrl}/${entity.id}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public partialUpdate(entity: ITestimonial): Promise<ITestimonial> {
    return new Promise<ITestimonial>((resolve, reject) => {
      axios
        .patch(`${baseApiUrl}/${entity.id}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}
