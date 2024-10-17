/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TrxDeliveryDetails from './trx-delivery-details.vue';
import TrxDeliveryService from './trx-delivery.service';
import AlertService from '@/shared/alert/alert.service';

type TrxDeliveryDetailsComponentType = InstanceType<typeof TrxDeliveryDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxDeliverySample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('TrxDelivery Management Detail Component', () => {
    let trxDeliveryServiceStub: SinonStubbedInstance<TrxDeliveryService>;
    let mountOptions: MountingOptions<TrxDeliveryDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      trxDeliveryServiceStub = sinon.createStubInstance<TrxDeliveryService>(TrxDeliveryService);

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
          trxDeliveryService: () => trxDeliveryServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxDeliveryServiceStub.find.resolves(trxDeliverySample);
        route = {
          params: {
            trxDeliveryId: '' + 123,
          },
        };
        const wrapper = shallowMount(TrxDeliveryDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.trxDelivery).toMatchObject(trxDeliverySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxDeliveryServiceStub.find.resolves(trxDeliverySample);
        const wrapper = shallowMount(TrxDeliveryDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
