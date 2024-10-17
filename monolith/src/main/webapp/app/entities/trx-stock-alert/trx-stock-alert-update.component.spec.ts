/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TrxStockAlertUpdate from './trx-stock-alert-update.vue';
import TrxStockAlertService from './trx-stock-alert.service';
import AlertService from '@/shared/alert/alert.service';

type TrxStockAlertUpdateComponentType = InstanceType<typeof TrxStockAlertUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxStockAlertSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<TrxStockAlertUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('TrxStockAlert Management Update Component', () => {
    let comp: TrxStockAlertUpdateComponentType;
    let trxStockAlertServiceStub: SinonStubbedInstance<TrxStockAlertService>;

    beforeEach(() => {
      route = {};
      trxStockAlertServiceStub = sinon.createStubInstance<TrxStockAlertService>(TrxStockAlertService);
      trxStockAlertServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          trxStockAlertService: () => trxStockAlertServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(TrxStockAlertUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxStockAlert = trxStockAlertSample;
        trxStockAlertServiceStub.update.resolves(trxStockAlertSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxStockAlertServiceStub.update.calledWith(trxStockAlertSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        trxStockAlertServiceStub.create.resolves(entity);
        const wrapper = shallowMount(TrxStockAlertUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxStockAlert = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxStockAlertServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        trxStockAlertServiceStub.find.resolves(trxStockAlertSample);
        trxStockAlertServiceStub.retrieve.resolves([trxStockAlertSample]);

        // WHEN
        route = {
          params: {
            trxStockAlertId: '' + trxStockAlertSample.id,
          },
        };
        const wrapper = shallowMount(TrxStockAlertUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.trxStockAlert).toMatchObject(trxStockAlertSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxStockAlertServiceStub.find.resolves(trxStockAlertSample);
        const wrapper = shallowMount(TrxStockAlertUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
