/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import TrxDeliveryUpdate from './trx-delivery-update.vue';
import TrxDeliveryService from './trx-delivery.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import MstDriverService from '@/entities/mst-driver/mst-driver.service';
import TrxOrderService from '@/entities/trx-order/trx-order.service';

type TrxDeliveryUpdateComponentType = InstanceType<typeof TrxDeliveryUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxDeliverySample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<TrxDeliveryUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('TrxDelivery Management Update Component', () => {
    let comp: TrxDeliveryUpdateComponentType;
    let trxDeliveryServiceStub: SinonStubbedInstance<TrxDeliveryService>;

    beforeEach(() => {
      route = {};
      trxDeliveryServiceStub = sinon.createStubInstance<TrxDeliveryService>(TrxDeliveryService);
      trxDeliveryServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          trxDeliveryService: () => trxDeliveryServiceStub,
          mstDriverService: () =>
            sinon.createStubInstance<MstDriverService>(MstDriverService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          trxOrderService: () =>
            sinon.createStubInstance<TrxOrderService>(TrxOrderService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(TrxDeliveryUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(TrxDeliveryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxDelivery = trxDeliverySample;
        trxDeliveryServiceStub.update.resolves(trxDeliverySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxDeliveryServiceStub.update.calledWith(trxDeliverySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        trxDeliveryServiceStub.create.resolves(entity);
        const wrapper = shallowMount(TrxDeliveryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxDelivery = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxDeliveryServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        trxDeliveryServiceStub.find.resolves(trxDeliverySample);
        trxDeliveryServiceStub.retrieve.resolves([trxDeliverySample]);

        // WHEN
        route = {
          params: {
            trxDeliveryId: '' + trxDeliverySample.id,
          },
        };
        const wrapper = shallowMount(TrxDeliveryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.trxDelivery).toMatchObject(trxDeliverySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxDeliveryServiceStub.find.resolves(trxDeliverySample);
        const wrapper = shallowMount(TrxDeliveryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
