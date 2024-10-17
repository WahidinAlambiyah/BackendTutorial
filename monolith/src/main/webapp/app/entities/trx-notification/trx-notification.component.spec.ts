/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import TrxNotification from './trx-notification.vue';
import TrxNotificationService from './trx-notification.service';
import AlertService from '@/shared/alert/alert.service';

type TrxNotificationComponentType = InstanceType<typeof TrxNotification>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('TrxNotification Management Component', () => {
    let trxNotificationServiceStub: SinonStubbedInstance<TrxNotificationService>;
    let mountOptions: MountingOptions<TrxNotificationComponentType>['global'];

    beforeEach(() => {
      trxNotificationServiceStub = sinon.createStubInstance<TrxNotificationService>(TrxNotificationService);
      trxNotificationServiceStub.retrieve.resolves({ headers: {} });

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
          trxNotificationService: () => trxNotificationServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxNotificationServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(TrxNotification, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(trxNotificationServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.trxNotifications[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(TrxNotification, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(trxNotificationServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: TrxNotificationComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(TrxNotification, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        trxNotificationServiceStub.retrieve.reset();
        trxNotificationServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        trxNotificationServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(trxNotificationServiceStub.retrieve.called).toBeTruthy();
        expect(comp.trxNotifications[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(trxNotificationServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        trxNotificationServiceStub.retrieve.reset();
        trxNotificationServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(trxNotificationServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.trxNotifications[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(trxNotificationServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        trxNotificationServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeTrxNotification();
        await comp.$nextTick(); // clear components

        // THEN
        expect(trxNotificationServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(trxNotificationServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
