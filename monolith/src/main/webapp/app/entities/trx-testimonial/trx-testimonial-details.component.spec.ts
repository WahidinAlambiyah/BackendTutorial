/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TrxTestimonialDetails from './trx-testimonial-details.vue';
import TrxTestimonialService from './trx-testimonial.service';
import AlertService from '@/shared/alert/alert.service';

type TrxTestimonialDetailsComponentType = InstanceType<typeof TrxTestimonialDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxTestimonialSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('TrxTestimonial Management Detail Component', () => {
    let trxTestimonialServiceStub: SinonStubbedInstance<TrxTestimonialService>;
    let mountOptions: MountingOptions<TrxTestimonialDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      trxTestimonialServiceStub = sinon.createStubInstance<TrxTestimonialService>(TrxTestimonialService);

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
          trxTestimonialService: () => trxTestimonialServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxTestimonialServiceStub.find.resolves(trxTestimonialSample);
        route = {
          params: {
            trxTestimonialId: '' + 123,
          },
        };
        const wrapper = shallowMount(TrxTestimonialDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.trxTestimonial).toMatchObject(trxTestimonialSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxTestimonialServiceStub.find.resolves(trxTestimonialSample);
        const wrapper = shallowMount(TrxTestimonialDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
