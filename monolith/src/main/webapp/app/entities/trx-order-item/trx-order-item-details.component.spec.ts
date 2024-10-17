/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TrxOrderItemDetails from './trx-order-item-details.vue';
import TrxOrderItemService from './trx-order-item.service';
import AlertService from '@/shared/alert/alert.service';

type TrxOrderItemDetailsComponentType = InstanceType<typeof TrxOrderItemDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxOrderItemSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('TrxOrderItem Management Detail Component', () => {
    let trxOrderItemServiceStub: SinonStubbedInstance<TrxOrderItemService>;
    let mountOptions: MountingOptions<TrxOrderItemDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      trxOrderItemServiceStub = sinon.createStubInstance<TrxOrderItemService>(TrxOrderItemService);

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
          trxOrderItemService: () => trxOrderItemServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxOrderItemServiceStub.find.resolves(trxOrderItemSample);
        route = {
          params: {
            trxOrderItemId: '' + 123,
          },
        };
        const wrapper = shallowMount(TrxOrderItemDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.trxOrderItem).toMatchObject(trxOrderItemSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxOrderItemServiceStub.find.resolves(trxOrderItemSample);
        const wrapper = shallowMount(TrxOrderItemDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
