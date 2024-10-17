/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import TrxDiscount from './trx-discount.vue';
import TrxDiscountService from './trx-discount.service';
import AlertService from '@/shared/alert/alert.service';

type TrxDiscountComponentType = InstanceType<typeof TrxDiscount>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('TrxDiscount Management Component', () => {
    let trxDiscountServiceStub: SinonStubbedInstance<TrxDiscountService>;
    let mountOptions: MountingOptions<TrxDiscountComponentType>['global'];

    beforeEach(() => {
      trxDiscountServiceStub = sinon.createStubInstance<TrxDiscountService>(TrxDiscountService);
      trxDiscountServiceStub.retrieve.resolves({ headers: {} });

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
          trxDiscountService: () => trxDiscountServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxDiscountServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(TrxDiscount, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(trxDiscountServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.trxDiscounts[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(TrxDiscount, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(trxDiscountServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: TrxDiscountComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(TrxDiscount, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        trxDiscountServiceStub.retrieve.reset();
        trxDiscountServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        trxDiscountServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(trxDiscountServiceStub.retrieve.called).toBeTruthy();
        expect(comp.trxDiscounts[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(trxDiscountServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        trxDiscountServiceStub.retrieve.reset();
        trxDiscountServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(trxDiscountServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.trxDiscounts[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(trxDiscountServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        trxDiscountServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeTrxDiscount();
        await comp.$nextTick(); // clear components

        // THEN
        expect(trxDiscountServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(trxDiscountServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
