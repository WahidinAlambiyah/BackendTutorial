/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TrxOrderItemUpdate from './trx-order-item-update.vue';
import TrxOrderItemService from './trx-order-item.service';
import AlertService from '@/shared/alert/alert.service';

import TrxOrderService from '@/entities/trx-order/trx-order.service';
import MstProductService from '@/entities/mst-product/mst-product.service';

type TrxOrderItemUpdateComponentType = InstanceType<typeof TrxOrderItemUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxOrderItemSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<TrxOrderItemUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('TrxOrderItem Management Update Component', () => {
    let comp: TrxOrderItemUpdateComponentType;
    let trxOrderItemServiceStub: SinonStubbedInstance<TrxOrderItemService>;

    beforeEach(() => {
      route = {};
      trxOrderItemServiceStub = sinon.createStubInstance<TrxOrderItemService>(TrxOrderItemService);
      trxOrderItemServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          trxOrderItemService: () => trxOrderItemServiceStub,
          trxOrderService: () =>
            sinon.createStubInstance<TrxOrderService>(TrxOrderService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
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

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(TrxOrderItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxOrderItem = trxOrderItemSample;
        trxOrderItemServiceStub.update.resolves(trxOrderItemSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxOrderItemServiceStub.update.calledWith(trxOrderItemSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        trxOrderItemServiceStub.create.resolves(entity);
        const wrapper = shallowMount(TrxOrderItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxOrderItem = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxOrderItemServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        trxOrderItemServiceStub.find.resolves(trxOrderItemSample);
        trxOrderItemServiceStub.retrieve.resolves([trxOrderItemSample]);

        // WHEN
        route = {
          params: {
            trxOrderItemId: '' + trxOrderItemSample.id,
          },
        };
        const wrapper = shallowMount(TrxOrderItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.trxOrderItem).toMatchObject(trxOrderItemSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxOrderItemServiceStub.find.resolves(trxOrderItemSample);
        const wrapper = shallowMount(TrxOrderItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
