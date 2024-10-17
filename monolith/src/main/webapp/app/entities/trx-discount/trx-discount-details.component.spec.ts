/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TrxDiscountDetails from './trx-discount-details.vue';
import TrxDiscountService from './trx-discount.service';
import AlertService from '@/shared/alert/alert.service';

type TrxDiscountDetailsComponentType = InstanceType<typeof TrxDiscountDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxDiscountSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('TrxDiscount Management Detail Component', () => {
    let trxDiscountServiceStub: SinonStubbedInstance<TrxDiscountService>;
    let mountOptions: MountingOptions<TrxDiscountDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      trxDiscountServiceStub = sinon.createStubInstance<TrxDiscountService>(TrxDiscountService);

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
          trxDiscountService: () => trxDiscountServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxDiscountServiceStub.find.resolves(trxDiscountSample);
        route = {
          params: {
            trxDiscountId: '' + 123,
          },
        };
        const wrapper = shallowMount(TrxDiscountDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.trxDiscount).toMatchObject(trxDiscountSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxDiscountServiceStub.find.resolves(trxDiscountSample);
        const wrapper = shallowMount(TrxDiscountDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
