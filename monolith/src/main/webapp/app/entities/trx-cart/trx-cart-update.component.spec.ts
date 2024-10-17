/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TrxCartUpdate from './trx-cart-update.vue';
import TrxCartService from './trx-cart.service';
import AlertService from '@/shared/alert/alert.service';

import MstCustomerService from '@/entities/mst-customer/mst-customer.service';

type TrxCartUpdateComponentType = InstanceType<typeof TrxCartUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxCartSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<TrxCartUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('TrxCart Management Update Component', () => {
    let comp: TrxCartUpdateComponentType;
    let trxCartServiceStub: SinonStubbedInstance<TrxCartService>;

    beforeEach(() => {
      route = {};
      trxCartServiceStub = sinon.createStubInstance<TrxCartService>(TrxCartService);
      trxCartServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          trxCartService: () => trxCartServiceStub,
          mstCustomerService: () =>
            sinon.createStubInstance<MstCustomerService>(MstCustomerService, {
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
        const wrapper = shallowMount(TrxCartUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxCart = trxCartSample;
        trxCartServiceStub.update.resolves(trxCartSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxCartServiceStub.update.calledWith(trxCartSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        trxCartServiceStub.create.resolves(entity);
        const wrapper = shallowMount(TrxCartUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxCart = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxCartServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        trxCartServiceStub.find.resolves(trxCartSample);
        trxCartServiceStub.retrieve.resolves([trxCartSample]);

        // WHEN
        route = {
          params: {
            trxCartId: '' + trxCartSample.id,
          },
        };
        const wrapper = shallowMount(TrxCartUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.trxCart).toMatchObject(trxCartSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxCartServiceStub.find.resolves(trxCartSample);
        const wrapper = shallowMount(TrxCartUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
