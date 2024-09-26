/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import TrxEventUpdate from './trx-event-update.vue';
import TrxEventService from './trx-event.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import MstServiceService from '@/entities/mst-service/mst-service.service';
import TrxTestimonialService from '@/entities/trx-testimonial/trx-testimonial.service';

type TrxEventUpdateComponentType = InstanceType<typeof TrxEventUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxEventSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<TrxEventUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('TrxEvent Management Update Component', () => {
    let comp: TrxEventUpdateComponentType;
    let trxEventServiceStub: SinonStubbedInstance<TrxEventService>;

    beforeEach(() => {
      route = {};
      trxEventServiceStub = sinon.createStubInstance<TrxEventService>(TrxEventService);
      trxEventServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          trxEventService: () => trxEventServiceStub,
          mstServiceService: () =>
            sinon.createStubInstance<MstServiceService>(MstServiceService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          trxTestimonialService: () =>
            sinon.createStubInstance<TrxTestimonialService>(TrxTestimonialService, {
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
        const wrapper = shallowMount(TrxEventUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(TrxEventUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxEvent = trxEventSample;
        trxEventServiceStub.update.resolves(trxEventSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxEventServiceStub.update.calledWith(trxEventSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        trxEventServiceStub.create.resolves(entity);
        const wrapper = shallowMount(TrxEventUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxEvent = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxEventServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        trxEventServiceStub.find.resolves(trxEventSample);
        trxEventServiceStub.retrieve.resolves([trxEventSample]);

        // WHEN
        route = {
          params: {
            trxEventId: '' + trxEventSample.id,
          },
        };
        const wrapper = shallowMount(TrxEventUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.trxEvent).toMatchObject(trxEventSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxEventServiceStub.find.resolves(trxEventSample);
        const wrapper = shallowMount(TrxEventUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
