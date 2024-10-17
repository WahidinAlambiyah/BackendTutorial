/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import TrxCoupon from './trx-coupon.vue';
import TrxCouponService from './trx-coupon.service';
import AlertService from '@/shared/alert/alert.service';

type TrxCouponComponentType = InstanceType<typeof TrxCoupon>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('TrxCoupon Management Component', () => {
    let trxCouponServiceStub: SinonStubbedInstance<TrxCouponService>;
    let mountOptions: MountingOptions<TrxCouponComponentType>['global'];

    beforeEach(() => {
      trxCouponServiceStub = sinon.createStubInstance<TrxCouponService>(TrxCouponService);
      trxCouponServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          jhiItemCount: true,
          bPagination: true,
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'jhi-sort-indicator': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          trxCouponService: () => trxCouponServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxCouponServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(TrxCoupon, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(trxCouponServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.trxCoupons[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(TrxCoupon, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(trxCouponServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: TrxCouponComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(TrxCoupon, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        trxCouponServiceStub.retrieve.reset();
        trxCouponServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        trxCouponServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(trxCouponServiceStub.retrieve.called).toBeTruthy();
        expect(comp.trxCoupons[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(trxCouponServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        trxCouponServiceStub.retrieve.reset();
        trxCouponServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(trxCouponServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.trxCoupons[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(trxCouponServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        trxCouponServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeTrxCoupon();
        await comp.$nextTick(); // clear components

        // THEN
        expect(trxCouponServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(trxCouponServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
