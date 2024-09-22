/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ServiceUpdate from './service-update.vue';
import ServiceService from './service.service';
import AlertService from '@/shared/alert/alert.service';

import TestimonialService from '@/entities/testimonial/testimonial.service';

type ServiceUpdateComponentType = InstanceType<typeof ServiceUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const serviceSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ServiceUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Service Management Update Component', () => {
    let comp: ServiceUpdateComponentType;
    let serviceServiceStub: SinonStubbedInstance<ServiceService>;

    beforeEach(() => {
      route = {};
      serviceServiceStub = sinon.createStubInstance<ServiceService>(ServiceService);
      serviceServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          serviceService: () => serviceServiceStub,
          testimonialService: () =>
            sinon.createStubInstance<TestimonialService>(TestimonialService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(ServiceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.service = serviceSample;
        serviceServiceStub.update.resolves(serviceSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(serviceServiceStub.update.calledWith(serviceSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        serviceServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ServiceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.service = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(serviceServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        serviceServiceStub.find.resolves(serviceSample);
        serviceServiceStub.retrieve.resolves([serviceSample]);

        // WHEN
        route = {
          params: {
            serviceId: '' + serviceSample.id,
          },
        };
        const wrapper = shallowMount(ServiceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.service).toMatchObject(serviceSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        serviceServiceStub.find.resolves(serviceSample);
        const wrapper = shallowMount(ServiceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
