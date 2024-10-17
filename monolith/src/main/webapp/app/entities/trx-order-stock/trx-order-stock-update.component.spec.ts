/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import TrxOrderStockUpdate from './trx-order-stock-update.vue';
import TrxOrderStockService from './trx-order-stock.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import MstSupplierService from '@/entities/mst-supplier/mst-supplier.service';

type TrxOrderStockUpdateComponentType = InstanceType<typeof TrxOrderStockUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxOrderStockSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<TrxOrderStockUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('TrxOrderStock Management Update Component', () => {
    let comp: TrxOrderStockUpdateComponentType;
    let trxOrderStockServiceStub: SinonStubbedInstance<TrxOrderStockService>;

    beforeEach(() => {
      route = {};
      trxOrderStockServiceStub = sinon.createStubInstance<TrxOrderStockService>(TrxOrderStockService);
      trxOrderStockServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          trxOrderStockService: () => trxOrderStockServiceStub,
          mstSupplierService: () =>
            sinon.createStubInstance<MstSupplierService>(MstSupplierService, {
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
        const wrapper = shallowMount(TrxOrderStockUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(TrxOrderStockUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxOrderStock = trxOrderStockSample;
        trxOrderStockServiceStub.update.resolves(trxOrderStockSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxOrderStockServiceStub.update.calledWith(trxOrderStockSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        trxOrderStockServiceStub.create.resolves(entity);
        const wrapper = shallowMount(TrxOrderStockUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxOrderStock = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxOrderStockServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        trxOrderStockServiceStub.find.resolves(trxOrderStockSample);
        trxOrderStockServiceStub.retrieve.resolves([trxOrderStockSample]);

        // WHEN
        route = {
          params: {
            trxOrderStockId: '' + trxOrderStockSample.id,
          },
        };
        const wrapper = shallowMount(TrxOrderStockUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.trxOrderStock).toMatchObject(trxOrderStockSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxOrderStockServiceStub.find.resolves(trxOrderStockSample);
        const wrapper = shallowMount(TrxOrderStockUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
