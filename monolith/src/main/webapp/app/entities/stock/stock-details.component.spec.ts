/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import StockDetails from './stock-details.vue';
import StockService from './stock.service';
import AlertService from '@/shared/alert/alert.service';

type StockDetailsComponentType = InstanceType<typeof StockDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const stockSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Stock Management Detail Component', () => {
    let stockServiceStub: SinonStubbedInstance<StockService>;
    let mountOptions: MountingOptions<StockDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      stockServiceStub = sinon.createStubInstance<StockService>(StockService);

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
          stockService: () => stockServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        stockServiceStub.find.resolves(stockSample);
        route = {
          params: {
            stockId: '' + 123,
          },
        };
        const wrapper = shallowMount(StockDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.stock).toMatchObject(stockSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        stockServiceStub.find.resolves(stockSample);
        const wrapper = shallowMount(StockDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
