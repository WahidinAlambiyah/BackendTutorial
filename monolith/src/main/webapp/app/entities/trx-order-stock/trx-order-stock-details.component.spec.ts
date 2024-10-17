/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TrxOrderStockDetails from './trx-order-stock-details.vue';
import TrxOrderStockService from './trx-order-stock.service';
import AlertService from '@/shared/alert/alert.service';

type TrxOrderStockDetailsComponentType = InstanceType<typeof TrxOrderStockDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxOrderStockSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('TrxOrderStock Management Detail Component', () => {
    let trxOrderStockServiceStub: SinonStubbedInstance<TrxOrderStockService>;
    let mountOptions: MountingOptions<TrxOrderStockDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      trxOrderStockServiceStub = sinon.createStubInstance<TrxOrderStockService>(TrxOrderStockService);

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
          trxOrderStockService: () => trxOrderStockServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxOrderStockServiceStub.find.resolves(trxOrderStockSample);
        route = {
          params: {
            trxOrderStockId: '' + 123,
          },
        };
        const wrapper = shallowMount(TrxOrderStockDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.trxOrderStock).toMatchObject(trxOrderStockSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxOrderStockServiceStub.find.resolves(trxOrderStockSample);
        const wrapper = shallowMount(TrxOrderStockDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
