/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TrxCartDetails from './trx-cart-details.vue';
import TrxCartService from './trx-cart.service';
import AlertService from '@/shared/alert/alert.service';

type TrxCartDetailsComponentType = InstanceType<typeof TrxCartDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxCartSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('TrxCart Management Detail Component', () => {
    let trxCartServiceStub: SinonStubbedInstance<TrxCartService>;
    let mountOptions: MountingOptions<TrxCartDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      trxCartServiceStub = sinon.createStubInstance<TrxCartService>(TrxCartService);

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
          trxCartService: () => trxCartServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxCartServiceStub.find.resolves(trxCartSample);
        route = {
          params: {
            trxCartId: '' + 123,
          },
        };
        const wrapper = shallowMount(TrxCartDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.trxCart).toMatchObject(trxCartSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxCartServiceStub.find.resolves(trxCartSample);
        const wrapper = shallowMount(TrxCartDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
