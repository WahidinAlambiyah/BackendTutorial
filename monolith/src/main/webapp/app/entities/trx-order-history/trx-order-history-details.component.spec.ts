/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TrxOrderHistoryDetails from './trx-order-history-details.vue';
import TrxOrderHistoryService from './trx-order-history.service';
import AlertService from '@/shared/alert/alert.service';

type TrxOrderHistoryDetailsComponentType = InstanceType<typeof TrxOrderHistoryDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxOrderHistorySample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('TrxOrderHistory Management Detail Component', () => {
    let trxOrderHistoryServiceStub: SinonStubbedInstance<TrxOrderHistoryService>;
    let mountOptions: MountingOptions<TrxOrderHistoryDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      trxOrderHistoryServiceStub = sinon.createStubInstance<TrxOrderHistoryService>(TrxOrderHistoryService);

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
          trxOrderHistoryService: () => trxOrderHistoryServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxOrderHistoryServiceStub.find.resolves(trxOrderHistorySample);
        route = {
          params: {
            trxOrderHistoryId: '' + 123,
          },
        };
        const wrapper = shallowMount(TrxOrderHistoryDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.trxOrderHistory).toMatchObject(trxOrderHistorySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxOrderHistoryServiceStub.find.resolves(trxOrderHistorySample);
        const wrapper = shallowMount(TrxOrderHistoryDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
