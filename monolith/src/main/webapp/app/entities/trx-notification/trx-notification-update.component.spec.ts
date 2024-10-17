/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import TrxNotificationUpdate from './trx-notification-update.vue';
import TrxNotificationService from './trx-notification.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import MstCustomerService from '@/entities/mst-customer/mst-customer.service';

type TrxNotificationUpdateComponentType = InstanceType<typeof TrxNotificationUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxNotificationSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<TrxNotificationUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('TrxNotification Management Update Component', () => {
    let comp: TrxNotificationUpdateComponentType;
    let trxNotificationServiceStub: SinonStubbedInstance<TrxNotificationService>;

    beforeEach(() => {
      route = {};
      trxNotificationServiceStub = sinon.createStubInstance<TrxNotificationService>(TrxNotificationService);
      trxNotificationServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          trxNotificationService: () => trxNotificationServiceStub,
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

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(TrxNotificationUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(TrxNotificationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxNotification = trxNotificationSample;
        trxNotificationServiceStub.update.resolves(trxNotificationSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxNotificationServiceStub.update.calledWith(trxNotificationSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        trxNotificationServiceStub.create.resolves(entity);
        const wrapper = shallowMount(TrxNotificationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxNotification = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxNotificationServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        trxNotificationServiceStub.find.resolves(trxNotificationSample);
        trxNotificationServiceStub.retrieve.resolves([trxNotificationSample]);

        // WHEN
        route = {
          params: {
            trxNotificationId: '' + trxNotificationSample.id,
          },
        };
        const wrapper = shallowMount(TrxNotificationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.trxNotification).toMatchObject(trxNotificationSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxNotificationServiceStub.find.resolves(trxNotificationSample);
        const wrapper = shallowMount(TrxNotificationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
