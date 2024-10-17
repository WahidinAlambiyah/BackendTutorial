/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import TrxOrderHistoryUpdate from './trx-order-history-update.vue';
import TrxOrderHistoryService from './trx-order-history.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type TrxOrderHistoryUpdateComponentType = InstanceType<typeof TrxOrderHistoryUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxOrderHistorySample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<TrxOrderHistoryUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('TrxOrderHistory Management Update Component', () => {
    let comp: TrxOrderHistoryUpdateComponentType;
    let trxOrderHistoryServiceStub: SinonStubbedInstance<TrxOrderHistoryService>;

    beforeEach(() => {
      route = {};
      trxOrderHistoryServiceStub = sinon.createStubInstance<TrxOrderHistoryService>(TrxOrderHistoryService);
      trxOrderHistoryServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          trxOrderHistoryService: () => trxOrderHistoryServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(TrxOrderHistoryUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(TrxOrderHistoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxOrderHistory = trxOrderHistorySample;
        trxOrderHistoryServiceStub.update.resolves(trxOrderHistorySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxOrderHistoryServiceStub.update.calledWith(trxOrderHistorySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        trxOrderHistoryServiceStub.create.resolves(entity);
        const wrapper = shallowMount(TrxOrderHistoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxOrderHistory = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxOrderHistoryServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        trxOrderHistoryServiceStub.find.resolves(trxOrderHistorySample);
        trxOrderHistoryServiceStub.retrieve.resolves([trxOrderHistorySample]);

        // WHEN
        route = {
          params: {
            trxOrderHistoryId: '' + trxOrderHistorySample.id,
          },
        };
        const wrapper = shallowMount(TrxOrderHistoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.trxOrderHistory).toMatchObject(trxOrderHistorySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxOrderHistoryServiceStub.find.resolves(trxOrderHistorySample);
        const wrapper = shallowMount(TrxOrderHistoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
