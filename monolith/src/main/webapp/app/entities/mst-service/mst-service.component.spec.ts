/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import MstService from './mst-service.vue';
import MstServiceService from './mst-service.service';
import AlertService from '@/shared/alert/alert.service';

type MstServiceComponentType = InstanceType<typeof MstService>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('MstService Management Component', () => {
    let mstServiceServiceStub: SinonStubbedInstance<MstServiceService>;
    let mountOptions: MountingOptions<MstServiceComponentType>['global'];

    beforeEach(() => {
      mstServiceServiceStub = sinon.createStubInstance<MstServiceService>(MstServiceService);
      mstServiceServiceStub.retrieve.resolves({ headers: {} });

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
          mstServiceService: () => mstServiceServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstServiceServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(MstService, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(mstServiceServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.mstServices[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: MstServiceComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(MstService, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        mstServiceServiceStub.retrieve.reset();
        mstServiceServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        mstServiceServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeMstService();
        await comp.$nextTick(); // clear components

        // THEN
        expect(mstServiceServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(mstServiceServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
