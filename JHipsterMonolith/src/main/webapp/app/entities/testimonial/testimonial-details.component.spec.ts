/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TestimonialDetails from './testimonial-details.vue';
import TestimonialService from './testimonial.service';
import AlertService from '@/shared/alert/alert.service';

type TestimonialDetailsComponentType = InstanceType<typeof TestimonialDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const testimonialSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Testimonial Management Detail Component', () => {
    let testimonialServiceStub: SinonStubbedInstance<TestimonialService>;
    let mountOptions: MountingOptions<TestimonialDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      testimonialServiceStub = sinon.createStubInstance<TestimonialService>(TestimonialService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          testimonialService: () => testimonialServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        testimonialServiceStub.find.resolves(testimonialSample);
        route = {
          params: {
            testimonialId: '' + 123,
          },
        };
        const wrapper = shallowMount(TestimonialDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.testimonial).toMatchObject(testimonialSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        testimonialServiceStub.find.resolves(testimonialSample);
        const wrapper = shallowMount(TestimonialDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
