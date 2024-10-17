/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstCustomerDetails from './mst-customer-details.vue';
import MstCustomerService from './mst-customer.service';
import AlertService from '@/shared/alert/alert.service';

type MstCustomerDetailsComponentType = InstanceType<typeof MstCustomerDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstCustomerSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('MstCustomer Management Detail Component', () => {
    let mstCustomerServiceStub: SinonStubbedInstance<MstCustomerService>;
    let mountOptions: MountingOptions<MstCustomerDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      mstCustomerServiceStub = sinon.createStubInstance<MstCustomerService>(MstCustomerService);

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
          mstCustomerService: () => mstCustomerServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstCustomerServiceStub.find.resolves(mstCustomerSample);
        route = {
          params: {
            mstCustomerId: '' + 123,
          },
        };
        const wrapper = shallowMount(MstCustomerDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.mstCustomer).toMatchObject(mstCustomerSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstCustomerServiceStub.find.resolves(mstCustomerSample);
        const wrapper = shallowMount(MstCustomerDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
