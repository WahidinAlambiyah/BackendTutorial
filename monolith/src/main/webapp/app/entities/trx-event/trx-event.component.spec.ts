/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import TrxEvent from './trx-event.vue';
import TrxEventService from './trx-event.service';
import AlertService from '@/shared/alert/alert.service';

type TrxEventComponentType = InstanceType<typeof TrxEvent>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('TrxEvent Management Component', () => {
    let trxEventServiceStub: SinonStubbedInstance<TrxEventService>;
    let mountOptions: MountingOptions<TrxEventComponentType>['global'];

    beforeEach(() => {
      trxEventServiceStub = sinon.createStubInstance<TrxEventService>(TrxEventService);
      trxEventServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          trxEventService: () => trxEventServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxEventServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(TrxEvent, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(trxEventServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.trxEvents[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: TrxEventComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(TrxEvent, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        trxEventServiceStub.retrieve.reset();
        trxEventServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        trxEventServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeTrxEvent();
        await comp.$nextTick(); // clear components

        // THEN
        expect(trxEventServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(trxEventServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
