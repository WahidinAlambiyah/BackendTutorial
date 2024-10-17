/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import TrxDiscountUpdate from './trx-discount-update.vue';
import TrxDiscountService from './trx-discount.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type TrxDiscountUpdateComponentType = InstanceType<typeof TrxDiscountUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxDiscountSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<TrxDiscountUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('TrxDiscount Management Update Component', () => {
    let comp: TrxDiscountUpdateComponentType;
    let trxDiscountServiceStub: SinonStubbedInstance<TrxDiscountService>;

    beforeEach(() => {
      route = {};
      trxDiscountServiceStub = sinon.createStubInstance<TrxDiscountService>(TrxDiscountService);
      trxDiscountServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          trxDiscountService: () => trxDiscountServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(TrxDiscountUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(TrxDiscountUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxDiscount = trxDiscountSample;
        trxDiscountServiceStub.update.resolves(trxDiscountSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxDiscountServiceStub.update.calledWith(trxDiscountSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        trxDiscountServiceStub.create.resolves(entity);
        const wrapper = shallowMount(TrxDiscountUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxDiscount = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxDiscountServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        trxDiscountServiceStub.find.resolves(trxDiscountSample);
        trxDiscountServiceStub.retrieve.resolves([trxDiscountSample]);

        // WHEN
        route = {
          params: {
            trxDiscountId: '' + trxDiscountSample.id,
          },
        };
        const wrapper = shallowMount(TrxDiscountUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.trxDiscount).toMatchObject(trxDiscountSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxDiscountServiceStub.find.resolves(trxDiscountSample);
        const wrapper = shallowMount(TrxDiscountUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
