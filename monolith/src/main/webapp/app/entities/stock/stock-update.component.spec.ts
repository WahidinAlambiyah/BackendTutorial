/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import StockUpdate from './stock-update.vue';
import StockService from './stock.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import MstProductService from '@/entities/mst-product/mst-product.service';

type StockUpdateComponentType = InstanceType<typeof StockUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const stockSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<StockUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Stock Management Update Component', () => {
    let comp: StockUpdateComponentType;
    let stockServiceStub: SinonStubbedInstance<StockService>;

    beforeEach(() => {
      route = {};
      stockServiceStub = sinon.createStubInstance<StockService>(StockService);
      stockServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          stockService: () => stockServiceStub,
          mstProductService: () =>
            sinon.createStubInstance<MstProductService>(MstProductService, {
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
        const wrapper = shallowMount(StockUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(StockUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.stock = stockSample;
        stockServiceStub.update.resolves(stockSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(stockServiceStub.update.calledWith(stockSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        stockServiceStub.create.resolves(entity);
        const wrapper = shallowMount(StockUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.stock = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(stockServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        stockServiceStub.find.resolves(stockSample);
        stockServiceStub.retrieve.resolves([stockSample]);

        // WHEN
        route = {
          params: {
            stockId: '' + stockSample.id,
          },
        };
        const wrapper = shallowMount(StockUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.stock).toMatchObject(stockSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        stockServiceStub.find.resolves(stockSample);
        const wrapper = shallowMount(StockUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
