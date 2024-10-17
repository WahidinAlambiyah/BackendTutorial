/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import TrxProductHistory from './trx-product-history.vue';
import TrxProductHistoryService from './trx-product-history.service';
import AlertService from '@/shared/alert/alert.service';

type TrxProductHistoryComponentType = InstanceType<typeof TrxProductHistory>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('TrxProductHistory Management Component', () => {
    let trxProductHistoryServiceStub: SinonStubbedInstance<TrxProductHistoryService>;
    let mountOptions: MountingOptions<TrxProductHistoryComponentType>['global'];

    beforeEach(() => {
      trxProductHistoryServiceStub = sinon.createStubInstance<TrxProductHistoryService>(TrxProductHistoryService);
      trxProductHistoryServiceStub.retrieve.resolves({ headers: {} });

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
          trxProductHistoryService: () => trxProductHistoryServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxProductHistoryServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(TrxProductHistory, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(trxProductHistoryServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.trxProductHistories[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(TrxProductHistory, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(trxProductHistoryServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: TrxProductHistoryComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(TrxProductHistory, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        trxProductHistoryServiceStub.retrieve.reset();
        trxProductHistoryServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        trxProductHistoryServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(trxProductHistoryServiceStub.retrieve.called).toBeTruthy();
        expect(comp.trxProductHistories[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(trxProductHistoryServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        trxProductHistoryServiceStub.retrieve.reset();
        trxProductHistoryServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(trxProductHistoryServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.trxProductHistories[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(trxProductHistoryServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        trxProductHistoryServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeTrxProductHistory();
        await comp.$nextTick(); // clear components

        // THEN
        expect(trxProductHistoryServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(trxProductHistoryServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
