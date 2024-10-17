/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import TrxProductHistoryUpdate from './trx-product-history-update.vue';
import TrxProductHistoryService from './trx-product-history.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type TrxProductHistoryUpdateComponentType = InstanceType<typeof TrxProductHistoryUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxProductHistorySample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<TrxProductHistoryUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('TrxProductHistory Management Update Component', () => {
    let comp: TrxProductHistoryUpdateComponentType;
    let trxProductHistoryServiceStub: SinonStubbedInstance<TrxProductHistoryService>;

    beforeEach(() => {
      route = {};
      trxProductHistoryServiceStub = sinon.createStubInstance<TrxProductHistoryService>(TrxProductHistoryService);
      trxProductHistoryServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          trxProductHistoryService: () => trxProductHistoryServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(TrxProductHistoryUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(TrxProductHistoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxProductHistory = trxProductHistorySample;
        trxProductHistoryServiceStub.update.resolves(trxProductHistorySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxProductHistoryServiceStub.update.calledWith(trxProductHistorySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        trxProductHistoryServiceStub.create.resolves(entity);
        const wrapper = shallowMount(TrxProductHistoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxProductHistory = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxProductHistoryServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        trxProductHistoryServiceStub.find.resolves(trxProductHistorySample);
        trxProductHistoryServiceStub.retrieve.resolves([trxProductHistorySample]);

        // WHEN
        route = {
          params: {
            trxProductHistoryId: '' + trxProductHistorySample.id,
          },
        };
        const wrapper = shallowMount(TrxProductHistoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.trxProductHistory).toMatchObject(trxProductHistorySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxProductHistoryServiceStub.find.resolves(trxProductHistorySample);
        const wrapper = shallowMount(TrxProductHistoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
