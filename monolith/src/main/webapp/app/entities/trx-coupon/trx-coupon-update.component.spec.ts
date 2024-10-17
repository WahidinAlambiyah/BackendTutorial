/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import TrxCouponUpdate from './trx-coupon-update.vue';
import TrxCouponService from './trx-coupon.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type TrxCouponUpdateComponentType = InstanceType<typeof TrxCouponUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxCouponSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<TrxCouponUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('TrxCoupon Management Update Component', () => {
    let comp: TrxCouponUpdateComponentType;
    let trxCouponServiceStub: SinonStubbedInstance<TrxCouponService>;

    beforeEach(() => {
      route = {};
      trxCouponServiceStub = sinon.createStubInstance<TrxCouponService>(TrxCouponService);
      trxCouponServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          trxCouponService: () => trxCouponServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(TrxCouponUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(TrxCouponUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxCoupon = trxCouponSample;
        trxCouponServiceStub.update.resolves(trxCouponSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxCouponServiceStub.update.calledWith(trxCouponSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        trxCouponServiceStub.create.resolves(entity);
        const wrapper = shallowMount(TrxCouponUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxCoupon = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxCouponServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        trxCouponServiceStub.find.resolves(trxCouponSample);
        trxCouponServiceStub.retrieve.resolves([trxCouponSample]);

        // WHEN
        route = {
          params: {
            trxCouponId: '' + trxCouponSample.id,
          },
        };
        const wrapper = shallowMount(TrxCouponUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.trxCoupon).toMatchObject(trxCouponSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxCouponServiceStub.find.resolves(trxCouponSample);
        const wrapper = shallowMount(TrxCouponUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
