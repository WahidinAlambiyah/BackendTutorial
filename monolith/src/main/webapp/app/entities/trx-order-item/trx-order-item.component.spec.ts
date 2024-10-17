/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import TrxOrderItem from './trx-order-item.vue';
import TrxOrderItemService from './trx-order-item.service';
import AlertService from '@/shared/alert/alert.service';

type TrxOrderItemComponentType = InstanceType<typeof TrxOrderItem>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('TrxOrderItem Management Component', () => {
    let trxOrderItemServiceStub: SinonStubbedInstance<TrxOrderItemService>;
    let mountOptions: MountingOptions<TrxOrderItemComponentType>['global'];

    beforeEach(() => {
      trxOrderItemServiceStub = sinon.createStubInstance<TrxOrderItemService>(TrxOrderItemService);
      trxOrderItemServiceStub.retrieve.resolves({ headers: {} });

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
          trxOrderItemService: () => trxOrderItemServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxOrderItemServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(TrxOrderItem, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(trxOrderItemServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.trxOrderItems[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(TrxOrderItem, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(trxOrderItemServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: TrxOrderItemComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(TrxOrderItem, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        trxOrderItemServiceStub.retrieve.reset();
        trxOrderItemServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        trxOrderItemServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(trxOrderItemServiceStub.retrieve.called).toBeTruthy();
        expect(comp.trxOrderItems[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(trxOrderItemServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        trxOrderItemServiceStub.retrieve.reset();
        trxOrderItemServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(trxOrderItemServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.trxOrderItems[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(trxOrderItemServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        trxOrderItemServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeTrxOrderItem();
        await comp.$nextTick(); // clear components

        // THEN
        expect(trxOrderItemServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(trxOrderItemServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
