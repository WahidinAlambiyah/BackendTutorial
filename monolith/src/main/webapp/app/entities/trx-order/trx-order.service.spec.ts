/* tslint:disable max-line-length */
import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import TrxOrderService from './trx-order.service';
import { DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { TrxOrder } from '@/shared/model/trx-order.model';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  get: sinon.stub(axios, 'get'),
  post: sinon.stub(axios, 'post'),
  put: sinon.stub(axios, 'put'),
  patch: sinon.stub(axios, 'patch'),
  delete: sinon.stub(axios, 'delete'),
};

describe('Service Tests', () => {
  describe('TrxOrder Service', () => {
    let service: TrxOrderService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new TrxOrderService();
      currentDate = new Date();
      elemDefault = new TrxOrder(123, currentDate, currentDate, 'PENDING', 'CASH', 0);
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = Object.assign(
          {
            orderDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
            deliveryDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          },
          elemDefault,
        );
        axiosStub.get.resolves({ data: returnedFromService });

        return service.find(123).then(res => {
          expect(res).toMatchObject(elemDefault);
        });
      });

      it('should not find an element', async () => {
        axiosStub.get.rejects(error);
        return service
          .find(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should create a TrxOrder', async () => {
        const returnedFromService = Object.assign(
          {
            id: 123,
            orderDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
            deliveryDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          },
          elemDefault,
        );
        const expected = Object.assign(
          {
            orderDate: currentDate,
            deliveryDate: currentDate,
          },
          returnedFromService,
        );

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a TrxOrder', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a TrxOrder', async () => {
        const returnedFromService = Object.assign(
          {
            orderDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
            deliveryDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
            orderStatus: 'BBBBBB',
            paymentMethod: 'BBBBBB',
            totalAmount: 1,
          },
          elemDefault,
        );

        const expected = Object.assign(
          {
            orderDate: currentDate,
            deliveryDate: currentDate,
          },
          returnedFromService,
        );
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a TrxOrder', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a TrxOrder', async () => {
        const patchObject = Object.assign(
          {
            orderDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
            deliveryDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
            orderStatus: 'BBBBBB',
            paymentMethod: 'BBBBBB',
          },
          new TrxOrder(),
        );
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            orderDate: currentDate,
            deliveryDate: currentDate,
          },
          returnedFromService,
        );
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a TrxOrder', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of TrxOrder', async () => {
        const returnedFromService = Object.assign(
          {
            orderDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
            deliveryDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
            orderStatus: 'BBBBBB',
            paymentMethod: 'BBBBBB',
            totalAmount: 1,
          },
          elemDefault,
        );
        const expected = Object.assign(
          {
            orderDate: currentDate,
            deliveryDate: currentDate,
          },
          returnedFromService,
        );
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of TrxOrder', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a TrxOrder', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a TrxOrder', async () => {
        axiosStub.delete.rejects(error);

        return service
          .delete(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});
