/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TrxOrderDetails from './trx-order-details.vue';
import TrxOrderService from './trx-order.service';
import AlertService from '@/shared/alert/alert.service';

type TrxOrderDetailsComponentType = InstanceType<typeof TrxOrderDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxOrderSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('TrxOrder Management Detail Component', () => {
    let trxOrderServiceStub: SinonStubbedInstance<TrxOrderService>;
    let mountOptions: MountingOptions<TrxOrderDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      trxOrderServiceStub = sinon.createStubInstance<TrxOrderService>(TrxOrderService);

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
          trxOrderService: () => trxOrderServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxOrderServiceStub.find.resolves(trxOrderSample);
        route = {
          params: {
            trxOrderId: '' + 123,
          },
        };
        const wrapper = shallowMount(TrxOrderDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.trxOrder).toMatchObject(trxOrderSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxOrderServiceStub.find.resolves(trxOrderSample);
        const wrapper = shallowMount(TrxOrderDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
