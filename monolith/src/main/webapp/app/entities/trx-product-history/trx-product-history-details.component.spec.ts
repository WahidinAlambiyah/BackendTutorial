/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TrxProductHistoryDetails from './trx-product-history-details.vue';
import TrxProductHistoryService from './trx-product-history.service';
import AlertService from '@/shared/alert/alert.service';

type TrxProductHistoryDetailsComponentType = InstanceType<typeof TrxProductHistoryDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxProductHistorySample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('TrxProductHistory Management Detail Component', () => {
    let trxProductHistoryServiceStub: SinonStubbedInstance<TrxProductHistoryService>;
    let mountOptions: MountingOptions<TrxProductHistoryDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      trxProductHistoryServiceStub = sinon.createStubInstance<TrxProductHistoryService>(TrxProductHistoryService);

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
          trxProductHistoryService: () => trxProductHistoryServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxProductHistoryServiceStub.find.resolves(trxProductHistorySample);
        route = {
          params: {
            trxProductHistoryId: '' + 123,
          },
        };
        const wrapper = shallowMount(TrxProductHistoryDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.trxProductHistory).toMatchObject(trxProductHistorySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxProductHistoryServiceStub.find.resolves(trxProductHistorySample);
        const wrapper = shallowMount(TrxProductHistoryDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
