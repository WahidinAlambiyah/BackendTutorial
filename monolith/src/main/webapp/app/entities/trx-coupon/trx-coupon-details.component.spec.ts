/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TrxCouponDetails from './trx-coupon-details.vue';
import TrxCouponService from './trx-coupon.service';
import AlertService from '@/shared/alert/alert.service';

type TrxCouponDetailsComponentType = InstanceType<typeof TrxCouponDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxCouponSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('TrxCoupon Management Detail Component', () => {
    let trxCouponServiceStub: SinonStubbedInstance<TrxCouponService>;
    let mountOptions: MountingOptions<TrxCouponDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      trxCouponServiceStub = sinon.createStubInstance<TrxCouponService>(TrxCouponService);

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
          trxCouponService: () => trxCouponServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxCouponServiceStub.find.resolves(trxCouponSample);
        route = {
          params: {
            trxCouponId: '' + 123,
          },
        };
        const wrapper = shallowMount(TrxCouponDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.trxCoupon).toMatchObject(trxCouponSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxCouponServiceStub.find.resolves(trxCouponSample);
        const wrapper = shallowMount(TrxCouponDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
