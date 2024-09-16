/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ServiceDetails from './service-details.vue';
import ServiceService from './service.service';
import AlertService from '@/shared/alert/alert.service';

type ServiceDetailsComponentType = InstanceType<typeof ServiceDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const serviceSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Service Management Detail Component', () => {
    let serviceServiceStub: SinonStubbedInstance<ServiceService>;
    let mountOptions: MountingOptions<ServiceDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      serviceServiceStub = sinon.createStubInstance<ServiceService>(ServiceService);

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
          serviceService: () => serviceServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        serviceServiceStub.find.resolves(serviceSample);
        route = {
          params: {
            serviceId: '' + 123,
          },
        };
        const wrapper = shallowMount(ServiceDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.service).toMatchObject(serviceSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        serviceServiceStub.find.resolves(serviceSample);
        const wrapper = shallowMount(ServiceDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
