/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Service from './service.vue';
import ServiceService from './service.service';
import AlertService from '@/shared/alert/alert.service';

type ServiceComponentType = InstanceType<typeof Service>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Service Management Component', () => {
    let serviceServiceStub: SinonStubbedInstance<ServiceService>;
    let mountOptions: MountingOptions<ServiceComponentType>['global'];

    beforeEach(() => {
      serviceServiceStub = sinon.createStubInstance<ServiceService>(ServiceService);
      serviceServiceStub.retrieve.resolves({ headers: {} });

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
          serviceService: () => serviceServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        serviceServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Service, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(serviceServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.services[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: ServiceComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Service, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        serviceServiceStub.retrieve.reset();
        serviceServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        serviceServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeService();
        await comp.$nextTick(); // clear components

        // THEN
        expect(serviceServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(serviceServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
