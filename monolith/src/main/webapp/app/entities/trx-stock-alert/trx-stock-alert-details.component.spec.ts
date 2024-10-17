/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TrxStockAlertDetails from './trx-stock-alert-details.vue';
import TrxStockAlertService from './trx-stock-alert.service';
import AlertService from '@/shared/alert/alert.service';

type TrxStockAlertDetailsComponentType = InstanceType<typeof TrxStockAlertDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxStockAlertSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('TrxStockAlert Management Detail Component', () => {
    let trxStockAlertServiceStub: SinonStubbedInstance<TrxStockAlertService>;
    let mountOptions: MountingOptions<TrxStockAlertDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      trxStockAlertServiceStub = sinon.createStubInstance<TrxStockAlertService>(TrxStockAlertService);

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
          trxStockAlertService: () => trxStockAlertServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxStockAlertServiceStub.find.resolves(trxStockAlertSample);
        route = {
          params: {
            trxStockAlertId: '' + 123,
          },
        };
        const wrapper = shallowMount(TrxStockAlertDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.trxStockAlert).toMatchObject(trxStockAlertSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxStockAlertServiceStub.find.resolves(trxStockAlertSample);
        const wrapper = shallowMount(TrxStockAlertDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
